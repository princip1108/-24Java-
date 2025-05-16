package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Teacher教师表实体类 保存每个教师的信息，
 * Integer personId 教师表 teacher 主键 person_id 与Person表主键相同
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String title 职称
 * String degree 学位
 * Date enterTime 入职时间
 * Integer studentNum 学生数量
 * String className 班级名称
 */
@Getter
@Setter
@Entity
@Table(name = "teacher",
        uniqueConstraints = {
        })
public class Teacher {
    /**
     * 教师ID，与Person表的personId相同
     */
    @Id
    private Integer personId;

    /**
     * 关联的Person对象，包含基本人员信息
     */
    @OneToOne
    @JoinColumn(name="personId")
    @JsonIgnore
    private Person person;

    /**
     * 职称，如教授、副教授、讲师等
     */
    @Size(max=50)
    private String title;

    /**
     * 学位，如学士、硕士、博士等
     */
    @Size(max=30)
    private String degree;

    /**
     * 入职时间
     */
    private Date enterTime;

    /**
     * 指导的学生数量
     */
    private Integer studentNum;

    /**
     * 所教班级名称
     */
    @Size(max = 50)
    private String className;

    /**
     * 获取教师的姓名（从关联的Person对象中获取）
     * @return 教师姓名
     */
    @Transient
    public String getName() {
        return person != null ? person.getName() : null;
    }

    /**
     * 获取教师的工号（从关联的Person对象中获取）
     * @return 教师工号
     */
    @Transient
    public String getNum() {
        return person != null ? person.getNum() : null;
    }

    /**
     * 获取教师所属学院（从关联的Person对象中获取）
     * @return 所属学院
     */
    @Transient
    public String getDept() {
        return person != null ? person.getDept() : null;
    }
}





