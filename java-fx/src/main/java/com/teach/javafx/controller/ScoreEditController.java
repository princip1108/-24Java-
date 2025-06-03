package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    @FXML
    private ComboBox<OptionItem> courseComboBox;
    @FXML
    private TextField scoreField;
    @FXML
    private TextField gradeField;

    private List<OptionItem> studentList;
    private List<OptionItem> courseList;
    private ScoreTableController scoreTableController = null;
    private Integer scoreId = null;

    @FXML
    public void initialize() {
        // 成绩字段设置为可编辑，等级字段设置为不可编辑
        scoreField.setEditable(true);
        gradeField.setEditable(false);
    }

    @FXML
    public void okButtonClick(){
        Map<String,Object> data = new HashMap<>();

        // 获取学生选择
        OptionItem studentOp = studentComboBox.getSelectionModel().getSelectedItem();
        if (studentOp != null) {
            data.put("studentId", Integer.parseInt(studentOp.getValue()));
        }

        // 获取课程选择
        OptionItem courseOp = courseComboBox.getSelectionModel().getSelectedItem();
        if (courseOp != null) {
            data.put("courseId", Integer.parseInt(courseOp.getValue()));
        }

        // 获取成绩输入
        data.put("scoreValue", scoreField.getText());
        data.put("scoreId", scoreId);

        // 关闭对话框并传递数据
        scoreTableController.doClose("ok", data);
    }

    @FXML
    public void cancelButtonClick(){
        scoreTableController.doClose("cancel", null);
    }

    public void setScoreTableController(ScoreTableController scoreTableController) {
        this.scoreTableController = scoreTableController;
    }

    public void init(){
        // 初始化下拉框选项
        studentList = scoreTableController.getStudentList();
        studentComboBox.getItems().addAll(studentList);

        courseList = scoreTableController.getCourseList();
        courseComboBox.getItems().addAll(courseList);
    }

    public void showDialog(Map data){
        if (data == null) {
            // 新增模式
            scoreId = null;
            studentComboBox.getSelectionModel().select(-1);
            courseComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            courseComboBox.setDisable(false);
            scoreField.setText("");
            gradeField.setText("");
        } else {
            // 编辑模式
            scoreId = CommonMethod.getInteger(data, "scoreId");

            // 设置学生选择
            String studentId = CommonMethod.getString(data, "studentId");
            int index = CommonMethod.getOptionItemIndexByValue(studentList, studentId);
            studentComboBox.getSelectionModel().select(index);
            studentComboBox.setDisable(true); // 编辑时禁止修改学生

            // 设置课程选择
            String courseId = CommonMethod.getString(data, "courseId");
            index = CommonMethod.getOptionItemIndexByValue(courseList, courseId);
            courseComboBox.getSelectionModel().select(index);
            courseComboBox.setDisable(true); // 编辑时禁止修改课程

            // 设置成绩和等级
            scoreField.setText(CommonMethod.getString(data, "scoreValue"));
            gradeField.setText(CommonMethod.getString(data, "grade"));
        }
    }

    // 成绩字段输入变化时自动计算等级
    @FXML
    private void onScoreChanged() {
        String scoreStr = scoreField.getText();
        if (scoreStr == null || scoreStr.isEmpty()) {
            gradeField.setText("");
            return;
        }

        try {
            double score = Double.parseDouble(scoreStr);
            if (score >= 90) {
                gradeField.setText("A");
            } else if (score >= 80) {
                gradeField.setText("B");
            } else if (score >= 70) {
                gradeField.setText("C");
            } else if (score >= 60) {
                gradeField.setText("D");
            } else {
                gradeField.setText("F");
            }
        } catch (NumberFormatException e) {
            gradeField.setText("无效成绩");
        }
    }
}