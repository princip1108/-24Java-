package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {
    List<Score> findByStudentPersonId(Integer personId);
    List<Score> findByCourseCourseId(Integer courseId);
    @Query("SELECT s FROM Score s WHERE " +
            "(:studentId IS NULL OR s.student.personId = :studentId) AND " +
            "(:courseId IS NULL OR s.course.courseId = :courseId)")
    List<Score> findByStudentIdAndCourseId(Integer studentId, Integer courseId);
    @Query("SELECT s FROM Score s WHERE " +
            "(:numName = '' OR s.student.person.num LIKE %:numName% OR " +
            "s.student.person.name LIKE %:numName%)")
    List<Score> findScoreListByNumName(String numName);
    @Query("SELECT s FROM Score s WHERE " +
            "(:name = '' OR s.course.name LIKE %:name%)")
    List<Score> findScoreListByCourseName(String name);
}