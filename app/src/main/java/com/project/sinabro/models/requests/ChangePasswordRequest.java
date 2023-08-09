package com.project.sinabro.models.requests;

public class ChangePasswordRequest {
    private String password;

    public ChangePasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
