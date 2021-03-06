package edu.gatech.cs6301.service;

import edu.gatech.cs6301.exception.InvalidRequestBodyException;
import edu.gatech.cs6301.exception.ResourceConflictException;
import edu.gatech.cs6301.exception.ResourceNotFoundException;
import edu.gatech.cs6301.repository.UserRepository;
import edu.gatech.cs6301.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String pathUserid) {
        Integer userid = Integer.parseInt(pathUserid);
        if (userRepository.existsUserById(userid)) {
            return userRepository.findUserById(userid);
        } else {
            throw new ResourceNotFoundException("User Not Found");
        }
    }

    public User deleteUserById(String pathUserid) {
        Integer userid = Integer.parseInt(pathUserid);
        if (userRepository.existsUserById(userid)) {
            User user = userRepository.findUserById(userid);
            userRepository.deleteById(userid);
            return user;
        } else {
            throw new ResourceNotFoundException("User Not Found");
        }
    }

    public List<User> deleteAllUsers() {
        List<User> userList = userRepository.findAll();
        try{
            userRepository.deleteAll();
        }catch (Exception e){
            throw e;
        }
        return userList;
    }

    public User updateUser(User newUser, String pathUserid) {
        Integer userid = Integer.parseInt(pathUserid);
        if (userRepository.existsUserById(userid)) {
            User user = userRepository.findUserById(userid);
            if (newUser.getFirstName() != null) {
                user.setFirstName(newUser.getFirstName());
            }
            if (newUser.getLastName() != null) {
                user.setLastName(newUser.getLastName());
            }
            userRepository.save(user);
            return user;
        } else {
            throw new ResourceNotFoundException("User Not Found");
        }
    }

    public User addUser(User user) {
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ResourceConflictException("Email Existed");
        } else {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                String firstname = user.getFirstName() == null ? "" : user.getFirstName();
                String lastname = user.getLastName() == null ? "" : user.getLastName();
                User newUser = new User(firstname, lastname, user.getEmail());
                userRepository.save(newUser);
                return newUser;
            } else {
                throw new InvalidRequestBodyException("Invalid User Email");
            }
        }
    }
}
