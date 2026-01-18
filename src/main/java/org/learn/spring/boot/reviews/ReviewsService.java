package org.learn.spring.boot.reviews;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class ReviewsService {

    // Collections with Nullable Elements

    // Customer may skip some questions so we defined
    // some of the messages as null
    public List<@Nullable String> getReviewMessage() {
        List<@Nullable String> reviews = new ArrayList<>();
        reviews.add("hello world");
        reviews.add(null);
        reviews.add("review world");
        reviews.add(null);
        reviews.add("close world");

        return reviews;
    }
}
