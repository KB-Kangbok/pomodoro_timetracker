package edu.gatech.cs6301.repository;

import edu.gatech.cs6301.entity.User;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findAll();

    boolean existsUserById(Integer userid);

    boolean existsUserByEmail(String email);

    User findUserById(Integer userid);

    void deleteById(Integer userid);
}
