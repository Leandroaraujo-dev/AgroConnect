package com.example.agroconnect.DTO;

public class AtualizaStatusUsuarioRequest {
    private AtualizaStatusUsuarioRequest(){}
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
