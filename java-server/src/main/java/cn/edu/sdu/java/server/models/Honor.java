package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "honor")
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorId;

    @ManyToOne
    @JoinColumn(name = "personId")
    private Student student;

    private String honorName;  // 荣誉名称
    private String honorLevel; // 荣誉级别
}