package edu.gatech.cs6301.controller;

import edu.gatech.cs6301.model.SessionHttp;
import edu.gatech.cs6301.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value="/users/{userId}/projects/{projectId}/sessions")
public class SessionController {
    private final SessionService sessionService;
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping(path="")
    public ResponseEntity<List<SessionHttp>> getSessions(@PathVariable("userId") String pathUserid, @PathVariable("projectId") String pathProjectid) {
        List<SessionHttp> sessions = sessionService.getSessions(pathUserid, pathProjectid);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }

    @PostMapping(path="", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionHttp> createSession(@PathVariable("userId") String pathUserid, @PathVariable("projectId") String pathProjectid, @Valid @RequestBody SessionHttp sessionHttp) {
        SessionHttp newSession = sessionService.createSession(pathUserid, pathProjectid, sessionHttp);
        return new ResponseEntity<>(newSession, HttpStatus.CREATED);
    }

    @PutMapping(path="/{sessionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionHttp> updateSession(@PathVariable("userId") String pathUserid, @PathVariable("projectId") String pathProjectid, @PathVariable("sessionId") String pathSessionid, @Valid @RequestBody SessionHttp sessionHttp) {
        SessionHttp updatedSession = sessionService.updateSession(pathUserid, pathProjectid, pathSessionid, sessionHttp);
        return new ResponseEntity<>(updatedSession, HttpStatus.CREATED);
    }
}
