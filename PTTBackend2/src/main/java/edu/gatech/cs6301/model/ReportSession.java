package edu.gatech.cs6301.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
@Setter
@Getter
public class ReportSession {
    private String startingTime;
    private String endingTime;
    private Double hoursWorked;
}
