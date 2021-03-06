package edu.gatech.cs6301.service;

import edu.gatech.cs6301.model.SessionHttp;
import edu.gatech.cs6301.repository.SessionRepository;
import edu.gatech.cs6301.repository.ProjectRepository;
import edu.gatech.cs6301.entity.Session;
import edu.gatech.cs6301.exception.InvalidRequestBodyException;
import edu.gatech.cs6301.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<SessionHttp> getSessions(String pathUserid, String pathProjectid) {
        Integer userid = Integer.parseInt(pathUserid);
        Integer projectid = Integer.parseInt(pathProjectid);
        if (projectRepository.existsProjectByUseridAndId(userid, projectid)) {
            List<Session> sessions = sessionRepository.findSessionsByUseridAndProjectid(userid, projectid);
            List<SessionHttp> sessionHttps = new ArrayList<>();
            sessions.forEach(session -> {
                sessionHttps.add(generateSessionHttpFromSession(session));
            });
            return sessionHttps;
        } else {
            throw new ResourceNotFoundException("User, Project Not Found");
        }
    }

    public SessionHttp createSession(String pathUserid, String pathProjectid, SessionHttp sessionHttp) {
        Integer userid = Integer.parseInt(pathUserid);
        Integer projectid = Integer.parseInt(pathProjectid);
        if (projectRepository.existsProjectByUseridAndId(userid, projectid)) {
            Session newSession = generateSessionFromSessionHttp(userid, projectid, sessionHttp);
            sessionRepository.save(newSession);
            return generateSessionHttpFromSession(newSession);
        } else {
            throw new ResourceNotFoundException("User, Project Not Found");
        }
    }

    public SessionHttp updateSession(String pathUserid, String pathProjectid, String pathSessionid, SessionHttp sessionHttp) {
        Integer userid = Integer.parseInt(pathUserid);
        Integer projectid = Integer.parseInt(pathProjectid);
        Integer sessionid = Integer.parseInt(pathSessionid);
        if (sessionRepository.existsSessionByUseridAndProjectidAndId(userid, projectid, sessionid)) {
            Session updatedSession = generateSessionFromSessionHttp(userid, projectid, sessionid, sessionHttp);
            sessionRepository.save(updatedSession);
            return generateSessionHttpFromSession(updatedSession);
        } else {
            throw new ResourceNotFoundException("User, Project, Session Not Found");
        }
    }

    private SessionHttp generateSessionHttpFromSession(Session session) {
        return new SessionHttp(session.getId(), DateTimeConverter.toISO8601(session.getStartTime()), DateTimeConverter.toISO8601(session.getEndTime()), session.getCounter());
    }

    private Session generateSessionFromSessionHttp(Integer userid, Integer projectid, Integer sessionid, SessionHttp sessionHttp) {
        if (validateSessionHttpUpdate(userid, sessionHttp)) {
            if (sessionRepository.existsSessionByUseridAndProjectidAndId(userid, projectid, sessionid)) {
                Session updatedSession = sessionRepository.findSessionById(sessionid);
                updatedSession.setStartTime(DateTimeConverter.toSQLTimestampUTC(sessionHttp.getStartTime()));
                updatedSession.setEndTime(DateTimeConverter.toSQLTimestampUTC(sessionHttp.getEndTime()));
                updatedSession.setCounter(sessionHttp.getCounter());
                return updatedSession;

            } else {
                throw new ResourceNotFoundException("User, Project, Session Not Found");
            }
        } else {
            throw new InvalidRequestBodyException("Invalid Session Request Body (counter value, startTime/endTime format)");
        }
    }

    private Session generateSessionFromSessionHttp(Integer userid, Integer projectid, SessionHttp sessionHttp) {
        if (validateSessionHttp(userid, sessionHttp)) {
            if (projectRepository.existsProjectByUseridAndId(userid, projectid)) {
                return generateSessionFromSessionHttpHelper(userid, projectid, sessionHttp);
            } else {
                throw new ResourceNotFoundException("User, Project Not Found");
            }
        } else {
            throw new InvalidRequestBodyException("Invalid Session Request Body (counter value, startTime/endTime format)");
        }
    }

    private boolean validateSessionHttp(Integer userid, SessionHttp sessionHttp) {
        try {
            Timestamp startTimeUTC = DateTimeConverter.toSQLTimestampUTC(sessionHttp.getStartTime());
            Timestamp endTimeUTC = DateTimeConverter.toSQLTimestampUTC(sessionHttp.getEndTime());
            long diffMilisec = endTimeUTC.getTime() - startTimeUTC.getTime();
            List<Session> sessionList = sessionRepository.getOverlappingSessions(userid, startTimeUTC, endTimeUTC);
            return sessionHttp.getCounter() >= 0 && startTimeUTC.before(endTimeUTC) && diffMilisec >= sessionHttp.getCounter() * 30 * 60 * 1000 && sessionList.size() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateSessionHttpUpdate(Integer userid, SessionHttp sessionHttp) {
        try {
            Timestamp startTimeUTC = DateTimeConverter.toSQLTimestampUTC(sessionHttp.getStartTime());
            Timestamp endTimeUTC = DateTimeConverter.toSQLTimestampUTC(sessionHttp.getEndTime());
            long diffMilisec = endTimeUTC.getTime() - startTimeUTC.getTime();
            //List<Session> sessionList = sessionRepository.getOverlappingSessions(userid, startTimeUTC, endTimeUTC);
            return sessionHttp.getCounter() >= 0 && startTimeUTC.before(endTimeUTC) && diffMilisec >= sessionHttp.getCounter() * 30 * 60 * 1000;
        } catch (Exception e) {
            return false;
        }
    }

    private Session generateSessionFromSessionHttpHelper(Integer userid, Integer projectid, SessionHttp sessionHttp) {
        return new Session(userid, projectid,
                DateTimeConverter.toSQLTimestampUTC(sessionHttp.getStartTime()),
                DateTimeConverter.toSQLTimestampUTC(sessionHttp.getEndTime()),
                sessionHttp.getCounter());
    }
}
