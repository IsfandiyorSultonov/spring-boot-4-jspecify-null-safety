package org.learn.spring.boot.users;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {this.userService = userService;}

    @GetMapping("/get-user-by-email")
    public User getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);

        if (user != null && user.firstname().equals("dev")) {
            IO.println("He is Developer from 'OCTO' LLC. He is Spring learner");
            return user;
        }

        throw new RuntimeException("User not found");
    }

    @GetMapping("/get-user-by-email-optional")
    public ResponseEntity<User> getUserByEmailOptional(@RequestParam String email) {
        return userService.findByEmailByOptional(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}
