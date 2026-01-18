package org.learn.spring.boot.orders;

import jakarta.annotation.Nullable;

public record Order(
    String email,
   // Promo code defined as nullable value
   @Nullable String promoCode
) {

}
