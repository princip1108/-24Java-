package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    // 根据工号或姓名模糊查询教师列表（分页）
    @Query("SELECT t FROM Teacher t JOIN t.person p " +
            "WHERE (:numName IS NULL OR :numName = '' OR " +
            "p.num LIKE %:numName% OR p.name LIKE %:numName%)")
    Page<Teacher> findTeacherListByNumName(@Param("numName") String numName, Pageable pageable);

    // 根据工号或姓名模糊查询教师列表（不分页）
    @Query("SELECT t FROM Teacher t JOIN t.person p " +
            "WHERE (:numName IS NULL OR :numName = '' OR " +
            "p.num LIKE %:numName% OR p.name LIKE %:numName%)")
    List<Teacher> findTeacherListByNumName(@Param("numName") String numName);

    // 根据personId查询教师信息
    Optional<Teacher> findByPersonId(Integer personId);

    // 根据人员编号查询教师信息
    @Query("SELECT t FROM Teacher t JOIN t.person p WHERE p.num = :num")
    Optional<Teacher> findByPersonNum(@Param("num") String num);

    // 统计教师数量
    @Query("SELECT COUNT(t) FROM Teacher t")
    long countAllTeachers();

    // 按学院统计教师数量
    @Query("SELECT COUNT(t) FROM Teacher t JOIN t.person p WHERE p.dept = :dept")
    long countByDepartment(@Param("dept") String dept);

    // 获取某个学院的所有教师
    @Query("SELECT t FROM Teacher t JOIN t.person p WHERE p.dept = :dept")
    List<Teacher> findByDepartment(@Param("dept") String dept);

    // 添加查询方法，按入职时间范围查询教师
    @Query("SELECT t FROM Teacher t WHERE t.entryDate BETWEEN :startDate AND :endDate")
    List<Teacher> findByEntryDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

