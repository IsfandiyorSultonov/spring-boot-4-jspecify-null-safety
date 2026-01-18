package org.learn.spring.boot.users;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@NullMarked // Explicitly defining nullness [non-null] service parameter or method return type
public class ServiceLayerNullMarked {

    private List<User> users = new ArrayList<>();

    // if we don't use @Nullable annotation here build won't be success
    // because we defined @NullMarked to this service
    public @Nullable User getUserByEmail(String email){
        return users.stream().filter(user -> user.email().equals(email))
            .findFirst()
            .orElse(null);
    }

}
