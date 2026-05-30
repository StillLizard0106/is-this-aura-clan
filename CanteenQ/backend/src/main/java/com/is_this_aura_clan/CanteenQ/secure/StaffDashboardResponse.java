package com.is_this_aura_clan.CanteenQ.secure;

import com.is_this_aura_clan.CanteenQ.account.UserRole;

public record StaffDashboardResponse(String message, String name, String email, UserRole role) {
}
