package com.cs6301.PTTBackend2.repository;

import com.cs6301.PTTBackend2.entity.Project;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Integer>{
    List<Project> findProjectsByUserid(Integer userid);

    boolean existsProjectByUseridAndId(Integer userid, Integer projectid);

    boolean existsProjectByUseridAndProjectname(Integer userid, String projectname);

    Project findProjectById(Integer projectid);

    void deleteById(Integer projectid);
}
