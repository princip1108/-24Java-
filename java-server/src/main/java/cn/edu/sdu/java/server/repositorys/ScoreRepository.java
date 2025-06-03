package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {

    // 根据学生ID查询成绩
    List<Score> findByStudentPersonId(Integer personId);

    // 根据课程ID查询成绩
    List<Score> findByCourseCourseId(Integer courseId);

    // 根据学生ID和课程ID查询成绩
    @Query("SELECT s FROM Score s WHERE " +
            "(:studentId IS NULL OR s.student.personId = :studentId) AND " +
            "(:courseId IS NULL OR s.course.courseId = :courseId)")
    List<Score> findByStudentIdAndCourseId(Integer studentId, Integer courseId);

    // 根据学生学号或姓名模糊查询成绩
    @Query("SELECT s FROM Score s WHERE " +
            "(:numName = '' OR s.student.person.num LIKE %:numName% OR " +
            "s.student.person.name LIKE %:numName%)")
    List<Score> findScoreListByNumName(String numName);

    // 根据课程名称模糊查询成绩
    @Query("SELECT s FROM Score s WHERE " +
            "(:name = '' OR s.course.name LIKE %:name%)")
    List<Score> findScoreListByCourseName(String name);


}