package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class TeacherController extends ToolController {


    private static final String API_GET_TEACHER_LIST = "/api/teacher/getTeacherList";
    private static final String API_GET_TEACHER_INFO = "/api/teacher/getTeacherInfo";
    private static final String API_TEACHER_EDIT_SAVE = "/api/teacher/teacherEditSave";
    private static final String API_TEACHER_DELETE = "/api/teacher/teacherDelete";
    private static final String API_UPLOAD_PHOTO = "/api/base/uploadPhoto";
    private static final String API_GET_FILE_DATA = "/api/base/getFileByteData";

    private static final String GENDER_DICT_CODE = "XBM";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String PHOTO_PATH_PREFIX = "photo/";
    private static final String PHOTO_FILE_EXTENSION = ".jpg";


    @FXML
    private ImageView photoImageView;
    @FXML
    private Button photoButton;


    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> numColumn;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> deptColumn;
    @FXML
    private TableColumn<Map, String> titleColumn;
    @FXML
    private TableColumn<Map, String> degreeColumn;

    @FXML
    private TableColumn<Map, String> cardColumn;  // 已移除
    @FXML
    private TableColumn<Map, String> genderColumn;
    @FXML
    private TableColumn<Map, String> birthdayColumn;  // 已移除
    @FXML
    private TableColumn<Map, String> emailColumn;
    @FXML
    private TableColumn<Map, String> phoneColumn;
    @FXML
    private TableColumn<Map, String> addressColumn;  // 已移除


    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField deptField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField degreeField;
    @FXML
    private TextField cardField;
    @FXML
    private ComboBox<OptionItem> genderComboBox;
    @FXML
    private DatePicker birthdayPick;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;


    @FXML
    private TextField numNameTextField;


    @FXML
    private Label statusLabel;
    @FXML
    private Label recordCountLabel;
    @FXML
    private Label formModeLabel;


    @FXML
    private Button saveButton2;
    @FXML
    private Button clearButton2;


    private Integer personId = null;


    private ArrayList<Map> teacherList = new ArrayList<>();


    private List<OptionItem> genderList;


    private ObservableList<Map> observableList = FXCollections.observableArrayList();


    private boolean isLoading = false;


    private void setTableViewData() {
        Platform.runLater(() -> {
            observableList.clear();
            if (teacherList != null && !teacherList.isEmpty()) {
                observableList.addAll(teacherList);
            }
            dataTableView.setItems(observableList);


            int recordCount = teacherList != null ? teacherList.size() : 0;
            if (recordCountLabel != null) {
                recordCountLabel.setText("共 " + recordCount + " 条记录");
            }

            if (statusLabel != null) {
                statusLabel.setText(recordCount > 0 ? "数据已加载" : "暂无数据");
            }

            System.out.println("教师列表已更新，共 " + recordCount + " 条记录");
        });
    }


    @FXML
    public void initialize() {
        try {
            System.out.println("=== 教师管理界面初始化开始 ===");


            initializeTableColumns();

            initializeTableSelectionListener();


            initializeGenderComboBox();


            initializeDatePicker();


            loadTeacherListAsync("");

            System.out.println("=== 教师管理界面初始化完成 ===");

        } catch (Exception e) {
            System.err.println("教师管理界面初始化失败: " + e.getMessage());
            e.printStackTrace();
            MessageDialog.showDialog("界面初始化失败：" + e.getMessage());
        }
    }


    private void initializeTableColumns() {

        if (numColumn != null) {
            numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        }
        if (nameColumn != null) {
            nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        }
        if (deptColumn != null) {
            deptColumn.setCellValueFactory(new MapValueFactory<>("dept"));
        }
        if (titleColumn != null) {
            titleColumn.setCellValueFactory(new MapValueFactory<>("title"));
        }
        if (degreeColumn != null) {
            degreeColumn.setCellValueFactory(new MapValueFactory<>("degree"));
        }
        if (genderColumn != null) {
            genderColumn.setCellValueFactory(new MapValueFactory<>("genderName"));
        }
        if (phoneColumn != null) {
            phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        }
        if (emailColumn != null) {
            emailColumn.setCellValueFactory(new MapValueFactory<>("email"));
        }


        System.out.println("表格列初始化完成");
    }


    private void initializeTableSelectionListener() {
        TableView.TableViewSelectionModel<Map> selectionModel = dataTableView.getSelectionModel();
        ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
        selectedIndices.addListener(this::onTableRowSelect);

        System.out.println("表格选择监听器初始化完成");
    }


    private void initializeGenderComboBox() {
        try {
            genderList = HttpRequestUtil.getDictionaryOptionItemList(GENDER_DICT_CODE);
            if (genderList != null && !genderList.isEmpty()) {
                genderComboBox.getItems().addAll(genderList);
                System.out.println("性别下拉框初始化完成，共 " + genderList.size() + " 个选项");
            } else {
                System.out.println("警告：性别字典数据为空");
            }
        } catch (Exception e) {
            System.err.println("性别下拉框初始化失败: " + e.getMessage());
        }
    }

    private void initializeDatePicker() {
        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        System.out.println("日期选择器初始化完成");
    }

    private void loadTeacherListAsync(String numName) {
        if (isLoading) {
            System.out.println("正在加载数据，请稍候...");
            return;
        }

        isLoading = true;

        Task<DataResponse> task = new Task<DataResponse>() {
            @Override
            protected DataResponse call() throws Exception {
                DataRequest req = new DataRequest();
                req.add("numName", numName != null ? numName : "");
                return HttpRequestUtil.request(API_GET_TEACHER_LIST, req);
            }

            @Override
            protected void succeeded() {
                isLoading = false;
                DataResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.getCode() == 0) {
                        teacherList = (ArrayList<Map>) response.getData();
                        setTableViewData();
                        System.out.println("教师列表加载成功");
                    } else {
                        String errorMsg = response != null ? response.getMsg() : "网络请求失败";
                        System.err.println("教师列表加载失败: " + errorMsg);
                        MessageDialog.showDialog("加载教师列表失败：" + errorMsg);
                    }
                });
            }

            @Override
            protected void failed() {
                isLoading = false;
                Platform.runLater(() -> {
                    System.err.println("教师列表加载异常: " + getException().getMessage());
                    MessageDialog.showDialog("加载教师列表失败：" + getException().getMessage());
                });
            }
        };

        new Thread(task).start();
    }




    public void clearPanel() {
        Platform.runLater(() -> {
            personId = null;
            numField.setText("");
            nameField.setText("");
            deptField.setText("");
            titleField.setText("");
            degreeField.setText("");
            cardField.setText("");
            genderComboBox.getSelectionModel().select(-1);
            birthdayPick.getEditor().setText("");
            emailField.setText("");
            phoneField.setText("");
            addressField.setText("");

            // 清空照片显示
            if (photoImageView != null) {
                photoImageView.setImage(null);
            }

            // 更新表单模式标签
            if (formModeLabel != null) {
                formModeLabel.setText("新增模式");
            }

            System.out.println("表单已清空");
        });
    }


    private boolean validateFormData() {
        // 验证工号
        String num = numField.getText();
        if (num == null || num.trim().isEmpty()) {
            MessageDialog.showDialog("教师工号不能为空！");
            numField.requestFocus();
            return false;
        }

        // 验证姓名
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            MessageDialog.showDialog("教师姓名不能为空！");
            nameField.requestFocus();
            return false;
        }


        if (!num.trim().matches("^[a-zA-Z0-9]+$")) {
            MessageDialog.showDialog("工号只能包含字母和数字！");
            numField.requestFocus();
            return false;
        }

        return true;
    }


    protected void changeTeacherInfo() {
        Map<String, Object> selectedItem = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            clearPanel();
            return;
        }

        Integer selectedPersonId = CommonMethod.getInteger(selectedItem, "personId");
        if (selectedPersonId == null) {
            System.err.println("选中记录的personId为空");
            clearPanel();
            return;
        }

        System.out.println("加载教师详细信息，personId: " + selectedPersonId);


        Task<DataResponse> task = new Task<DataResponse>() {
            @Override
            protected DataResponse call() throws Exception {
                DataRequest req = new DataRequest();
                req.add("personId", selectedPersonId);
                return HttpRequestUtil.request(API_GET_TEACHER_INFO, req);
            }

            @Override
            protected void succeeded() {
                DataResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.getCode() == 0) {
                        Map<String, Object> teacherInfo = (Map<String, Object>) response.getData();
                        fillFormWithTeacherInfo(teacherInfo);
                        personId = selectedPersonId;
                        displayPhoto();
                        System.out.println("教师详细信息加载成功");
                    } else {
                        String errorMsg = response != null ? response.getMsg() : "网络请求失败";
                        System.err.println("获取教师详细信息失败: " + errorMsg);
                        MessageDialog.showDialog("获取教师详细信息失败：" + errorMsg);
                        clearPanel();
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("获取教师详细信息异常: " + getException().getMessage());
                    MessageDialog.showDialog("获取教师详细信息失败：" + getException().getMessage());
                    clearPanel();
                });
            }
        };

        new Thread(task).start();
    }


    private void fillFormWithTeacherInfo(Map<String, Object> teacherInfo) {
        if (teacherInfo == null) {
            return;
        }

        numField.setText(CommonMethod.getString(teacherInfo, "num"));
        nameField.setText(CommonMethod.getString(teacherInfo, "name"));
        deptField.setText(CommonMethod.getString(teacherInfo, "dept"));
        titleField.setText(CommonMethod.getString(teacherInfo, "title"));
        degreeField.setText(CommonMethod.getString(teacherInfo, "degree"));
        cardField.setText(CommonMethod.getString(teacherInfo, "card"));

        // 设置性别下拉框
        String gender = CommonMethod.getString(teacherInfo, "gender");
        if (genderList != null && !genderList.isEmpty()) {
            int genderIndex = CommonMethod.getOptionItemIndexByValue(genderList, gender);
            genderComboBox.getSelectionModel().select(genderIndex);
        }

        birthdayPick.getEditor().setText(CommonMethod.getString(teacherInfo, "birthday"));
        emailField.setText(CommonMethod.getString(teacherInfo, "email"));
        phoneField.setText(CommonMethod.getString(teacherInfo, "phone"));
        addressField.setText(CommonMethod.getString(teacherInfo, "address"));

        // 更新表单模式标签
        if (formModeLabel != null) {
            formModeLabel.setText("编辑模式");
        }
    }


    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeTeacherInfo();
    }


    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        System.out.println("查询教师，条件: " + (numName != null ? numName : "全部"));
        loadTeacherListAsync(numName);
    }


    @FXML
    protected void onAddButtonClick() {
        System.out.println("准备添加新教师");
        clearPanel();
    }


    @FXML
    protected void onSaveButtonClick() {
        try {
            System.out.println("开始保存教师信息");


            if (!validateUserPermission()) {
                return;
            }


            if (!validateFormData()) {
                return;
            }


            Map<String, Object> formData = buildFormData();


            saveTeacherAsync(formData);

        } catch (Exception e) {
            System.err.println("保存教师信息时发生异常: " + e.getMessage());
            e.printStackTrace();
            MessageDialog.showDialog("保存失败：" + e.getMessage());
        }
    }


    private boolean validateUserPermission() {
        if (AppStore.getJwt() == null) {
            MessageDialog.showDialog("用户未登录，请重新登录！");
            return false;
        }

        String currentRole = AppStore.getJwt().getRole();
        if (!"ROLE_ADMIN".equals(currentRole)) {
            MessageDialog.showDialog("权限不足！只有管理员可以添加/修改教师信息。");
            return false;
        }

        return true;
    }


    private Map<String, Object> buildFormData() {
        Map<String, Object> form = new HashMap<>();


        form.put("num", numField.getText() != null ? numField.getText().trim() : "");
        form.put("name", nameField.getText() != null ? nameField.getText().trim() : "");
        form.put("dept", deptField.getText() != null ? deptField.getText().trim() : "");
        form.put("title", titleField.getText() != null ? titleField.getText().trim() : "");
        form.put("degree", degreeField.getText() != null ? degreeField.getText().trim() : "");
        form.put("card", cardField.getText() != null ? cardField.getText().trim() : "");
        form.put("email", emailField.getText() != null ? emailField.getText().trim() : "");
        form.put("phone", phoneField.getText() != null ? phoneField.getText().trim() : "");
        form.put("address", addressField.getText() != null ? addressField.getText().trim() : "");


        String birthday = "";
        if (birthdayPick != null && birthdayPick.getEditor() != null &&
            birthdayPick.getEditor().getText() != null) {
            birthday = birthdayPick.getEditor().getText().trim();
        }
        form.put("birthday", birthday);


        String gender = "";
        if (genderComboBox != null && genderComboBox.getSelectionModel() != null &&
            genderComboBox.getSelectionModel().getSelectedItem() != null) {
            gender = genderComboBox.getSelectionModel().getSelectedItem().getValue();
        }
        form.put("gender", gender != null ? gender : "");


        form.put("studentNum", null);
        form.put("enterTime", null);

        if (form.get("birthday").toString().isEmpty()) {
            form.put("birthday", null);
        }
        if (form.get("gender").toString().isEmpty()) {
            form.put("gender", null);
        }

        System.out.println("构建的表单数据: " + form);
        return form;
    }


    private void saveTeacherAsync(Map<String, Object> formData) {
        Task<DataResponse> task = new Task<DataResponse>() {
            @Override
            protected DataResponse call() throws Exception {
                DataRequest req = new DataRequest();
                req.add("personId", personId);
                req.add("form", formData);

                System.out.println("发送保存请求，personId: " + personId);
                System.out.println("表单数据: " + formData);

                return HttpRequestUtil.request(API_TEACHER_EDIT_SAVE, req);
            }

            @Override
            protected void succeeded() {
                DataResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.getCode() == 0) {
                        personId = CommonMethod.getIntegerFromObject(response.getData());
                        System.out.println("教师信息保存成功，personId: " + personId);
                        MessageDialog.showDialog("保存成功！");

                        // 刷新列表
                        String currentQuery = numNameTextField.getText();
                        loadTeacherListAsync(currentQuery);

                    } else {
                        String errorMsg = response != null ? response.getMsg() : "未知错误";
                        System.err.println("教师信息保存失败: " + errorMsg);


                        String userMsg = errorMsg;
                        if (errorMsg.contains("rollback-only")) {
                            userMsg = "保存失败，可能原因：\n1. 工号已存在，请使用其他工号\n2. 数据格式不正确\n3. 必填字段缺失\n\n详细错误：" + errorMsg;
                        } else if (errorMsg.contains("Duplicate entry") || errorMsg.contains("unique constraint")) {
                            userMsg = "工号已存在，请使用其他工号！";
                        }

                        MessageDialog.showDialog(userMsg);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("保存教师信息异常: " + getException().getMessage());
                    MessageDialog.showDialog("保存失败：" + getException().getMessage());
                });
            }
        };

        new Thread(task).start();
    }


    @FXML
    protected void onDeleteButtonClick() {
        Map<String, Object> selectedItem = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            MessageDialog.showDialog("请先选择要删除的教师！");
            return;
        }

        String teacherName = CommonMethod.getString(selectedItem, "name");
        String teacherNum = CommonMethod.getString(selectedItem, "num");

        int ret = MessageDialog.choiceDialog("确认要删除教师 " + teacherName + "(" + teacherNum + ") 吗？\n删除后无法恢复！");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }

        Integer deletePersonId = CommonMethod.getInteger(selectedItem, "personId");
        if (deletePersonId == null) {
            MessageDialog.showDialog("无法获取教师ID，删除失败！");
            return;
        }

        System.out.println("删除教师，personId: " + deletePersonId);


        Task<DataResponse> task = new Task<DataResponse>() {
            @Override
            protected DataResponse call() throws Exception {
                DataRequest req = new DataRequest();
                req.add("personId", deletePersonId);
                return HttpRequestUtil.request(API_TEACHER_DELETE, req);
            }

            @Override
            protected void succeeded() {
                DataResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.getCode() == 0) {
                        System.out.println("教师删除成功");
                        MessageDialog.showDialog("删除成功！");
                        clearPanel();


                        String currentQuery = numNameTextField.getText();
                        loadTeacherListAsync(currentQuery);

                    } else {
                        String errorMsg = response != null ? response.getMsg() : "删除失败";
                        System.err.println("教师删除失败: " + errorMsg);
                        MessageDialog.showDialog("删除失败：" + errorMsg);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("删除教师异常: " + getException().getMessage());
                    MessageDialog.showDialog("删除失败：" + getException().getMessage());
                });
            }
        };

        new Thread(task).start();
    }


    @Override
    public void doNew() {
        System.out.println("ToolController.doNew() 被调用");
        clearPanel();
    }


    @Override
    public void doSave() {
        System.out.println("ToolController.doSave() 被调用");
        onSaveButtonClick();
    }


    @Override
    public void doDelete() {
        System.out.println("ToolController.doDelete() 被调用");
        onDeleteButtonClick();
    }

    public void displayPhoto() {
        if (personId == null) {
            if (photoImageView != null) {
                photoImageView.setImage(null);
            }
            return;
        }

        Task<byte[]> task = new Task<byte[]>() {
            @Override
            protected byte[] call() throws Exception {
                DataRequest req = new DataRequest();
                req.add("fileName", PHOTO_PATH_PREFIX + personId + PHOTO_FILE_EXTENSION);
                return HttpRequestUtil.requestByteData(API_GET_FILE_DATA, req);
            }

            @Override
            protected void succeeded() {
                byte[] bytes = getValue();
                Platform.runLater(() -> {
                    if (bytes != null && photoImageView != null) {
                        try {
                            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                            Image img = new Image(in);
                            photoImageView.setImage(img);
                            System.out.println("教师照片加载成功");
                        } catch (Exception e) {
                            System.err.println("照片显示失败: " + e.getMessage());
                            photoImageView.setImage(null);
                        }
                    } else {
                        if (photoImageView != null) {
                            photoImageView.setImage(null);
                        }
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    if (photoImageView != null) {
                        photoImageView.setImage(null);
                    }
                    System.out.println("照片加载失败: " + getException().getMessage());
                });
            }
        };

        new Thread(task).start();
    }


    @FXML
    public void onPhotoButtonClick() {
        if (personId == null) {
            MessageDialog.showDialog("请先选择或保存教师信息后再上传照片！");
            return;
        }

        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("选择教师照片");
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("JPG 文件", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG 文件", "*.png")
        );

        File file = fileDialog.showOpenDialog(null);
        if (file == null) {
            return;
        }


        long fileSize = file.length();
        if (fileSize > 5 * 1024 * 1024) {
            MessageDialog.showDialog("照片文件过大，请选择小于5MB的图片！");
            return;
        }

        System.out.println("上传教师照片，personId: " + personId + ", 文件: " + file.getName());


        Task<DataResponse> task = new Task<DataResponse>() {
            @Override
            protected DataResponse call() throws Exception {
                String targetFileName = PHOTO_PATH_PREFIX + personId + PHOTO_FILE_EXTENSION;
                return HttpRequestUtil.uploadFile(API_UPLOAD_PHOTO, file.getPath(), targetFileName);
            }

            @Override
            protected void succeeded() {
                DataResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.getCode() == 0) {
                        System.out.println("照片上传成功");
                        MessageDialog.showDialog("照片上传成功！");
                        displayPhoto();
                    } else {
                        String errorMsg = response != null ? response.getMsg() : "上传失败";
                        System.err.println("照片上传失败: " + errorMsg);
                        MessageDialog.showDialog("照片上传失败：" + errorMsg);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("照片上传异常: " + getException().getMessage());
                    MessageDialog.showDialog("照片上传失败：" + getException().getMessage());
                });
            }
        };

        new Thread(task).start();
    }


    @FXML
    protected void onImportButtonClick() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("选择教师数据文件");
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel 文件", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"),
                new FileChooser.ExtensionFilter("XLS 文件", "*.xls")
        );

        File file = fileDialog.showOpenDialog(null);
        if (file == null) {
            return;
        }

        System.out.println("导入教师数据，文件: " + file.getName());


        Task<DataResponse> task = new Task<DataResponse>() {
            @Override
            protected DataResponse call() throws Exception {
                String paras = "";
                return HttpRequestUtil.importData("/api/teacher/importTeacherData", file.getPath(), paras);
            }

            @Override
            protected void succeeded() {
                DataResponse response = getValue();
                Platform.runLater(() -> {
                    if (response != null && response.getCode() == 0) {
                        System.out.println("教师数据导入成功");
                        MessageDialog.showDialog("数据导入成功！");

                        // 刷新列表
                        String currentQuery = numNameTextField.getText();
                        loadTeacherListAsync(currentQuery);

                    } else {
                        String errorMsg = response != null ? response.getMsg() : "导入失败";
                        System.err.println("教师数据导入失败: " + errorMsg);
                        MessageDialog.showDialog("数据导入失败：" + errorMsg);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("教师数据导入异常: " + getException().getMessage());
                    MessageDialog.showDialog("数据导入失败：" + getException().getMessage());
                });
            }
        };

        new Thread(task).start();
    }
}