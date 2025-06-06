package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface VolunteerRepository extends JpaRepository<Volunteer,Integer> {
    List<Volunteer> findByStudentPersonId(Integer personId);
    // 根据学生ID和活动名称模糊查询
    @Query(value="from Volunteer where (?1=0 or student.personId=?1) and (?2=0 or volunteerId=?2)")
    List<Volunteer> findByStudentActivityName(Integer personId, Integer volunteerId);
    @Query(value = "from Volunteer where ?1='' or student.person.num like %?1% or student.person.name like %?1% ")
    List<Volunteer> findVolunteerListByName(String numName);
}
