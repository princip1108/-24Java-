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

public class CompetitionController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> competitionNameColumn;
    @FXML
    private TableColumn<Map, String> subjectColumn;
    @FXML
    private TableColumn<Map, String> levelColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;
    //--------------------- 数据存储相关 ---------------------
    private ArrayList<Map> ComList = new ArrayList();          // 原始成绩数据（后端返回）
    private ObservableList<Map> observableList = FXCollections.observableArrayList(); // 表格可观察数据源

    @FXML
    private ComboBox<OptionItem> studentComboBox;      // 学生筛选下拉框
    @FXML
    private ComboBox<OptionItem> subjectComboBox;
    @FXML
    private ComboBox<OptionItem> levelComboBox;
    private List<OptionItem> studentList;              // 学生选项数据
    private List<OptionItem> subjectList;
    private List<OptionItem> levelList;

    //--------------------- 对话框控制相关 ---------------------
    private ComEditController comEditController = null; // 活动编辑对话框控制器
    private Stage stage = null;                       // 编辑对话框的舞台
    public List<OptionItem> getStudentList() {return studentList;}
    public List<OptionItem> getSubjectList() {return subjectList;}
    public List<OptionItem> getLevelList() {return levelList;}
    @FXML
    private void onQueryButtonClick() {
        // 获取筛选条件：选中的学生和活动
        Integer personId = 0;
        Integer subjectId = 0;
        Integer level = 0;
        OptionItem op = studentComboBox.getSelectionModel().getSelectedItem();
        if (op != null) personId = Integer.parseInt(op.getValue());
        op = subjectComboBox.getSelectionModel().getSelectedItem();
        if (op != null) subjectId = Integer.parseInt(op.getValue());
        op = levelComboBox.getSelectionModel().getSelectedItem();
        if (op != null) level = Integer.parseInt(op.getValue());
        // 构建请求对象并发送查询
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("subjectId", subjectId);
        req.add("level", level);
        DataResponse res = HttpRequestUtil.request("/api/competition/getCompetitionList", req);
        if(res != null && res.getCode()== 0) {
            ComList = (ArrayList<Map>)res.getData();
        }
        setTableViewData(); // 刷新表格显示
    }


    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < ComList.size(); j++) {
            Map map = ComList.get(j);
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
        Map data = ComList.get(j); // 获取对应行数据
        initDialog(); // 初始化对话框（懒加载）
        comEditController.showDialog(data); // 传递数据给编辑控制器
        MainApplication.setCanClose(false); // 禁止主窗口关闭
        stage.showAndWait(); // 模态显示对话框
    }
    @FXML
    public void initialize() {
        // 配置表格列与数据键的映射关系
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        competitionNameColumn.setCellValueFactory(new MapValueFactory<>("competitionName"));
        subjectColumn.setCellValueFactory(new MapValueFactory<>("subjectName"));
        levelColumn.setCellValueFactory(new MapValueFactory<>("level"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));// 绑定编辑按钮列

        // 初始化下拉框选项
        DataRequest req = new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/competition/getStudentItemOptionList", req);
        subjectList = HttpRequestUtil.requestOptionItemList("/api/competition/getSubjectItemOptionList", req);
        levelList = HttpRequestUtil.requestOptionItemList("/api/competition/getLevelItemOptionList", req);

        // 添加默认选项并填充下拉框
        OptionItem item = new OptionItem(null, "0", "请选择");
        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);
        subjectComboBox.getItems().addAll(item);
        subjectComboBox.getItems().addAll(subjectList);
        levelComboBox.getItems().addAll(item);
        levelComboBox.getItems().addAll(levelList);

        // 启用表格多选模式并首次加载数据
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }
    /**
     * 初始化编辑对话框
     */
    private void initDialog() {
        if (stage != null) return; // 避免重复初始化
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("com-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 260, 260);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage()); // 设置父窗口
            stage.initModality(Modality.NONE); // 非模态对话框
            stage.setAlwaysOnTop(true); // 保持置顶
            stage.setScene(scene);
            stage.setTitle("科研竞赛录入对话框！");
            stage.setOnCloseRequest(event -> MainApplication.setCanClose(true)); // 恢复主窗口关闭权限

            // 获取对话框控制器并建立双向关联
            comEditController = (ComEditController) fxmlLoader.getController();
            comEditController.setCompetitionController(this);
            comEditController.init(); // 初始化对话框组件
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


        Integer personId = CommonMethod.getInteger(data, "personId");
        if (personId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }
        Integer subjectId = CommonMethod.getInteger(data, "subjectId");
        if (subjectId == null) {
            MessageDialog.showDialog("没有选中学科不能添加保存！");
            return;
        }
        Integer level = CommonMethod.getInteger(data, "level");
        if (level == null) {
            MessageDialog.showDialog("没有选中等级不能添加保存！");
            return;
        }

        // 构建保存请求
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("subjectId", subjectId);
        req.add("competitionId", CommonMethod.getInteger(data, "competitionId"));
        req.add("competitionName", CommonMethod.getString(data, "competitionName"));
        req.add("level", CommonMethod.getInteger(data, "level"));
        DataResponse res = HttpRequestUtil.request("/api/competition/competitionSave", req);

        // 处理保存结果
        if (res != null && res.getCode() == 0) {
            onQueryButtonClick(); // 保存成功后刷新表格
        }
    }
    @FXML
    private void onAddButtonClick() {
        initDialog();
        comEditController.showDialog(null); // 传递null表示新增操作
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
        comEditController.showDialog(data);
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
        Integer scoreId = CommonMethod.getInteger(form, "competitionId");
        DataRequest req = new DataRequest();
        req.add("competitionId", scoreId);
        DataResponse res = HttpRequestUtil.request("/api/competition/competitionDelete", req);

        // 处理结果
        if (res.getCode() == 0) {
            onQueryButtonClick(); // 删除成功后刷新
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
