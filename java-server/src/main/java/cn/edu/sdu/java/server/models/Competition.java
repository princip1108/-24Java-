package cn.edu.sdu.java.server.models;
//志愿活动实体类
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(	name = "competition",
        uniqueConstraints = {
        })
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer competitionId;

    @ManyToOne
    @JoinColumn(name = "personId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subjectId")
    private Subject subject;
    private String competition_name;
    private Integer level;//123一二三等4其他

}
