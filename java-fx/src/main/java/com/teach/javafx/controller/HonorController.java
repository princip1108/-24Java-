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
import java.util.logging.Level;
import java.util.logging.Logger;

public class HonorController {
    private static final Logger LOGGER = Logger.getLogger(HonorController.class.getName());

    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> honorNameColumn;
    @FXML
    private TableColumn<Map, String> honorLevelColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;

    private ArrayList<Map> honorList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<OptionItem> studentComboBox;
    @FXML
    private TextField levelField;

    private List<OptionItem> studentList;
    private HonorEditController honorEditController = null;
    private Stage stage = null;

    public List<OptionItem> getStudentList() {
        return studentList;
    }

    @FXML
    private void onQueryButtonClick() {
        Integer personId = getSelectedPersonId();
        String level = levelField.getText();

        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("honorLevel", level);

        try {
            DataResponse res = HttpRequestUtil.request("/api/honor/getHonorList", req);
            if (res != null && res.getCode() == 0) {
                honorList = (ArrayList<Map>) res.getData();
                setTableViewData();
            } else {
                String msg = res != null ? res.getMsg() : "请求失败";
                LOGGER.log(Level.WARNING, "获取荣誉列表失败: {0}", msg);
                MessageDialog.showDialog("获取荣誉列表失败: " + msg);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "获取荣誉列表时出现异常", e);
            MessageDialog.showDialog("获取荣誉列表时出现异常: " + e.getMessage());
        }
    }

    private Integer getSelectedPersonId() {
        OptionItem op = studentComboBox.getSelectionModel().getSelectedItem();
        return op != null ? Integer.parseInt(op.getValue()) : 0;
    }

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < honorList.size(); j++) {
            Map map = honorList.get(j);
            Button editButton = createEditButton(j);
            map.put("edit", editButton);
            observableList.add(map);
        }
        dataTableView.setItems(observableList);
    }

    private Button createEditButton(int index) {
        Button editButton = new Button("编辑");
        editButton.setId("edit" + index);
        editButton.setOnAction(e -> editItem(editButton.getId()));
        return editButton;
    }

    public void editItem(String name) {
        if (name == null) return;
        try {
            int j = Integer.parseInt(name.substring(4));
            Map data = honorList.get(j);
            initDialog();
            honorEditController.showDialog(data);
            MainApplication.setCanClose(false);
            stage.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "编辑项目时出现错误", e);
            MessageDialog.showDialog("编辑项目时出现错误: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        honorNameColumn.setCellValueFactory(new MapValueFactory<>("honorName"));
        honorLevelColumn.setCellValueFactory(new MapValueFactory<>("honorLevel"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));


        DataRequest req = new DataRequest();
        try {
            studentList = HttpRequestUtil.requestOptionItemList("/api/honor/getStudentItemOptionList", req);
            if (studentList == null) {
                studentList = new ArrayList<>();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "加载学生列表时发生错误", e);
            studentList = new ArrayList<>();
            MessageDialog.showDialog("加载学生列表时出现异常: " + e.getMessage());
        }


        OptionItem item = new OptionItem(null, "0", "请选择");
        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);

        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    private void initDialog() {
        if (stage != null) return;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("honor-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("荣誉信息录入对话框");
            stage.setOnCloseRequest(event -> MainApplication.setCanClose(true));

            honorEditController = (HonorEditController) fxmlLoader.getController();
            honorEditController.setHonorController(this);
            honorEditController.init();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "初始化对话框时出现错误", e);
            MessageDialog.showDialog("初始化对话框时出现错误: " + e.getMessage());
        }
    }

    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();
        if (!"ok".equals(cmd)) return;

        try {
            Integer personId = CommonMethod.getInteger(data, "personId");
            if (personId == null) {
                MessageDialog.showDialog("没有选中学生不能添加保存！");
                return;
            }

            DataRequest req = new DataRequest();
            req.add("personId", personId);
            req.add("honorId", CommonMethod.getInteger(data, "honorId"));
            req.add("honorName", CommonMethod.getString(data, "honorName"));
            req.add("honorLevel", CommonMethod.getString(data, "honorLevel"));

            DataResponse res = HttpRequestUtil.request("/api/honor/honorSave", req);
            if (res != null && res.getCode() == 0) {
                onQueryButtonClick();
            } else {
                String msg = res != null ? res.getMsg() : "保存失败";
                LOGGER.log(Level.WARNING, "保存荣誉信息失败: {0}", msg);
                MessageDialog.showDialog("保存荣誉信息失败: " + msg);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "保存荣誉信息时出现异常", e);
            MessageDialog.showDialog("保存荣誉信息时出现异常: " + e.getMessage());
        }
    }

    @FXML
    private void onAddButtonClick() {
        initDialog();
        honorEditController.showDialog(null);
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
        honorEditController.showDialog(data);
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

        try {
            Integer honorId = CommonMethod.getInteger(form, "honorId");
            DataRequest req = new DataRequest();
            req.add("honorId", honorId);

            DataResponse res = HttpRequestUtil.request("/api/honor/honorDelete", req);
            if (res.getCode() == 0) {
                onQueryButtonClick();
            } else {
                LOGGER.log(Level.WARNING, "删除荣誉失败: {0}", res.getMsg());
                MessageDialog.showDialog("删除失败: " + res.getMsg());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "删除荣誉时出现异常", e);
            MessageDialog.showDialog("删除荣誉时出现异常: " + e.getMessage());
        }
    }
}

