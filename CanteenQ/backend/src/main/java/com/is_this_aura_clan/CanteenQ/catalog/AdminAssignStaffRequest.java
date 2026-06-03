package com.is_this_aura_clan.CanteenQ.catalog;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminAssignStaffRequest(
    @NotBlank(message = "staffEmail must not be blank")
    @Email(message = "staffEmail must be a valid email address")
    String staffEmail
) {}
