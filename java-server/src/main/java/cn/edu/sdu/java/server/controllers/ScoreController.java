package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.ScoreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/score")
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    // 获取学生选项列表（用于下拉框）
    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.getStudentItemOptionList(dataRequest);
    }

    // 获取课程选项列表（用于下拉框）
    @PostMapping("/getCourseItemOptionList")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.getCourseItemOptionList(dataRequest);
    }

    // 获取成绩列表
    @PostMapping("/getScoreList")
    public DataResponse getScoreList(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.getScoreList(dataRequest);
    }

    // 保存成绩（新增）
    @PostMapping("/scoreSave")
    public DataResponse scoreSave(@Valid @RequestBody DataRequest dataRequest) {
        System.out.println(dataRequest);
        return scoreService.scoreSave(dataRequest);
    }

    // 删除成绩
    @PostMapping("/scoreDelete")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.scoreDelete(dataRequest);
    }

    // 更新成绩
    @PostMapping("/scoreUpdate")
    public DataResponse scoreUpdate(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.scoreUpdate(dataRequest);
    }
}