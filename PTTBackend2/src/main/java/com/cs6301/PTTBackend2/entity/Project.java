package com.cs6301.PTTBackend2.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@JsonIgnoreProperties(ignoreUnknown = false)
@Table(name="project")
@NoArgsConstructor
@Setter
public class Project {
    public Project(Integer userid, String projectname){
        this.userid = userid;
        this.projectname = projectname;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="projectid")
    @Getter
    private Integer id;

    private Integer userid;

    @NotBlank
    @Getter
    private String projectname;
}
