package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByCourseCode(String courseCode);
    @Query("SELECT c FROM Course c WHERE c.name LIKE %:name%")
    List<Course> findByNameContaining(String name);
    List<Course> findByType(String type);
    @Query("SELECT c FROM Course c WHERE " +
            "(:courseCode IS NULL OR c.courseCode = :courseCode) AND " +
            "(:name IS NULL OR c.name LIKE %:name%) AND " +
            "(:type IS NULL OR c.type = :type)")
    List<Course> findByCriteria(String courseCode, String name, String type);

}