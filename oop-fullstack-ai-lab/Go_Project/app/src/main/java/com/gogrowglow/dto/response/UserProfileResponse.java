package com.gogrowglow.dto.response;

public class UserProfileResponse {
    private Long id;
    private String email;
    private String username;

    public UserProfileResponse(Long id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
