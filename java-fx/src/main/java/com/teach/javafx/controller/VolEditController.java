package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private TextField nameField;
    @FXML
    private TextField timeField;
    private VolunteerController volunteerController = null;
    private Integer volunteerId = null;
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
        data.put("volunteerId", volunteerId);
        data.put("activityName", nameField.getText());
        data.put("timeLong", timeField.getText());
        volunteerController.doClose("ok",data);
    }
    @FXML
    public void cancelButtonClick(){
        volunteerController.doClose("cancel",null);
    }

    public void setVolunteerController(VolunteerController volunteerController) {
        this.volunteerController = volunteerController;
    }
    public void init(){
        studentList = volunteerController.getStudentList();
        studentComboBox.getItems().addAll(studentList );
    }
    public void showDialog(Map data){
        if(data == null) {
            volunteerId = null;
            studentComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            nameField.setText("");
        }else {
            volunteerId = CommonMethod.getInteger(data,"volunteerId");
            studentComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(studentList, CommonMethod.getString(data, "personId")));
            studentComboBox.setDisable(true);
            nameField.setText(CommonMethod.getString(data, "activityName"));
            timeField.setText(CommonMethod.getString(data, "timeLong"));
        }
    }
}
