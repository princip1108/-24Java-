package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*志愿活动增删改查（学生姓名、学号、可以记录时长活动类型、环保、社区服务等）
* */
public class VolunteerController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> activityNameColumn;
    @FXML
    private TableColumn<Map, String> timeLongColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;
    //--------------------- 数据存储相关 ---------------------
    private ArrayList<Map> VolList = new ArrayList();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<OptionItem> studentComboBox;
    @FXML
    private ComboBox<OptionItem> activityComboBox;
    @FXML
    private ComboBox<OptionItem> volNameComboBox;
    private List<OptionItem> studentList;              // 学生选项数据
    private List<OptionItem> activityList;

    //--------------------- 对话框控制相关 ---------------------
    private VolEditController volEditController = null;
    private Stage stage = null;
    public List<OptionItem> getStudentList() {
        return studentList;
    }
    @FXML
    private void onQueryButtonClick() {

        Integer personId = 0;
        OptionItem op = studentComboBox.getSelectionModel().getSelectedItem();
        if (op != null) personId = Integer.parseInt(op.getValue());
        Integer volunteerId = 0;
        op = activityComboBox.getSelectionModel().getSelectedItem();
        if (op != null) volunteerId = Integer.parseInt(op.getValue());
        // 构建请求对象并发送查询
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("volunteerId", volunteerId);
        DataResponse res = HttpRequestUtil.request("/api/volunteer/getVolunteerList", req);
        activityList = HttpRequestUtil.requestOptionItemList("/api/volunteer/getActivityItemOptionList",new DataRequest());
        activityComboBox.getItems().clear();
        activityComboBox.getItems().addAll(new OptionItem(null, "0", "请选择"));
        activityComboBox.getItems().addAll(activityList);

        if (res != null && res.getCode() == 0) {
            VolList = (ArrayList<Map>) res.getData(); // 更新原始数据
        }
        setTableViewData();
    }


    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < VolList.size(); j++) {
            Map map = VolList.get(j);
            Button editButton = new Button("编辑");
            editButton.setId("edit" + j);
            editButton.setOnAction(e -> editItem(((Button) e.getSource()).getId()));
            map.put("edit", editButton);
            observableList.add(map);
        }
        dataTableView.setItems(observableList);
    }
    /**
     * 打开编辑对话框
     * @param name 按钮ID（格式："edit+行号"）
     */
    public void editItem(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(4)); // 提取行号
        Map data = VolList.get(j); // 获取对应行数据
        initDialog(); // 初始化对话框（懒加载）
        volEditController.showDialog(data); // 传递数据给编辑控制器
        MainApplication.setCanClose(false); // 禁止主窗口关闭
        stage.showAndWait(); // 模态显示对话框
    }
    @FXML
    public void initialize() {
        // 配置表格列与数据键的映射关系
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        activityNameColumn.setCellValueFactory(new MapValueFactory<>("activityName"));
        timeLongColumn.setCellValueFactory(new MapValueFactory<>("timeLong"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));// 绑定编辑按钮列

        // 初始化下拉框选项
        DataRequest req = new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/volunteer/getStudentItemOptionList", req);
        activityList = HttpRequestUtil.requestOptionItemList("/api/volunteer/getActivityItemOptionList", req);

        // 添加默认选项并填充下拉框
        OptionItem item = new OptionItem(null, "0", "请选择");
        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);
        item = new OptionItem(null, "0", "请选择");
        activityComboBox.getItems().addAll(item);
        activityComboBox.getItems().addAll(activityList);

        // 启用表格多选模式并首次加载数据
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    private void initDialog() {
        if (stage != null) return;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("vol-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("志愿活动录入对话框！");
            stage.setOnCloseRequest(event -> MainApplication.setCanClose(true)); // 恢复主窗口关闭权限


            volEditController = (VolEditController) fxmlLoader.getController();
            volEditController.setVolunteerController(this);
            volEditController.init(); // 初始化对话框组件
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();
        if (!"ok".equals(cmd)) return;


        Integer personId = CommonMethod.getInteger(data, "personId");
        if (personId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }


        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("volunteerId", CommonMethod.getInteger(data, "volunteerId"));
        req.add("activityName", CommonMethod.getString(data, "activityName"));
        req.add("timeLong", CommonMethod.getInteger(data, "timeLong"));
        DataResponse res = HttpRequestUtil.request("/api/volunteer/volunteerSave", req);


        if (res != null && res.getCode() == 0) {
            onQueryButtonClick();
        }
    }
    @FXML
    private void onAddButtonClick() {
        initDialog();
        volEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    private void onEditButtonClick() {
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if (data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        volEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    private void onDeleteButtonClick() {
        Map<String, Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) return;

        // 发送删除请求
        Integer scoreId = CommonMethod.getInteger(form, "volunteerId");
        DataRequest req = new DataRequest();
        req.add("volunteerId", scoreId);
        DataResponse res = HttpRequestUtil.request("/api/volunteer/volunteerDelete", req);

        // 处理结果
        if (res.getCode() == 0) {
            onQueryButtonClick(); // 删除成功后刷新
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}