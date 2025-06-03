package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    // 根据课序号查询课程
    Course findByCourseCode(String courseCode);

    // 根据课程名称模糊查询
    @Query("SELECT c FROM Course c WHERE c.name LIKE %:name%")
    List<Course> findByNameContaining(String name);

    // 根据课程类型查询
    List<Course> findByType(String type);

    // 综合查询（课序号/名称/类型）
    @Query("SELECT c FROM Course c WHERE " +
            "(:courseCode IS NULL OR c.courseCode = :courseCode) AND " +
            "(:name IS NULL OR c.name LIKE %:name%) AND " +
            "(:type IS NULL OR c.type = :type)")
    List<Course> findByCriteria(String courseCode, String name, String type);

}