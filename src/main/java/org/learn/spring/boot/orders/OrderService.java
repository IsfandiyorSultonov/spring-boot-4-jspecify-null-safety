package org.learn.spring.boot.orders;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public Order createOrder(String email, @Nullable String promoCode) {

        // not needed to validate email to null
        // because we marked it as non-null
        sendEmailOrderConfirmation(email);

        // promo code can be null because
        // we defined as @Nullable that means it can be null
        if (promoCode != null) {
            applyPromoCode(promoCode);
        }

        return new Order(email, promoCode);
    }

    void sendEmailOrderConfirmation(String email) {
        IO.println("Sending email to " + email);
    }

    void applyPromoCode(String promoCode) {
        IO.println("Applying promo code to " + promoCode);
    }
}
