package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Teacher;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository {
    Optional<Teacher> findByPersonNum(String num);
    @Query(value = "from Teacher where ?1='' or person.num like %?1% or person.name like %?1% ")
List<Teacher> findTeacherListByNumName(String numName);

