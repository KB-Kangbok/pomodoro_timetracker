package edu.gatech.cs6301.repository;

import edu.gatech.cs6301.entity.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface SessionRepository extends CrudRepository<Session, Integer> {

    List<Session> findSessionsByUseridAndProjectid(Integer userid, Integer projectid);

    boolean existsSessionByUseridAndProjectidAndId(Integer userid, Integer projectid, Integer sessionid);

    Session findSessionById(Integer sessionid);

    @Query(value = "SELECT * FROM session s WHERE s.userid = ?1 AND s.projectid = ?2 AND ((starttime < ?4 AND starttime > ?3) OR (endtime > ?3 AND endtime < ?4))", nativeQuery = true)
    List<Session> getSessionReports(Integer userid, Integer projectid, Timestamp startingTime, Timestamp endingTime);
}
