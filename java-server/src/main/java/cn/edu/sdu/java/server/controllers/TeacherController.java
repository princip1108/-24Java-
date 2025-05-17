package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherServices;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherServices teacherServices;

    public TeacherController(TeacherServices teacherServices) {
        this.teacherServices = teacherServices;
    }

    // ... 其他方法保持不变 ...

    @PostMapping("/getTeachersByEntryDate")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeachersByEntryDate(@Valid @RequestBody DataRequest dataRequest) {
        Date startDate = dataRequest.getDate("startDate");
        Date endDate = dataRequest.getDate("endDate");
        List<Teacher> teachers = teacherServices.getTeachersByEntryDate(startDate, endDate);

        // 创建标准响应
        DataResponse response = new DataResponse();
        response.setCode(200);
        response.setMsg("查询成功");
        response.setData(teachers);
        return response;
    }

    @PostMapping("/addTeacher")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addTeacher(@Valid @RequestBody DataRequest dataRequest) {
        // 调用服务层方法添加教师
        return teacherServices.addTeacher(dataRequest);
    }
}
