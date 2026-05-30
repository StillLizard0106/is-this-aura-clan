package com.is_this_aura_clan.CanteenQ.auth;

public class InsufficientRoleException extends RuntimeException {

	public InsufficientRoleException(String message) {
		super(message);
	}
}
