package org.learn.spring.boot.users;

public record User(
    String firstname,
    String lastname,
    String email
) {
}
