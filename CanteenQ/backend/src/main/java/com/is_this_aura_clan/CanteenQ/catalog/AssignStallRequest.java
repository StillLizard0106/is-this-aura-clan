package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignStallRequest(
	@NotNull(message = "stallId must not be null")
	UUID stallId
) {}
