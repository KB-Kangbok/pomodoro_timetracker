package com.cs6301.PTTBackend2.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@JsonIgnoreProperties(ignoreUnknown = false)
@Table(name="session")
@NoArgsConstructor
@Setter
@Getter
public class Session {
    public Session(Integer userid, Integer projectid, Timestamp startTime, Timestamp endTime, Integer counter) {
        this.userid = userid;
        this.projectid = projectid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.counter = counter;
    }

    public Session(Integer userid, Integer projectid, Timestamp startTime) {
        this(userid, projectid, startTime, null, 0);
    }

    public Session(Integer userid, Integer projectid, Timestamp startTime, Integer counter) {
        this(userid, projectid, startTime, null, counter);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sessionid")
    private Integer id;

    private Integer userid;

    private Integer projectid;

    @NotNull
    @Column(name="starttime")
    private Timestamp startTime;

    @Column(name="endtime")
    private Timestamp endTime;

    @NotNull
    private Integer counter;
}
