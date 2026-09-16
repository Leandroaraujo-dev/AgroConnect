package com.example.agroconnect.DTO;

public class CompradorResponse {

    public CompradorResponse(){}

    private long id;
    private String mensagem;

    public CompradorResponse(String mensagem,Long id){
        this.mensagem = mensagem;
        this.id = id;
    }

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
