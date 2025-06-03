package cn.edu.sdu.java.server.services;


import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Volunteer;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.VolunteerRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import io.lettuce.core.output.ScanOutput;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VolunteerService {
    private final VolunteerRepository volunteerRepository;
    private final StudentRepository studentRepository;

    public VolunteerService(VolunteerRepository scoreRepository, StudentRepository studentRepository) {
        this.volunteerRepository = scoreRepository;
        this.studentRepository = studentRepository;
    }
    public OptionItemList getStudentItemOptionList(DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public OptionItemList getActivityItemOptionList(DataRequest dataRequest) {
        List<Volunteer> sList = volunteerRepository.findVolunteerListByName("");  //数据库查询操作
        System.out.println(sList.size());
        List<OptionItem> itemList = new ArrayList<>();
        for (Volunteer s : sList) {
            itemList.add(new OptionItem( s.getVolunteerId(),s.getVolunteerId()+"", s.getVolunteerId()+"-"+s.getActivity_name()));
        }
        return new OptionItemList(0, itemList);
    }
    public DataResponse getVolList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer volunteerId = dataRequest.getInteger("volunteerId");
        List<Volunteer> sList = volunteerRepository.findByStudentActivityName(personId,volunteerId);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Volunteer s : sList) {
            m = new HashMap<>();
            m.put("volunteerId",s.getVolunteerId());
            m.put("personId",s.getStudent().getPersonId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("activityName",s.getActivity_name());
            m.put("timeLong",s.getTime_long());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse volunteerSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer timeLong = dataRequest.getInteger("timeLong");
        String activityName = dataRequest.getString("activityName");
        Integer volunteerId = dataRequest.getInteger("volunteerId");
        Optional<Volunteer> op;
        Volunteer s = null;
        if(volunteerId != null) {
            op= volunteerRepository.findById(volunteerId);
            if(op.isPresent())
                s = op.get();
        }
        if(s == null) {
            s = new Volunteer();
            s.setStudent(studentRepository.findById(personId).get());
        }
        s.setActivity_name(activityName);
        s.setTime_long(timeLong);
        volunteerRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse volunteerDelete(DataRequest dataRequest) {
        Integer volunteerId = dataRequest.getInteger("volunteerId");
        Optional<Volunteer> op;
        Volunteer s = null;
        if(volunteerId != null) {
            op= volunteerRepository.findById(volunteerId);
            if(op.isPresent()) {
                s = op.get();
                volunteerRepository.delete(s);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
