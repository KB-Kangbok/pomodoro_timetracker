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
    public Session(Integer userid, Integer projectid, Timestamp startTime, String startTimeZone, Timestamp endTime, String endTimeZone, Integer counter) {
        this.userid = userid;
        this.projectid = projectid;
        this.startTime = startTime;
        this.startTimeZone = startTimeZone;
        this.endTime = endTime;
        this.endTimeZone = endTimeZone;
        this.counter = counter;
    }

    public Session(Integer userid, Integer projectid, Timestamp startTime) {
        this(userid, projectid, startTime, "UTC", null, null, 0);
    }

    public Session(Integer userid, Integer projectid, Timestamp startTime, Integer counter) {
        this(userid, projectid, startTime, "UTC", null, null, counter);
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

    @NotBlank
    @Column(name="starttimezone")
    private String startTimeZone;

    @Column(name="endtime")
    private Timestamp endTime;

    @Column(name="endtimezone")
    private String endTimeZone;

    @NotNull
    private Integer counter;
}
