package edu.gatech.cs6301.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
@Setter
@Getter
public class Report {
    private List<ReportSession> sessions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer completedPomodoros;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double totalHoursWorkedOnProject;
}
