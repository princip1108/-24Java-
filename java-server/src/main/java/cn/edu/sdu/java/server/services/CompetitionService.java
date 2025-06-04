package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Subject;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CompetitionRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public CompetitionService(CompetitionRepository competitionRepository, StudentRepository studentRepository, SubjectRepository subjectRepository) {
        this.competitionRepository = competitionRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }
    public OptionItemList getStudentItemOptionList(DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public OptionItemList getSubjectItemOptionList(DataRequest dataRequest) {
        List<Subject> sList = competitionRepository.findSubjectListByName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Subject s : sList) {
            itemList.add(new OptionItem( s.getSubjectId(),s.getSubjectId()+"", s.getSubjectId()+"-"+s.getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public OptionItemList getLevelItemOptionList(DataRequest dataRequest) {
        List<OptionItem> itemList = new ArrayList<>();
        itemList.add(new OptionItem( 1,"1", "一等奖"));
        itemList.add(new OptionItem( 2,"2", "二等奖"));
        itemList.add(new OptionItem( 3,"3", "三等奖"));
        itemList.add(new OptionItem( 4,"4", "其他"));
        return new OptionItemList(0, itemList);
    }
    public DataResponse getComList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer subjectId = dataRequest.getInteger("subjectId");
        Integer level = dataRequest.getInteger("level");
        List<Competition> sList = competitionRepository.findStudentSubjectLevel(personId, subjectId, level);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Competition s : sList) {
            m = new HashMap<>();
            m.put("competitionId",s.getCompetitionId());
            m.put("subjectId",s.getCompetitionId());
            m.put("personId",s.getStudent().getPersonId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("competitionName",s.getCompetition_name());
            m.put("subjectName",s.getSubject().getName());
            m.put("level",trans(s.getLevel()));
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse competitionSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer subjectId = dataRequest.getInteger("subjectId");
        Integer level = dataRequest.getInteger("level");
        String competitionName = dataRequest.getString("competitionName");
        Integer competitionId = dataRequest.getInteger("competitionId");
        Optional<Competition> op;
        Competition s = null;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);
            if(op.isPresent())
                s = op.get();
        }
        if(s == null) {
            s = new Competition();
            s.setStudent(studentRepository.findById(personId).get());
            s.setSubject(subjectRepository.findById(subjectId).get());
        }
        s.setCompetition_name(competitionName);
        s.setLevel(level);
        competitionRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse competitionDelete(DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");
        Optional<Competition> op;
        Competition s = null;
        if(competitionId != null) {
            op= competitionRepository.findById(competitionId);
            if(op.isPresent()) {
                s = op.get();
                competitionRepository.delete(s);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
    String trans(int x){
        String[] A={"一","二","三"};
        if(x==4)
            return "其他";
        else
            return A[x-1]+"等奖";
    }
}
@Repository
interface SubjectRepository extends JpaRepository<Subject,Integer>{}