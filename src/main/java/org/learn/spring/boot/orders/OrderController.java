package org.learn.spring.boot.orders;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {this.orderService = orderService;}

    @PostMapping
    public Order createOrder(@RequestParam(required = true) String email,
                             // Promo code we defined that it can be null
                             @RequestParam(required = false) String promoCode) {
        Order order = orderService.createOrder(email, promoCode);

        // build will not be success if we don't check promoCode to null
        // because we everywhere defined as it can be null
        if (order.promoCode() != null && order.promoCode().equals(promoCode)) {
            IO.println("Order created");
        }
        return order;
    }

}
