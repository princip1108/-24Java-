package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * Teacher教师表实体类 保存教师的专业信息
 * Integer personId 教师表 teacher 主键 person_id 与Person表主键相同
 * Person person 关联到该教师所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String title 职称
 * String degree 学位
 * Integer studentNum 管理学生数量
 * Date enterTime 入职时间
 */
@Entity
@Table(name = "teacher")
public class Teacher {
    @Id
    private Integer personId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    @JsonIgnore
    private Person person;

    @Size(max = 50)
    private String name;  // 添加缺失的name字段

    @Size(max = 50)
    private String title;

    @Size(max = 50)
    private String degree;

    private Integer studentNum;

    @Temporal(TemporalType.DATE)
    private Date enterTime;

    // 默认构造函数
    public Teacher() {
    }

    // 带参构造函数
    public Teacher(Integer personId) {
        this.personId = personId;
    }

    // Getter和Setter方法
    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public Integer getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(Integer studentNum) {
        this.studentNum = studentNum;
    }

    public Date getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "personId=" + personId +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", degree='" + degree + '\'' +
                ", studentNum=" + studentNum +
                ", enterTime=" + enterTime +
                '}';
    }
}