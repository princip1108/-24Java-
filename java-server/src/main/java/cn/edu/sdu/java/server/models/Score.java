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
    private Integer scoreId;

    @ManyToOne
    @JoinColumn(name ="person_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Double scoreValue;

    private String grade;


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