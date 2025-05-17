package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Date;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherServices teacherServices;

    // 获取教师列表
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        return teacherServices.getTeacherList(dataRequest);
    }

    // 获取教师分页数据
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public DataResponse getTeacherPageData(@Valid @RequestBody DataRequest dataRequest) {
        return teacherServices.getTeacherPageData(dataRequest);
    }

    // 获取教师信息
    @GetMapping("/{personId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public DataResponse getTeacherInfo(@PathVariable Integer personId) {
        DataRequest request = new DataRequest();
        request.add("personId", personId);
        return teacherServices.getTeacherInfo(request);
    }

    // 编辑保存教师信息
    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return teacherServices.teacherEditSave(dataRequest);
    }

    // 删除教师
    @DeleteMapping("/{personId}")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse teacherDelete(@PathVariable Integer personId) {
        DataRequest request = new DataRequest();
        request.add("personId", personId);
        return teacherServices.teacherDelete(request);
    }

    // 根据入职日期获取教师列表
    @GetMapping("/byEntryDate")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeachersByEntryDate(@RequestParam Date startDate, @RequestParam Date endDate) {
        return teacherServices.getTeachersByEntryDate(startDate, endDate);
    }

    // 添加教师
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addTeacher(@Valid @RequestBody DataRequest dataRequest) {
        return teacherServices.addTeacher(dataRequest);
    }

    // 导出教师列表到Excel
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StreamingResponseBody> getTeacherListExcl(@Valid @RequestBody DataRequest dataRequest) {
        return teacherServices.getTeacherListExcl(dataRequest);
    }
}

