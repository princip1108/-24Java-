package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> subjectComboBox;
    private List<OptionItem> subjectList;
    @FXML
    private ComboBox<OptionItem> levelComboBox;
    private List<OptionItem> levelList;
    @FXML
    private TextField nameField;
    private CompetitionController competitionController = null;
    private Integer competitionId = null;
    @FXML
    public void initialize() {
    }

    @FXML
    public void okButtonClick(){
        Map<String,Object> data = new HashMap<>();
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("personId",Integer.parseInt(op.getValue()));
        }
        op = subjectComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("subjectId",Integer.parseInt(op.getValue()));
        }
        op = levelComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("level",Integer.parseInt(op.getValue()));
        }
        data.put("competitionId", competitionId);
        data.put("competitionName", nameField.getText());
        competitionController.doClose("ok",data);
    }
    @FXML
    public void cancelButtonClick(){
        competitionController.doClose("cancel",null);
    }

    public void setCompetitionController(CompetitionController competitionController) {
        this.competitionController = competitionController;
    }
    public void init(){
        studentList = competitionController.getStudentList();
        subjectList = competitionController.getSubjectList();
        levelList = competitionController.getLevelList();
        studentComboBox.getItems().addAll(studentList );
        subjectComboBox.getItems().addAll(subjectList );
        levelComboBox.getItems().addAll(levelList );
    }
    public void showDialog(Map data){
        if(data == null) {
            competitionId = null;
            studentComboBox.getSelectionModel().select(-1);
            subjectComboBox.getSelectionModel().select(-1);
            levelComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            subjectComboBox.setDisable(false);
            levelComboBox.setDisable(false);
            nameField.setText("");
        }else {
            competitionId = CommonMethod.getInteger(data,"competitionId");
            studentComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(studentList, CommonMethod.getString(data, "personId")));
            subjectComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(subjectList, CommonMethod.getString(data, "subjectId")));
            levelComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(levelList, CommonMethod.getString(data, "level")));
            studentComboBox.setDisable(true);
            nameField.setText(CommonMethod.getString(data, "competitionName"));
        }
    }
}
