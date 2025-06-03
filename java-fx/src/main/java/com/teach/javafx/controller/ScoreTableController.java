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

public class ScoreTableController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> scoreValueColumn;
    @FXML
    private TableColumn<Map, String> gradeColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;

    //--------------------- 数据存储相关 ---------------------
    private ArrayList<Map> scoreList = new ArrayList();          // 原始成绩数据（后端返回）
    private ObservableList<Map> observableList = FXCollections.observableArrayList(); // 表格可观察数据源

    @FXML
    private ComboBox<OptionItem> studentComboBox;      // 学生筛选下拉框
    @FXML
    private ComboBox<OptionItem> courseComboBox;       // 课程筛选下拉框
    private List<OptionItem> studentList;              // 学生选项数据
    private List<OptionItem> courseList;              // 课程选项数据

    //--------------------- 对话框控制相关 ---------------------
    private ScoreEditController scoreEditController = null; // 成绩编辑对话框控制器
    private Stage stage = null;                       // 编辑对话框的舞台

    public List<OptionItem> getStudentList() {
        return studentList;
    }

    public List<OptionItem> getCourseList() {
        return courseList;
    }

    @FXML
    private void onQueryButtonClick() {
        // 获取筛选条件：选中的学生和课程
        Integer studentId = null;
        OptionItem op = studentComboBox.getSelectionModel().getSelectedItem();
        if (op != null && !"0".equals(op.getValue())) {
            studentId = Integer.parseInt(op.getValue());
        }

        Integer courseId = null;
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if (op != null && !"0".equals(op.getValue())) {
            courseId = Integer.parseInt(op.getValue());
        }

        // 执行查询
        queryScoreData(studentId, courseId);
    }

    /**
     * 查询成绩数据的通用方法
     * @param studentId 学生ID，null或0表示不限制学生
     * @param courseId 课程ID，null或0表示不限制课程
     */
    private void queryScoreData(Integer studentId, Integer courseId) {
        // 构建请求对象并发送查询
        DataRequest req = new DataRequest();

        // 只有有效的ID才添加到请求中（大于0的值）
        if (studentId != null && studentId > 0) {
            req.add("studentId", studentId);
        }
        if (courseId != null && courseId > 0) {
            req.add("courseId", courseId);
        }

        DataResponse res = HttpRequestUtil.request("/api/score/getScoreList", req);

        // 处理响应结果
        if (res != null && res.getCode() == 0) {
            scoreList = (ArrayList<Map>) res.getData(); // 更新原始数据
        } else {
            scoreList = new ArrayList<>(); // 查询失败时初始化为空列表
            if (res != null) {
                System.err.println("查询成绩失败: " + res.getMsg());
            }
        }
        setTableViewData(); // 刷新表格显示
    }

    /**
     * 加载所有成绩数据（用于初始化显示）
     */
    private void loadAllScores() {
        // 构建空请求，获取所有成绩
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/score/getScoreList", req);

        // 处理响应结果
        if (res != null && res.getCode() == 0) {
            scoreList = (ArrayList<Map>) res.getData(); // 更新原始数据
        } else {
            scoreList = new ArrayList<>(); // 如果请求失败，初始化为空列表
            if (res != null) {
                System.err.println("加载所有成绩失败: " + res.getMsg());
            }
        }
        setTableViewData(); // 刷新表格显示
    }

    /**
     * 刷新表格数据
     * 功能：遍历原始数据，动态添加编辑按钮，绑定到可观察列表
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < scoreList.size(); j++) {
            Map map = scoreList.get(j);
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
        Map data = scoreList.get(j); // 获取对应行数据
        initDialog(); // 初始化对话框（懒加载）
        scoreEditController.showDialog(data); // 传递数据给编辑控制器
        MainApplication.setCanClose(false); // 禁止主窗口关闭
        stage.showAndWait(); // 模态显示对话框
    }

    @FXML
    public void initialize() {
        // 配置表格列与数据键的映射关系
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        scoreValueColumn.setCellValueFactory(new MapValueFactory<>("scoreValue"));
        gradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));// 绑定编辑按钮列

        // 初始化下拉框选项
        DataRequest req = new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/score/getStudentItemOptionList", req);
        courseList = HttpRequestUtil.requestOptionItemList("/api/score/getCourseItemOptionList", req);

        // 添加默认选项并填充下拉框
        OptionItem item = new OptionItem(null, "0", "请选择");
        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);

        item = new OptionItem(null, "0", "请选择");
        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);

        // 启用表格多选模式并首次加载所有数据
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 使用Platform.runLater确保在界面完全初始化后加载数据
        javafx.application.Platform.runLater(() -> {
            loadAllScores(); // 初始化时加载所有成绩数据
        });
    }

    /**
     * 初始化编辑对话框（懒加载模式）
     */
    private void initDialog() {
        if (stage != null) return; // 避免重复初始化
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("score-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage()); // 设置父窗口
            stage.initModality(Modality.NONE); // 非模态对话框
            stage.setAlwaysOnTop(true); // 保持置顶
            stage.setScene(scene);
            stage.setTitle("成绩编辑对话框");
            stage.setOnCloseRequest(event -> MainApplication.setCanClose(true)); // 恢复主窗口关闭权限

            // 获取对话框控制器并建立双向关联
            scoreEditController = (ScoreEditController) fxmlLoader.getController();
            scoreEditController.setScoreTableController(this);
            scoreEditController.init(); // 初始化对话框组件
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
        Integer studentId = CommonMethod.getInteger(data, "studentId");
        if (studentId == null) {
            MessageDialog.showDialog("请选择学生！");
            return;
        }

        Integer courseId = CommonMethod.getInteger(data, "courseId");
        if (courseId == null) {
            MessageDialog.showDialog("请选择课程！");
            return;
        }

        Double scoreValue = CommonMethod.getDouble(data, "scoreValue");
        if (scoreValue == null) {
            MessageDialog.showDialog("请输入成绩！");
            return;
        }

        // 构建保存请求
        DataRequest req = new DataRequest();
        req.add("scoreId", CommonMethod.getInteger(data, "scoreId"));
        req.add("studentId", studentId);
        req.add("courseId", courseId);
        req.add("scoreValue", scoreValue);

        // 根据scoreId判断是新增还是更新
        String apiPath = (data.get("scoreId") == null) ?
                "/api/score/scoreSave" : "/api/score/scoreUpdate";

        DataResponse res = HttpRequestUtil.request(apiPath, req);

        // 处理保存结果
        if (res != null && res.getCode() == 0) {
            loadAllScores(); // 保存成功后重新加载所有数据
        } else if (res != null) {
            MessageDialog.showDialog(res.getMsg()); // 显示错误信息
        }
    }

    @FXML
    private void onAddButtonClick() {
        initDialog();
        scoreEditController.showDialog(null); // 传递null表示新增操作
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    private void onEditButtonClick() {
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if (data == null) {
            MessageDialog.showDialog("请选择要修改的成绩记录！");
            return;
        }
        initDialog();
        scoreEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    private void onDeleteButtonClick() {
        Map<String, Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("请选择要删除的成绩记录！");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除这条成绩记录吗?");
        if (ret != MessageDialog.CHOICE_YES) return;

        // 发送删除请求
        Integer scoreId = CommonMethod.getInteger(form, "scoreId");
        DataRequest req = new DataRequest();
        req.add("scoreId", scoreId);
        DataResponse res = HttpRequestUtil.request("/api/score/scoreDelete", req);

        // 处理结果
        if (res != null && res.getCode() == 0) {
            loadAllScores(); // 删除成功后重新加载所有数据
        } else if (res != null) {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}