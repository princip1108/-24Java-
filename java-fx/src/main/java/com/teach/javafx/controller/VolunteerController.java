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
    private ArrayList<Map> VolList = new ArrayList();          // 原始成绩数据（后端返回）
    private ObservableList<Map> observableList = FXCollections.observableArrayList(); // 表格可观察数据源

    @FXML
    private ComboBox<OptionItem> studentComboBox;      // 学生筛选下拉框
    @FXML
    private ComboBox<OptionItem> activityComboBox;
    @FXML
    private ComboBox<OptionItem> volNameComboBox;
    private List<OptionItem> studentList;              // 学生选项数据
    private List<OptionItem> activityList;

    //--------------------- 对话框控制相关 ---------------------
    private VolEditController volEditController = null; // 活动编辑对话框控制器
    private Stage stage = null;                       // 编辑对话框的舞台
    public List<OptionItem> getStudentList() {
        return studentList;
    }
    @FXML
    private void onQueryButtonClick() {
        // 获取筛选条件：选中的学生和活动
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
        // 处理响应结果
        if (res != null && res.getCode() == 0) {
            VolList = (ArrayList<Map>) res.getData(); // 更新原始数据
        }
        setTableViewData(); // 刷新表格显示
    }

    /**
     * 刷新表格数据
     * 功能：遍历原始数据，动态添加编辑按钮，绑定到可观察列表
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < VolList.size(); j++) {
            Map map = VolList.get(j);
            Button editButton = new Button("编辑");
            editButton.setId("edit" + j); // 唯一标识按钮（edit+行号）
            editButton.setOnAction(e -> editItem(((Button) e.getSource()).getId()));
            map.put("edit", editButton); // 将按钮存入数据Map的"edit"键
            observableList.add(map); // 添加至可观察列表
        }
        dataTableView.setItems(observableList); // 绑定表格数据源
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
    /**
     * 初始化编辑对话框（懒加载模式）
     */
    private void initDialog() {
        if (stage != null) return; // 避免重复初始化
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("vol-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage()); // 设置父窗口
            stage.initModality(Modality.NONE); // 非模态对话框
            stage.setAlwaysOnTop(true); // 保持置顶
            stage.setScene(scene);
            stage.setTitle("志愿活动录入对话框！");
            stage.setOnCloseRequest(event -> MainApplication.setCanClose(true)); // 恢复主窗口关闭权限

            // 获取对话框控制器并建立双向关联
            volEditController = (VolEditController) fxmlLoader.getController();
            volEditController.setVolunteerController(this);
            volEditController.init(); // 初始化对话框组件
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理对话框关闭事件
     * @param cmd 操作命令（"ok"表示保存）
     * @param data 编辑后的数据
     */
    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true); // 允许主窗口关闭
        stage.close();
        if (!"ok".equals(cmd)) return; // 非保存操作直接退出

        // 数据校验
        Integer personId = CommonMethod.getInteger(data, "personId");
        if (personId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }

        // 构建保存请求
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("volunteerId", CommonMethod.getInteger(data, "volunteerId"));
        req.add("activityName", CommonMethod.getString(data, "activityName"));
        req.add("timeLong", CommonMethod.getInteger(data, "timeLong"));
        DataResponse res = HttpRequestUtil.request("/api/volunteer/volunteerSave", req);

        // 处理保存结果
        if (res != null && res.getCode() == 0) {
            onQueryButtonClick(); // 保存成功后刷新表格
        }
    }
    @FXML
    private void onAddButtonClick() {
        initDialog();
        volEditController.showDialog(null); // 传递null表示新增操作
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