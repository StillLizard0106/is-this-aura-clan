package com.is_this_aura_clan.CanteenQ.catalog;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminStaffAssignmentResponse(
    UUID assignmentId,
    UUID staffId,
    String staffEmail,
    String staffName,
    LocalDateTime assignedAt
) {}
