package edu.gatech.cs6301.service;

//<<<<<<< HEAD:PTTBackend2/src/main/java/com/cs6301/PTTBackend2/service/ReportService.java
//import com.cs6301.PTTBackend2.entity.Session;
//import com.cs6301.PTTBackend2.exception.InvalidRequestBodyException;
//import com.cs6301.PTTBackend2.exception.ResourceNotFoundException;
//import com.cs6301.PTTBackend2.model.Report;
//import com.cs6301.PTTBackend2.model.ReportSession;
//import com.cs6301.PTTBackend2.repository.SessionRepository;
//import com.cs6301.PTTBackend2.repository.ProjectRepository;
//=======
import edu.gatech.cs6301.entity.Session;
import edu.gatech.cs6301.exception.ResourceNotFoundException;
import edu.gatech.cs6301.model.Report;
import edu.gatech.cs6301.model.ReportSession;
import edu.gatech.cs6301.repository.SessionRepository;
import edu.gatech.cs6301.repository.ProjectRepository;
//>>>>>>> origin/backend-jipeng:PTTBackend2/src/main/java/edu/gatech/cs6301/service/ReportService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public Report getReport(String pathUserid, String pathProjectid, String pathStartingtime, String pathEndingtime, Boolean includeCompletedPomodoros, Boolean includeTotalHoursWorkedOnProject) {
        Integer userid = Integer.parseInt(pathUserid);
        Integer projectid = Integer.parseInt(pathProjectid);
        Timestamp startingTime = DateTimeConverter.toSQLTimestampUTC(pathStartingtime);
        Timestamp endingTime = DateTimeConverter.toSQLTimestampUTC(pathEndingtime);
        if(startingTime.after(endingTime)){
            throw new IllegalArgumentException("Ending time is earlier than starting time.");
        }
        if (projectRepository.existsProjectByUseridAndId(userid, projectid)) {
            List<Session> sessionList = sessionRepository.getSessionReports(userid, projectid, startingTime, endingTime);
            return generateReportFromSessionList(sessionList, includeCompletedPomodoros, includeTotalHoursWorkedOnProject);
        } else {
            throw new ResourceNotFoundException("User, Project Not Found");
        }
    }

    private Report generateReportFromSessionList(List<Session> sessionList, Boolean includeCompletedPomodoros, Boolean includeTotalHoursWorkedOnProject) {
        List<Integer> counterList = new ArrayList<>();
        List<Double> hoursWorkedOnProjectList = new ArrayList<>();
        List<ReportSession> sessions = new ArrayList<>();
        sessionList.forEach(session -> {
            counterList.add(session.getCounter());
            ReportSession reportSession = generateReportSessionFromSession(session);
            sessions.add(reportSession);
            hoursWorkedOnProjectList.add(reportSession.getHoursWorked());
        });
        Double totalHoursWorkedOnProject = (includeTotalHoursWorkedOnProject != null && includeTotalHoursWorkedOnProject) ? sumFromDoubleList(hoursWorkedOnProjectList) : null;
        Integer completedPomodoros = (includeCompletedPomodoros != null && includeCompletedPomodoros) ? sumFromIntegerList(counterList) : null;
        return new Report(sessions, completedPomodoros, totalHoursWorkedOnProject);
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
