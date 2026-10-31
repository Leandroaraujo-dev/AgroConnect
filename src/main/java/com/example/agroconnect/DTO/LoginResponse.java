package com.example.agroconnect.DTO;

public class LoginResponse {

    public LoginResponse() {
    }

    private String mensagem;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
