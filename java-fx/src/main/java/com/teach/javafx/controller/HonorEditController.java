package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HonorEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private TextField nameField;
    @FXML
    private TextField levelField;
    private HonorController honorController = null;
    private Integer honorId = null;

    @FXML
    public void initialize() {}

    @FXML
    public void okButtonClick() {
        try {
            Map<String, Object> data = new HashMap<>();
            OptionItem op = studentComboBox.getSelectionModel().getSelectedItem();

            if (op == null) {
                MessageDialog.showDialog("请选择学生");
                return;
            }

            data.put("personId", Integer.parseInt(op.getValue()));
            data.put("honorId", honorId);
            data.put("honorName", nameField.getText().trim());
            data.put("honorLevel", levelField.getText().trim());

            if (data.get("honorName") == null || ((String) data.get("honorName")).isEmpty()) {
                MessageDialog.showDialog("荣誉名称不能为空");
                return;
            }

            honorController.doClose("ok", data);
        } catch (Exception e) {
            MessageDialog.showDialog("保存数据时出现错误: " + e.getMessage());
        }
    }

    @FXML
    public void cancelButtonClick() {
        honorController.doClose("cancel", null);
    }

    public void setHonorController(HonorController honorController) {
        this.honorController = honorController;
    }

    public void init() {
        studentList = honorController.getStudentList();
        if (studentList == null) studentList = new ArrayList<>();
        studentComboBox.getItems().addAll(studentList);
    }

    public void showDialog(Map data) {
        try {
            if (data == null) {
                honorId = null;
                studentComboBox.getSelectionModel().select(-1);
                studentComboBox.setDisable(false);
                nameField.setText("");
                levelField.setText("");
            } else {
                honorId = CommonMethod.getInteger(data, "honorId");
                String personId = CommonMethod.getString(data, "personId");
                studentComboBox.getSelectionModel().select(
                        CommonMethod.getOptionItemIndexByValue(studentList, personId)
                );
                studentComboBox.setDisable(true);
                nameField.setText(CommonMethod.getString(data, "honorName"));
                levelField.setText(CommonMethod.getString(data, "honorLevel"));
            }
        } catch (Exception e) {
            MessageDialog.showDialog("加载数据时出现错误: " + e.getMessage());
        }
    }
}