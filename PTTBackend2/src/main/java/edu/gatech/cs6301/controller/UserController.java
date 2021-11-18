package edu.gatech.cs6301.controller;

import edu.gatech.cs6301.entity.User;
import edu.gatech.cs6301.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value="/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path="")
    public @ResponseBody
    ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

   @PostMapping(path="", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        User newUser = userService.addUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
   }

   @GetMapping(path="/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") String pathUserid) {
        User user = userService.getUserById(pathUserid);
        return new ResponseEntity<>(user, HttpStatus.OK);
   }

   @PutMapping(path="/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@Valid @RequestBody User newUser, @PathVariable("userId") String pathUserid) {
        User updatedUser = userService.updateUser(newUser, pathUserid);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
   }

   @DeleteMapping(path="/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable("userId") String pathUserid) {
        User deletedUser = userService.deleteUserById(pathUserid);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
   }

    @DeleteMapping(path="")
    public ResponseEntity<List<User>> deleteAllUsers() {
        List<User> deletedUser = userService.deleteAllUsers();
        return new ResponseEntity<List<User>>(deletedUser, HttpStatus.OK);
    }
}
