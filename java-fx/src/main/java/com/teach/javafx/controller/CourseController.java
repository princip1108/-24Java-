package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CourseController 课程管理控制类 对应 course-panel.fxml
 * 实现课程的增删改查功能
 */
public class CourseController {
    // 查询条件控件
    @FXML
    private TextField courseCodeField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Button queryButton;
    @FXML
    private Button addButton;

    // 表格控件
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map,String> courseCodeColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> typeColumn;
    @FXML
    private TableColumn<Map,FlowPane> operateColumn;

    private List<Map<String,Object>> courseList = new ArrayList<>();  // 课程信息列表数据
    private final ObservableList<Map<String,Object>> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    /**
     * 查询按钮点击事件
     */
    @FXML
    private void onQueryButtonClick(){
        DataRequest req = new DataRequest();

        // 获取查询条件
        String courseCode = courseCodeField.getText();
        String name = nameField.getText();
        String type = typeComboBox.getValue();

        // 只有非空条件才添加到请求中
        if (courseCode != null && !courseCode.trim().isEmpty()) {
            req.add("courseCode", courseCode.trim());
        }
        if (name != null && !name.trim().isEmpty()) {
            req.add("name", name.trim());
        }
        if (type != null && !"全部".equals(type)) {
            req.add("type", type);
        }

        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);
        if(res != null && res.getCode() == 0) {
            courseList = (List<Map<String, Object>>) res.getData();
        } else {
            courseList = new ArrayList<>();
            if (res != null) {
                MessageDialog.showDialog("查询失败：" + res.getMsg());
            }
        }
        setTableViewData();
    }

    /**
     * 添加按钮点击事件
     */
    @FXML
    private void onAddButtonClick(){
        showCourseEditDialog(null);
    }

    /**
     * 设置表格数据
     */
    private void setTableViewData() {
        observableList.clear();
        Map<String,Object> map;
        FlowPane flowPane;
        Button editButton, deleteButton;

        for (int j = 0; j < courseList.size(); j++) {
            map = courseList.get(j);
            flowPane = new FlowPane();
            flowPane.setHgap(10);
            flowPane.setAlignment(Pos.CENTER);

            editButton = new Button("编辑");
            editButton.setId("edit" + j);
            editButton.setOnAction(e -> {
                editItem(((Button)e.getSource()).getId());
            });

            deleteButton = new Button("删除");
            deleteButton.setId("delete" + j);
            deleteButton.setOnAction(e -> {
                deleteItem(((Button)e.getSource()).getId());
            });

            flowPane.getChildren().addAll(editButton, deleteButton);
            map.put("operate", flowPane);
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }
    /**
     * 编辑课程
     */
    public void editItem(String name){
        if(name == null) return;
        int j = Integer.parseInt(name.substring(4));
        Map<String,Object> data = courseList.get(j);
        showCourseEditDialog(data);
    }

    /**
     * 删除课程
     */
    public void deleteItem(String name){
        if(name == null) return;
        int j = Integer.parseInt(name.substring(6));
        Map<String,Object> data = courseList.get(j);

        // 确认删除
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除课程");
        alert.setContentText("确定要删除课程 \"" + data.get("name") + "\" 吗？");

        if (alert.showAndWait().get() == ButtonType.OK) {
            DataRequest req = new DataRequest();
            req.add("courseId", data.get("courseId"));

            DataResponse res = HttpRequestUtil.request("/api/course/courseDelete", req);
            if (res != null && res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                onQueryButtonClick(); // 刷新列表
            } else {
                MessageDialog.showDialog("删除失败：" + (res != null ? res.getMsg() : "网络错误"));
            }
        }
    }

    /**
     * 显示课程编辑对话框
     */
    private void showCourseEditDialog(Map<String, Object> data) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle(data == null ? "添加课程" : "编辑课程");
        dialog.setHeaderText(data == null ? "请输入课程信息" : "修改课程信息");

        // 设置按钮类型
        ButtonType saveButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // 创建表单
        VBox vbox = new VBox(10);

        TextField courseCodeField = new TextField();
        courseCodeField.setPromptText("课序号");
        if (data != null) {
            courseCodeField.setText(CommonMethod.getString(data, "courseCode"));
            courseCodeField.setDisable(true); // 编辑时不允许修改课序号
        }

        TextField nameField = new TextField();
        nameField.setPromptText("课程名称");
        if (data != null) {
            nameField.setText(CommonMethod.getString(data, "name"));
        }

        TextField creditField = new TextField();
        creditField.setPromptText("学分");
        if (data != null) {
            creditField.setText(CommonMethod.getString(data, "credit"));
        }

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("必修", "选修");
        if (data != null) {
            typeComboBox.setValue(CommonMethod.getString(data, "type"));
        }

        vbox.getChildren().addAll(
            new Label("课序号:"), courseCodeField,
            new Label("课程名称:"), nameField,
            new Label("学分:"), creditField,
            new Label("课程类型:"), typeComboBox
        );

        dialog.getDialogPane().setContent(vbox);

        // 设置结果转换器
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Map<String, Object> result = new HashMap<>();
                    if (data != null) {
                        result.put("courseId", data.get("courseId"));
                    }
                    result.put("courseCode", courseCodeField.getText());
                    result.put("name", nameField.getText());
                    result.put("credit", Double.parseDouble(creditField.getText()));
                    result.put("type", typeComboBox.getValue());
                    return result;
                } catch (NumberFormatException e) {
                    MessageDialog.showDialog("学分必须是数字！");
                    return null;
                }
            }
            return null;
        });

        // 显示对话框并处理结果
        dialog.showAndWait().ifPresent(result -> {
            DataRequest req = new DataRequest();
            req.add("courseId", result.get("courseId"));
            req.add("courseCode", result.get("courseCode"));
            req.add("name", result.get("name"));
            req.add("credit", result.get("credit"));
            req.add("type", result.get("type"));

            DataResponse res = HttpRequestUtil.request("/api/course/courseSave", req);
            if (res != null && res.getCode() == 0) {
                MessageDialog.showDialog("保存成功！");
                onQueryButtonClick(); // 刷新列表
            } else {
                MessageDialog.showDialog("保存失败：" + (res != null ? res.getMsg() : "网络错误"));
            }
        });
    }

    @FXML
    public void initialize() {
        // 初始化课程类型下拉框
        typeComboBox.getItems().addAll("全部", "必修", "选修");
        typeComboBox.setValue("全部");

        // 设置表格列
        courseCodeColumn.setCellValueFactory(new MapValueFactory<>("courseCode"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));

        // 初始加载数据
        onQueryButtonClick();
    }
}
