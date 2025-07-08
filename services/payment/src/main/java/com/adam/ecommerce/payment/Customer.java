package com.adam.ecommerce.payment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.aspectj.bridge.IMessage;
import org.springframework.validation.annotation.Validated;

@Validated
public record Customer(

        Integer id,
        @NotNull(message="Customer first Name is required")
        String firstName,
        @NotNull(message="Customer last Name is required")
        String lastName,
        @NotNull(message="Customer email is required")
        @Email(message="Customer email must be valid")
        String email
) {


}
