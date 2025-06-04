package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.CompetitionService;
import cn.edu.sdu.java.server.services.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/competition")
public class CompetitionController {
    private final CompetitionService competitionService;
    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }
    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getStudentItemOptionList(dataRequest);
    }

    @PostMapping("/getSubjectItemOptionList")
    public OptionItemList getSubjectItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getSubjectItemOptionList(dataRequest);
    }

    @PostMapping("/getLevelItemOptionList")
    public OptionItemList getLevelItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getLevelItemOptionList(dataRequest);
    }

    @PostMapping("/getCompetitionList")
    public DataResponse getCompetitionList(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getComList(dataRequest);
    }
    @PostMapping("/competitionSave")
    public DataResponse competitionSave(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionSave(dataRequest);
    }
    @PostMapping("/competitionDelete")
    public DataResponse competitionDelete(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionDelete(dataRequest);
    }
}
