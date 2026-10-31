package com.example.agroconnect.DTO;

public class EmpresaResponse {
    public EmpresaResponse() {
    }

    private long id;
    private  String mensagem;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
