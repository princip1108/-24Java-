package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;

@Entity
@Table(name = "competition_guide")
public class CompetitionGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer competitionId;

    @Column(nullable = false, length = 100)
    private String competitionName;

    // Getters and Setters
    public Integer getCompetitionId() { return competitionId; }
    public void setCompetitionId(Integer competitionId) { this.competitionId = competitionId; }
    public String getCompetitionName() { return competitionName; }
    public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }
}