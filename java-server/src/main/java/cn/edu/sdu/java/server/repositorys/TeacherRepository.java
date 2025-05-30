package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Optional<Teacher> findByPersonNum(String num);


    Optional<Teacher> findByPersonPersonId(Integer personId);


    List<Teacher> findByPersonName(String name);


    @Query("SELECT t FROM Teacher t WHERE ?1 = '' OR t.person.num LIKE %?1% OR t.person.name LIKE %?1%")
    List<Teacher> findTeacherListByNumName(String numName);


    @Query(value = "SELECT t FROM Teacher t WHERE ?1 = '' OR t.person.num LIKE %?1% OR t.person.name LIKE %?1%",
           countQuery = "SELECT COUNT(t.personId) FROM Teacher t WHERE ?1 = '' OR t.person.num LIKE %?1% OR t.person.name LIKE %?1%")
    Page<Teacher> findTeacherPageByNumName(String numName, Pageable pageable);
}