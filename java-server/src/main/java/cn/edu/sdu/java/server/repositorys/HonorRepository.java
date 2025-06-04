package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorRepository extends JpaRepository<Honor, Integer> {
    List<Honor> findByStudentPersonId(Integer personId);

    @Query("from Honor where (?1=0 or student.personId=?1) and (?2='' or honorLevel like %?2%)")
    List<Honor> findByStudentAndLevel(Integer personId, String honorLevel);

    @Query("from Honor where ?1='' or student.person.num like %?1% or student.person.name like %?1%")
    List<Honor> findHonorListByName(String numName);
}