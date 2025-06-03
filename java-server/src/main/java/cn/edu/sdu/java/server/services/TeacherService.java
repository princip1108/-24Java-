package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.repositorys.UserRepository;
import cn.edu.sdu.java.server.repositorys.UserTypeRepository;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * TeacherService 教师信息管理服务类
 * 提供教师信息的增删改查功能
 */
@Service
@Transactional
public class TeacherService {

    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder encoder;
    private final UserTypeRepository userTypeRepository;
    private final SystemService systemService;

    public TeacherService(TeacherRepository teacherRepository,
                         UserRepository userRepository,
                         PersonRepository personRepository,
                         PasswordEncoder encoder,
                         UserTypeRepository userTypeRepository,
                         SystemService systemService) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.encoder = encoder;
        this.userTypeRepository = userTypeRepository;
        this.systemService = systemService;
    }

    public Map<String,Object> getMapFromTeacher(Teacher t) {
        Map<String,Object> m = new HashMap<>();
        Person p;
        if(t == null)
            return m;
        m.put("title",t.getTitle());
        m.put("degree",t.getDegree());
        p = t.getPerson();
        if(p == null)
            return m;
        m.put("personId", t.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        return m;
    }
    public List<Map<String,Object>> getTeacherMapList(String numName) {
        List<Map<String,Object>> dataList = new ArrayList<>();
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
        List<Map<String,Object>> dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");  //获取teacher_id值
        Teacher s = null;
        Optional<Teacher> op;
        if (personId != null && personId > 0) {
            op = teacherRepository.findById(personId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                Optional<User> uOp = userRepository.findById(personId); //查询对应该老师的账户
                //删除对应该老师的账户
                uOp.ifPresent(userRepository::delete);
                Person p = s.getPerson();
                teacherRepository.delete(s);    //首先teacher表永久删除老师信息
                personRepository.delete(p);   // 然后person表永久删除老师信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Teacher s = null;
        Optional<Teacher> op;
        if (personId != null) {
            op = teacherRepository.findById(personId); //根据老师主键从数据库查询老师的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTeacher(s)); //这里回传包含老师信息的Map对象
    }

    public DataResponse getTeacherPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String,Object>> dataList = new ArrayList<>();
        Page<Teacher> page = null;
        Pageable pageable = PageRequest.of(cPage, size);
        page = teacherRepository.findTeacherPageByNumName(numName, pageable);
        Map<String,Object> m;
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
        Map<String,Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }

    /**
     * 保存教师信息（新增或修改）
     * @param dataRequest 包含教师信息的请求对象
     * @return DataResponse 操作结果
     */
    @Transactional
    public DataResponse teacherEditSave(DataRequest dataRequest) {
        try {
            logger.info("开始保存教师信息，请求数据: {}", dataRequest);

            Integer personId = dataRequest.getInteger("personId");
            Map<String, Object> form = dataRequest.getMap("form");

            // 验证必填字段
            String num = CommonMethod.getString(form, "num");
            String name = CommonMethod.getString(form, "name");

            if (num == null || num.trim().isEmpty()) {
                logger.error("教师工号为空");
                return CommonMethod.getReturnMessageError("教师工号不能为空！");
            }

            if (name == null || name.trim().isEmpty()) {
                logger.error("教师姓名为空");
                return CommonMethod.getReturnMessageError("教师姓名不能为空！");
            }

            Teacher teacher = null;
            Person person;
            User user;
            boolean isNew = false;

            // 查找现有教师
            if (personId != null) {
                Optional<Teacher> teacherOp = teacherRepository.findById(personId);
                if (teacherOp.isPresent()) {
                    teacher = teacherOp.get();
                    logger.info("找到现有教师，personId: {}", personId);
                }
            }

            // 检查工号是否已存在
            Optional<Person> existingPerson = personRepository.findByNum(num);
            if (existingPerson.isPresent()) {
                if (teacher == null || !teacher.getPerson().getNum().equals(num)) {
                    logger.error("工号已存在: {}", num);
                    return CommonMethod.getReturnMessageError("工号 " + num + " 已经存在，不能添加或修改！");
                }
            }

            // 新增教师
            if (teacher == null) {
                logger.info("创建新教师，工号: {}", num);

                // 创建Person记录 - 先设置所有必要字段
                person = new Person();
                person.setNum(num);
                person.setName(name);  // 立即设置姓名，避免数据库约束错误
                person.setType("2"); // 教师类型

                // 设置其他Person字段，特别处理邮箱验证
                person.setDept(CommonMethod.getString(form, "dept") != null ? CommonMethod.getString(form, "dept") : "");
                person.setCard(CommonMethod.getString(form, "card") != null ? CommonMethod.getString(form, "card") : "");
                person.setGender(CommonMethod.getString(form, "gender") != null ? CommonMethod.getString(form, "gender") : "");
                person.setBirthday(CommonMethod.getString(form, "birthday") != null ? CommonMethod.getString(form, "birthday") : "");
                person.setPhone(CommonMethod.getString(form, "phone") != null ? CommonMethod.getString(form, "phone") : "");
                person.setAddress(CommonMethod.getString(form, "address") != null ? CommonMethod.getString(form, "address") : "");

                // 特殊处理邮箱字段 - 空字符串会导致@Email验证失败，设为null
                String email = CommonMethod.getString(form, "email");
                if (email != null && !email.trim().isEmpty()) {
                    person.setEmail(email.trim());
                } else {
                    person.setEmail(null);  // 空邮箱设为null而不是空字符串
                }

                logger.info("准备保存Person对象: num={}, name={}, type={}, email={}, dept={}",
                    person.getNum(), person.getName(), person.getType(), person.getEmail(), person.getDept());

                try {
                    personRepository.saveAndFlush(person);
                    personId = person.getPersonId();
                    logger.info("Person保存成功，personId: {}", personId);
                } catch (Exception e) {
                    logger.error("保存Person时发生错误: {}", e.getMessage(), e);
                    throw e;
                }

                // 创建User账户
                String password = encoder.encode("123456");
                user = new User();
                user.setPersonId(personId);
                user.setUserName(num);
                user.setPassword(password);
                UserType userType = userTypeRepository.findByName(EUserType.valueOf("ROLE_TEACHER"));
                user.setUserType(userType);
                user.setCreateTime(DateTimeTool.parseDateTime(new Date()));
                user.setCreatorId(CommonMethod.getPersonId());
                userRepository.saveAndFlush(user);

                // 创建Teacher记录 - 先设置所有必要字段
                teacher = new Teacher();
                teacher.setPersonId(personId);
                teacher.setName(name);  // 立即设置name字段
                teacher.setTitle(CommonMethod.getString(form, "title"));
                teacher.setDegree(CommonMethod.getString(form, "degree"));
                teacher.setStudentNum(CommonMethod.getInteger(form, "studentNum"));
                teacher.setEnterTime(CommonMethod.getDate(form, "enterTime"));
                teacherRepository.saveAndFlush(teacher);
                isNew = true;

                logger.info("新教师创建成功，personId: {}", personId);
            } else {
                person = teacher.getPerson();
                logger.info("更新现有教师，personId: {}", personId);
            }

            // 更新工号（如果变化）
            if (!num.equals(person.getNum())) {
                Optional<User> userOp = userRepository.findByPersonPersonId(personId);
                if (userOp.isPresent()) {
                    user = userOp.get();
                    user.setUserName(num);
                    userRepository.saveAndFlush(user);
                }
                person.setNum(num);
            }

            // 更新Person信息（仅在编辑现有教师时需要）
            if (!isNew) {
                person.setName(name);
                person.setDept(CommonMethod.getString(form, "dept"));
                person.setCard(CommonMethod.getString(form, "card"));
                person.setGender(CommonMethod.getString(form, "gender"));
                person.setBirthday(CommonMethod.getString(form, "birthday"));
                person.setEmail(CommonMethod.getString(form, "email"));
                person.setPhone(CommonMethod.getString(form, "phone"));
                person.setAddress(CommonMethod.getString(form, "address"));
                personRepository.save(person);
            }

            // 更新Teacher信息（仅在编辑现有教师时需要）
            if (!isNew) {
                teacher.setName(name);  // 设置Teacher表中的name字段
                teacher.setTitle(CommonMethod.getString(form, "title"));
                teacher.setDegree(CommonMethod.getString(form, "degree"));
                teacher.setStudentNum(CommonMethod.getInteger(form, "studentNum"));
                teacher.setEnterTime(CommonMethod.getDate(form, "enterTime"));
                teacherRepository.save(teacher);
            }

            // 记录操作日志
            systemService.modifyLog(teacher, isNew);

            logger.info("教师信息保存成功，personId: {}", teacher.getPersonId());
            return CommonMethod.getReturnData(teacher.getPersonId());

        } catch (Exception e) {
            logger.error("保存教师信息时发生异常", e);
            return CommonMethod.getReturnMessageError("保存教师信息失败：" + e.getMessage());
        }
    }
}