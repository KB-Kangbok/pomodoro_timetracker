package edu.gatech.cs6301.service;

import edu.gatech.cs6301.repository.ProjectRepository;
import edu.gatech.cs6301.repository.UserRepository;
import edu.gatech.cs6301.entity.Project;
import edu.gatech.cs6301.exception.ResourceConflictException;
import edu.gatech.cs6301.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getProjects(String pathUserid) {
        Integer userid = Integer.parseInt(pathUserid);
        if (userRepository.existsUserById(userid)) {
            return projectRepository.findProjectsByUserid(userid);
        } else {
            throw new ResourceNotFoundException("User Not Found");
        }
    }

    public Project getProjectById(String pathUserid, String pathProjectid) {
        Integer userid = Integer.parseInt(pathUserid);
        Integer projectid = Integer.parseInt(pathProjectid);
        if (projectRepository.existsProjectByUseridAndId(userid, projectid)) {
            return projectRepository.findProjectById(projectid);
        } else {
            throw new ResourceNotFoundException("User, Project Not Found");
        }
    }

    public Project deleteProjectById(String pathUserid, String pathProjectid) {
        Integer userid = Integer.parseInt(pathUserid);
        Integer projectid = Integer.parseInt(pathProjectid);
        if (projectRepository.existsProjectByUseridAndId(userid, projectid)) {
            Project project = projectRepository.findProjectById(projectid);
            projectRepository.deleteById(projectid);
            return project;
        } else {
            throw new ResourceNotFoundException("User, Project Not Found");
        }
    }

    public Project createProject(String pathUserid, Project project) {
        Integer userid = Integer.parseInt(pathUserid);
        if (userRepository.existsUserById(userid)) {
            if (projectRepository.existsProjectByUseridAndProjectname(userid, project.getProjectname())) {
                throw new ResourceConflictException("Project Name Existed for User");
            } else {
                Project newProject = new Project(userid, project.getProjectname());
                projectRepository.save(newProject);
                return newProject;
            }
        } else {
            throw new ResourceNotFoundException("User Not Found");
        }
    }
}
