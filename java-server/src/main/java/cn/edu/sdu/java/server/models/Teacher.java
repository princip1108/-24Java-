package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.Date;


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
    private String name;

    @Size(max = 50)
    private String title;

    @Size(max = 50)
    private String degree;

    private Integer studentNum;

    @Temporal(TemporalType.DATE)
    private Date enterTime;


    public Teacher() {
    }


    public Teacher(Integer personId) {
        this.personId = personId;
    }


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