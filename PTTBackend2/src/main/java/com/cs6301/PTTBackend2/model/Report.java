package com.cs6301.PTTBackend2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.cs6301.PTTBackend2.model.ReportSession;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
@Setter
@Getter
public class Report {
    private List<ReportSession> reportSessionList;
    private Integer completedPomodoros;
    private Double totalHoursWorkedOnProject;
}
