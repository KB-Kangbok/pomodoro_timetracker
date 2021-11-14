package edu.gatech.cs6301.service;

import edu.gatech.cs6301.entity.Session;
import edu.gatech.cs6301.exception.ResourceNotFoundException;
import edu.gatech.cs6301.model.Report;
import edu.gatech.cs6301.model.ReportSession;
import edu.gatech.cs6301.repository.SessionRepository;
import edu.gatech.cs6301.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public Report getReport(String pathUserid, String pathProjectid, String pathStartingtime, String pathEndingtime) {
        Integer userid = Integer.parseInt(pathUserid);
        Integer projectid = Integer.parseInt(pathProjectid);
        Timestamp startingTime = DateTimeConverter.toSQLTimestampUTC(pathStartingtime);
        Timestamp endingTime = DateTimeConverter.toSQLTimestampUTC(pathEndingtime);
        if (projectRepository.existsProjectByUseridAndId(userid, projectid)) {
            List<Session> sessionList = sessionRepository.getSessionReports(userid, projectid, startingTime, endingTime);
            return generateReportFromSessionList(sessionList);
        } else {
            throw new ResourceNotFoundException("User, Project Not Found");
        }
    }

    private Report generateReportFromSessionList(List<Session> sessionList) {
        List<Integer> counterList = new ArrayList<>();
        List<Double> hoursWorkedOnProjectList = new ArrayList<>();
        List<ReportSession> reportSessionList = new ArrayList<>();
        sessionList.forEach(session -> {
            counterList.add(session.getCounter());
            ReportSession reportSession = generateReportSessionFromSession(session);
            reportSessionList.add(reportSession);
            hoursWorkedOnProjectList.add(reportSession.getHoursWorked());
        });
        return new Report(reportSessionList, sumFromIntegerList(counterList), sumFromDoubleList(hoursWorkedOnProjectList));
    }

    private ReportSession generateReportSessionFromSession(Session session) {
        return new ReportSession(DateTimeConverter.toISO8601(session.getStartTime()), DateTimeConverter.toISO8601(session.getEndTime()), getDuration(session.getStartTime(), session.getEndTime()));
    }

    private Integer sumFromIntegerList(List<Integer> inputList) {
        return inputList.stream().mapToInt(Integer::intValue).sum();
    }

    private Double sumFromDoubleList(List<Double> inputList) {
        return inputList.stream().mapToDouble(Double::doubleValue).sum();
    }

    private Double getDuration(Timestamp startTime, Timestamp endTime) {
        double durationMS = (double) (endTime.getTime() - startTime.getTime());
        return durationMS / 3.6E+6;
    }
}
