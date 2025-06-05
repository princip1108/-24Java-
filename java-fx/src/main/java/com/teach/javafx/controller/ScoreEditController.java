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

    // 保存当前编辑的学生和课程ID（用于编辑模式）
    private Integer currentStudentId = null;
    private Integer currentCourseId = null;

    @FXML
    public void initialize() {
        // 成绩字段设置为可编辑，等级字段设置为不可编辑
        scoreField.setEditable(true);
        gradeField.setEditable(false);
    }

    @FXML
    public void okButtonClick(){
        Map<String,Object> data = new HashMap<>();


        if (currentStudentId != null) {
            data.put("studentId", currentStudentId);
        } else {
            OptionItem studentOp = studentComboBox.getSelectionModel().getSelectedItem();
            if (studentOp != null && !"0".equals(studentOp.getValue())) {
                data.put("studentId", Integer.parseInt(studentOp.getValue()));
            }
        }


        if (currentCourseId != null) {
            data.put("courseId", currentCourseId);
        } else {
            OptionItem courseOp = courseComboBox.getSelectionModel().getSelectedItem();
            if (courseOp != null && !"0".equals(courseOp.getValue())) {
                data.put("courseId", Integer.parseInt(courseOp.getValue()));
            }
        }


        data.put("scoreValue", scoreField.getText());
        data.put("scoreId", scoreId);

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

        studentList = scoreTableController.getStudentList();
        studentComboBox.getItems().addAll(studentList);

        courseList = scoreTableController.getCourseList();
        courseComboBox.getItems().addAll(courseList);
    }

    public void showDialog(Map data){
        if (data == null) {

            scoreId = null;
            currentStudentId = null;
            currentCourseId = null;
            studentComboBox.getSelectionModel().select(-1);
            courseComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            courseComboBox.setDisable(false);
            scoreField.setText("");
            gradeField.setText("");
        } else {
            scoreId = CommonMethod.getInteger(data, "scoreId");
            currentStudentId = CommonMethod.getInteger(data, "studentId");
            currentCourseId = CommonMethod.getInteger(data, "courseId");


            String studentId = CommonMethod.getString(data, "studentId");
            int index = CommonMethod.getOptionItemIndexByValue(studentList, studentId);
            studentComboBox.getSelectionModel().select(index);
            studentComboBox.setDisable(true); // 编辑时禁止修改学生

            // 课程
            String courseId = CommonMethod.getString(data, "courseId");
            index = CommonMethod.getOptionItemIndexByValue(courseList, courseId);
            courseComboBox.getSelectionModel().select(index);
            courseComboBox.setDisable(true); // 编辑时禁止修改课程

            // 成绩和等级
            scoreField.setText(CommonMethod.getString(data, "scoreValue"));
            gradeField.setText(CommonMethod.getString(data, "grade"));
        }
    }

    // 计算等级
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