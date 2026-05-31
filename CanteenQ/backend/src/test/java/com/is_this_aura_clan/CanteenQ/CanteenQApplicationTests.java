package com.is_this_aura_clan.CanteenQ;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
/**
 * Represents a structured error response returned to the client
 * whenever an exception or validation failure occurs in the API.
 *
 * Follows the Single Responsibility Principle — solely responsible
 * for shaping the error payload returned to consumers.
 */
public record ErrorResponse( int status,
                             String code,
                             String message,
                             String path,

                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                             LocalDateTime timestamp
                             ) {
    /**
     * Compact canonical constructor with input validation (Encapsulation).
     * Ensures no ErrorResponse is ever created with null/blank critical fields.
     */
    public ErrorResponse {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Error code must not be blank");
        if (message == null || message.isBlank()) throw new IllegalArgumentException("Message must not be blank");
    }

    /**
     * Convenience factory method — auto-fills timestamp.
     * Reduces boilerplate at every call site (DRY).
     */
    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(status, code, message, path, LocalDateTime.now());
    }

}
