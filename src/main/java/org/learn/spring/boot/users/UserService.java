package org.learn.spring.boot.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

// All class method return values and consume parameters cannot be null
// because we defined @NullMarked in package-info that means they cannot be null
@Service
public class UserService {

    private List<User> users = new ArrayList<>();

    // As you first create method that return null value the IDE shows
    // that it cannot be return null value,
    // but if you want return explicitly null value we should use @Nullable from jspecify
    // here in this method we annotated with @Nullable primarily that means it can return null
    public @Nullable User findByEmail(String email) {
        return users.stream()
            .filter(user -> user.email().equalsIgnoreCase(email))
            .findFirst()
            .orElse(null);
    }


    public Optional<User> findByEmailByOptional(String email) {
        return users.stream()
            .filter(user -> user.email().equalsIgnoreCase(email))
            .findFirst();
    }

}
