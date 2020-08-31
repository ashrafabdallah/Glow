package com.example.glow.notification;

public class Token {
    String userToken;

    public Token(String userToken) {
        this.userToken = userToken;
    }

    public Token() {
    }

    public String getToken() {
        return userToken;
    }

    public void setToken(String token) {
        this.userToken = token;
    }
}
