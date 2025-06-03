package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId; // 课程ID（主键）

    @Column(unique = true, nullable = false)
    private String courseCode; // 课序号（唯一）

    @Column(nullable = false)
    private String name; // 课程名称

    @Column(nullable = false)
    private Double credit; // 学分

    @Column(nullable = false)
    private String type; // 课程类型（必选/选修）



    // 可选：课程描述等其他字段
}