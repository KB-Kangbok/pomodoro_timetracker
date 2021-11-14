package edu.gatech.cs6301.controller;

import edu.gatech.cs6301.model.Report;
import edu.gatech.cs6301.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/users/{userId}/projects/{projectId}/report")
public class ReportController {
    private final ReportService reportService;
    public ReportController(ReportService reportService) { this.reportService = reportService; }

    @GetMapping(path="")
    public ResponseEntity<Report> getReport(@PathVariable("userId") String pathUserid, @PathVariable("projectId") String pathProjectid, @RequestParam String from, @RequestParam String to, @RequestParam(required = false) Boolean includeCompletedPomodoros, @RequestParam(required = false) Boolean includeTotalHoursWorkedOnProject) {
        Report report = reportService.getReport(pathUserid, pathProjectid, from, to);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }
}
