package org.learn.spring.boot.menu;

import jakarta.annotation.Nullable;

public record CoffeeRequest(
    String coffeeType,              // non-null
    Long quantityOfBeen,            // non-null
    @Nullable Long quantityOfMilk   // can be null
) {

    public CoffeeRequest {
        if (quantityOfBeen == null) {
            throw new IllegalArgumentException("quantityOfBeen is null");
        }
        if (coffeeType == null) {
            throw new IllegalArgumentException("coffeeType is null");
        }
    }
}
