package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.models.Subject;
import cn.edu.sdu.java.server.models.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition,Integer>{
    List<Volunteer> findByStudentPersonId(Integer personId);
    // 根据学生ID和活动名称模糊查询
    @Query(value="from Competition where (?1=0 or student.personId=?1) and (?2=0 or subject.subjectId=?2) and (?3=0 or level=?3)")
    List<Competition> findStudentSubjectLevel(Integer personId, Integer subjectId, Integer level);
    @Query(value = "from Subject where ?1='' or name like %?1% ")
    List<Subject> findSubjectListByName(String numName);
}
