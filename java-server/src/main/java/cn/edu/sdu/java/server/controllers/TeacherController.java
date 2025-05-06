package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
public class TeacherController{

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }
    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        return  teacherService.getTeacherList(dataRequest);
    }
}
@PostMapping("/teacherDelete")
public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
    return studentService.studentDelete(dataRequest);
}
