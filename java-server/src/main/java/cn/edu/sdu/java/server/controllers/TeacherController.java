package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }


    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        try {
            logger.info("获取教师列表，查询条件: {}", dataRequest.getString("numName"));
            DataResponse response = teacherService.getTeacherList(dataRequest);
            logger.info("教师列表查询成功，返回 {} 条记录",
                response.getData() != null ? ((java.util.List<?>) response.getData()).size() : 0);
            return response;
        } catch (Exception e) {
            logger.error("获取教师列表失败", e);
            return CommonMethod.getReturnMessageError("获取教师列表失败：" + e.getMessage());
        }
    }


    @PostMapping("/teacherDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        try {
            Integer personId = dataRequest.getInteger("personId");
            logger.info("删除教师，personId: {}", personId);

            if (personId == null || personId <= 0) {
                return CommonMethod.getReturnMessageError("教师ID不能为空");
            }

            DataResponse response = teacherService.teacherDelete(dataRequest);
            logger.info("教师删除成功，personId: {}", personId);
            return response;
        } catch (Exception e) {
            logger.error("删除教师失败", e);
            return CommonMethod.getReturnMessageError("删除教师失败：" + e.getMessage());
        }
    }

    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        try {
            Integer personId = dataRequest.getInteger("personId");
            logger.info("获取教师详细信息，personId: {}", personId);

            if (personId == null || personId <= 0) {
                return CommonMethod.getReturnMessageError("教师ID不能为空");
            }

            DataResponse response = teacherService.getTeacherInfo(dataRequest);
            logger.info("教师详细信息获取成功，personId: {}", personId);
            return response;
        } catch (Exception e) {
            logger.error("获取教师详细信息失败", e);
            return CommonMethod.getReturnMessageError("获取教师详细信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/getTeacherPageData")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherPageData(@Valid @RequestBody DataRequest dataRequest) {
        try {
            String numName = dataRequest.getString("numName");
            Integer currentPage = dataRequest.getCurrentPage();
            logger.info("分页获取教师数据，查询条件: {}, 页码: {}", numName, currentPage);

            DataResponse response = teacherService.getTeacherPageData(dataRequest);
            logger.info("分页教师数据获取成功");
            return response;
        } catch (Exception e) {
            logger.error("分页获取教师数据失败", e);
            return CommonMethod.getReturnMessageError("分页获取教师数据失败：" + e.getMessage());
        }
    }


    @PostMapping("/teacherEditSave")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        try {
            logger.info("开始保存教师信息");

            // 验证请求数据
            if (dataRequest == null) {
                return CommonMethod.getReturnMessageError("请求数据不能为空");
            }

            java.util.Map<String, Object> form = dataRequest.getMap("form");
            if (form == null) {
                return CommonMethod.getReturnMessageError("教师信息不能为空");
            }

            String num = CommonMethod.getString(form, "num");
            String name = CommonMethod.getString(form, "name");

            if (num == null || num.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("教师工号不能为空");
            }

            if (name == null || name.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("教师姓名不能为空");
            }

            logger.info("保存教师信息，工号: {}, 姓名: {}", num, name);

            DataResponse response = teacherService.teacherEditSave(dataRequest);

            if (response.getCode() == 0) {
                logger.info("教师信息保存成功，personId: {}", response.getData());
            } else {
                logger.warn("教师信息保存失败: {}", response.getMsg());
            }

            return response;
        } catch (Exception e) {
            logger.error("保存教师信息时发生异常", e);
            return CommonMethod.getReturnMessageError("保存教师信息失败：" + e.getMessage());
        }
    }


    @PostMapping("/getCurrentTeacherInfo")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getCurrentTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        try {
            Integer personId = CommonMethod.getPersonId();
            logger.info("获取当前登录教师信息，personId: {}", personId);

            if (personId == null) {
                logger.warn("获取当前教师信息失败：用户未登录");
                return CommonMethod.getReturnMessageError("未登录或登录已过期");
            }

            DataRequest req = new DataRequest();
            req.add("personId", personId);

            DataResponse response = teacherService.getTeacherInfo(req);
            logger.info("当前教师信息获取成功");
            return response;
        } catch (Exception e) {
            logger.error("获取当前教师信息失败", e);
            return CommonMethod.getReturnMessageError("获取当前教师信息失败：" + e.getMessage());
        }
    }


    @GetMapping("/health")
    public DataResponse health() {
        return CommonMethod.getReturnMessageOK("教师服务运行正常");
    }
}
