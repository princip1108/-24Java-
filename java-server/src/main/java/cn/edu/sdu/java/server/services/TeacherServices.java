package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@Service
public class TeacherServices {
    private final PersonRepository personRepository;  //人��数据操作自动注入
    private final TeacherRepository teacherRepository;  //教师数据操作自动注入
    private final UserRepository userRepository;  //数据操作自动注入
    private final UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    private final PasswordEncoder encoder;  //密码服务自动注入
    private final SystemService systemService;

    public TeacherServices(PersonRepository personRepository, TeacherRepository teacherRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder, SystemService systemService) {
        this.personRepository = personRepository;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.encoder = encoder;
        this.systemService = systemService;
    }

    public Map<String, Object> getMapFromTeacher(Teacher s) {
        Map<String, Object> m = new HashMap<>();
        Person p;
        if (s == null)
            return m;
        m.put("title", s.getTitle());
        m.put("degree", s.getDegree());
        p = s.getPerson();
        if (p == null)
            return m;
        m.put("personId", s.getPersonId());
        m.put("num", p.getNum());
        m.put("name", p.getName());
        m.put("dept", p.getDept());
        m.put("card", p.getCard());
        String gender = p.getGender();
        m.put("gender", gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email", p.getEmail());
        m.put("phone", p.getPhone());
        m.remove("address");
        return m;
    }

    public List<Map<String, Object>> getTeacherMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (Teacher teacher : sList) {
            dataList.add(getMapFromTeacher(teacher));
        }
        return dataList;
    }

    public DataResponse getTeacherList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //���照测试框架规范会送Map的list
    }

    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Teacher s;
        Optional<Teacher> op;
        if (personId != null && personId > 0) {
            op = teacherRepository.findById(personId);
            if (op.isPresent()) {
                s = op.get();
                Optional<User> uOp = userRepository.findById(personId);
                uOp.ifPresent(userRepository::delete);
                Person p = s.getPerson();
                teacherRepository.delete(s);
                personRepository.delete(p);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Teacher s = null;
        Optional<Teacher> op;
        if (personId != null) {
            op = teacherRepository.findById(personId);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTeacher(s)); //这里回传包含学生信息的Map对象
    }

    public DataResponse teacherEditSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map<String, Object> form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        Teacher s = null;
        Person p;
        User u;
        Optional<Teacher> op;
        boolean isNew = false;
        if (personId != null) {
            op = teacherRepository.findById(personId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (s == null || !s.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新工号已经存在，不能添加或修改！");
            }
        }
        if (s == null) {
            p = new Person();
            p.setNum(num);
            p.setType("2");  // 2表示教师类型
            personRepository.saveAndFlush(p);  //插入新的Person记录
            personId = p.getPersonId();
            String password = encoder.encode("123456");
            u = new User();
            u.setPersonId(personId);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER));  // 设置为教师角色
            u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            u.setCreatorId(CommonMethod.getPersonId());
            userRepository.saveAndFlush(u); //插入新的User记录
            s = new Teacher();   // 创建实体对象
            s.setPersonId(personId);
            teacherRepository.saveAndFlush(s);  //插入新的Teacher记录
            isNew = true;
        } else {
            p = s.getPerson();
        }
        personId = p.getPersonId();
        if (!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User> uOp = userRepository.findByPersonPersonId(personId);
            if (uOp.isPresent()) {
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);  //设置属性
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(p);  // 修改保存人员信息
        s.setClassName(CommonMethod.getString(form, "className"));
        teacherRepository.save(s);
        systemService.modifyLog(s, isNew);
        return CommonMethod.getReturnData(s.getPersonId());  // 将personId返回前端
    }

    public ResponseEntity<StreamingResponseBody> getTeacherListExcl(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> list = getTeacherMapList(numName);

        // 调整列宽配置
        Integer[] widths = {8, 15, 10, 20, 15, 10, 8, 25, 15, 30};
        String[] titles = {"序号", "工号", "姓名", "学院", "职称", "学位", "性别", "邮箱", "电话",};
        String outPutSheetName = "teacher.xlsx";

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(outPutSheetName);

        // ��置列宽
        for (int j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }

        // 创建标题行样式
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = sheet.createRow(0);
        XSSFCell[] cell = new XSSFCell[widths.length];

        // 填充标题行
        for (int j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
        }

        // 填充数据行
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                Map<String, Object> m = list.get(i);

                // 创建单元格并设置样式
                for (int j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }

                // 填充具体数据
                cell[0].setCellValue((i + 1) + ""); // 序号
                cell[1].setCellValue(CommonMethod.getString(m, "num")); // 工号
                cell[2].setCellValue(CommonMethod.getString(m, "name")); // 姓名
                cell[3].setCellValue(CommonMethod.getString(m, "dept")); // 学院
                cell[4].setCellValue(CommonMethod.getString(m, "title")); // 职称
                cell[5].setCellValue(CommonMethod.getString(m, "degree")); // 学位
                cell[6].setCellValue(CommonMethod.getString(m, "genderName")); // 性别
                cell[7].setCellValue(CommonMethod.getString(m, "email")); // 邮箱
                cell[8].setCellValue(CommonMethod.getString(m, "phone")); // 电话
            }
        }

        try {
            StreamingResponseBody stream = wb::write;
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public DataResponse getTeacherPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String, Object>> dataList = new ArrayList<>();
        Pageable pageable = PageRequest.of(cPage, size);
        Page<Teacher> page = teacherRepository.findTeacherListByNumName(numName, pageable);
        Map<String, Object> m;
        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<Teacher> list = page.getContent();
            if (!list.isEmpty()) {
                for (Teacher teacher : list) {
                    m = getMapFromTeacher(teacher);
                    dataList.add(m);
                }
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }

    public List<Teacher> getTeachersByEntryDate(Date startDate, Date endDate) {
        // Assuming teacherRepository has a method to find by entry date range
        return teacherRepository.findByEntryDateBetween(startDate, endDate);
    }

    public DataResponse addTeacher(DataRequest dataRequest) {
        // 从请求中提取教师信息
        String name = dataRequest.getString("name");
        String title = dataRequest.getString("title");
        String degree = dataRequest.getString("degree");
        String className = dataRequest.getString("className");
        Date entryDate = dataRequest.getDate("entryDate");
        Integer studentCount = dataRequest.getInteger("studentCount");

        // 创建新的教师对象
        Teacher teacher = new Teacher();
        teacher.setTitle(title);
        teacher.setDegree(degree);
        teacher.setClassName(className);
        teacher.setEntryDate(entryDate);
        teacher.setStudentCount(studentCount);

        // 保存教师信息到数据库
        teacherRepository.save(teacher);

        // 返回成功响应
        return CommonMethod.getReturnMessageOK("教师添加成功");
    }
}
