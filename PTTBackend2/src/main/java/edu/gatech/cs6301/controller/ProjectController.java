package edu.gatech.cs6301.controller;

import edu.gatech.cs6301.entity.Project;
import edu.gatech.cs6301.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value="/users/{userId}/projects")
public class ProjectController {

    private final ProjectService projectService;
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(path="")
    public ResponseEntity<List<Project>> getProjects(@PathVariable("userId") String pathUserid) {
        List<Project> projects = projectService.getProjects(pathUserid);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @PostMapping(path="", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project, @PathVariable("userId") String pathUserid) {
        Project newProject = projectService.createProject(pathUserid, project);
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }

    @GetMapping(path="/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable("userId") String pathUserid, @PathVariable("projectId") String pathProjectid) {
        Project project = projectService.getProjectById(pathUserid, pathProjectid);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @DeleteMapping(path="/{projectId}")
    public ResponseEntity<Project> deleteProjectById(@PathVariable("userId") String pathUserid, @PathVariable("projectId") String pathProjectid) {
        Project project = projectService.deleteProjectById(pathUserid, pathProjectid);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }
}
