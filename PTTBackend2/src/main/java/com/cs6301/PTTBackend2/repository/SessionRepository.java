package com.cs6301.PTTBackend2.repository;

import com.cs6301.PTTBackend2.entity.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface SessionRepository extends CrudRepository<Session, Integer> {

    List<Session> findSessionsByUseridAndProjectid(Integer userid, Integer projectid);

    boolean existsSessionByUseridAndProjectidAndId(Integer userid, Integer projectid, Integer sessionid);

    Session findSessionById(Integer sessionid);

    @Query(value = "SELECT * FROM session s WHERE s.userid = ?1 AND s.projectid = ?2 AND (starttime < ?4 OR endtime > ?3)", nativeQuery = true)
    List<Session> getSessionReports(Integer userid, Integer projectid, Timestamp startingTime, Timestamp endingTime);
}
