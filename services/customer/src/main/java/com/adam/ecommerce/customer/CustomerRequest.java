package com.adam.ecommerce.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(

        String id,
        @NotNull(message="Costumer first Name is required")
        String firstName,
        @NotNull(message="Costumer last Name is required")
        String lastName,
        @NotNull(message="Costumer email is required")
        @Email(message="Costumer email must be valid")
        String email,
        
        Address adress
) {


}
