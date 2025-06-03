package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "score")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scoreId; // 成绩ID，主键

    @ManyToOne
    @JoinColumn(name ="person_id")
    private Student student; // 关联学生

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course; // 关联课程

    private Double scoreValue; // 成绩值

    private String grade; // 成绩等级（自动计算）

    // 在持久化之前和更新之前自动计算成绩等级
    @PrePersist
    @PreUpdate
    private void calculateGrade() {
        if (scoreValue == null) return;

        if (scoreValue >= 90) {
            grade = "A";
        } else if (scoreValue >= 80) {
            grade = "B";
        } else if (scoreValue >= 70) {
            grade = "C";
        } else if (scoreValue >= 60) {
            grade = "D";
        } else {
            grade = "F";
        }
    }
}