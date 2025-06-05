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
    private ArrayList<Map> scoreList = new ArrayList();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<OptionItem> studentComboBox;      // 学生下拉框
    @FXML
    private ComboBox<OptionItem> courseComboBox;       // 课程下拉框
    private List<OptionItem> studentList;
    private List<OptionItem> courseList;

    //--------------------- 对话框控制相关 ---------------------
    private ScoreEditController scoreEditController = null;
    private Stage stage = null;

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


    private void loadAllScores() {

        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/score/getScoreList", req);

        if (res != null && res.getCode() == 0) {
            scoreList = (ArrayList<Map>) res.getData(); // 更新原始数据
        } else {
            scoreList = new ArrayList<>(); // 如果请求失败，初始化为空列表
            if (res != null) {
                System.err.println("加载所有成绩失败: " + res.getMsg());
            }
        }
        setTableViewData(); // 刷新
    }


    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < scoreList.size(); j++) {
            Map map = scoreList.get(j);
            Button editButton = new Button("编辑");
            editButton.setId("edit" + j);
            editButton.setOnAction(e -> editItem(((Button) e.getSource()).getId()));
            map.put("edit", editButton);
            observableList.add(map);
        }
        dataTableView.setItems(observableList);
    }


    public void editItem(String name) {
        if (name == null) return;
        int j = Integer.parseInt(name.substring(4)); // 提取行号
        Map data = scoreList.get(j);
        initDialog();
        scoreEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    public void initialize() {

        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        scoreValueColumn.setCellValueFactory(new MapValueFactory<>("scoreValue"));
        gradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));// 绑定编辑按钮列


        DataRequest req = new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/score/getStudentItemOptionList", req);
        courseList = HttpRequestUtil.requestOptionItemList("/api/score/getCourseItemOptionList", req);

        OptionItem item = new OptionItem(null, "0", "请选择");
        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);

        item = new OptionItem(null, "0", "请选择");
        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);


        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        javafx.application.Platform.runLater(() -> {
            loadAllScores();
        });
    }


    private void initDialog() {
        if (stage != null) return;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("score-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("成绩编辑对话框");
            stage.setOnCloseRequest(event -> MainApplication.setCanClose(true));


            scoreEditController = (ScoreEditController) fxmlLoader.getController();
            scoreEditController.setScoreTableController(this);
            scoreEditController.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();
        if (!"ok".equals(cmd)) return;


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


        DataRequest req = new DataRequest();
        req.add("scoreId", CommonMethod.getInteger(data, "scoreId"));
        req.add("studentId", studentId);
        req.add("courseId", courseId);
        req.add("scoreValue", scoreValue);

        String apiPath = (data.get("scoreId") == null) ?
                "/api/score/scoreSave" : "/api/score/scoreUpdate";

        DataResponse res = HttpRequestUtil.request(apiPath, req);


        if (res != null && res.getCode() == 0) {
            loadAllScores();
        } else if (res != null) {
            MessageDialog.showDialog(res.getMsg());
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


        Integer scoreId = CommonMethod.getInteger(form, "scoreId");
        DataRequest req = new DataRequest();
        req.add("scoreId", scoreId);
        DataResponse res = HttpRequestUtil.request("/api/score/scoreDelete", req);


        if (res != null && res.getCode() == 0) {
            loadAllScores();
        } else if (res != null) {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}