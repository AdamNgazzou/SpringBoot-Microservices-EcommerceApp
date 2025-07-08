package com.adam.ecommerce.customer;

import jakarta.validation.constraints.Email;

public record CustomerResponse(
        String id ,
        String firstName,
        String lastName,
        String email
) {
}
