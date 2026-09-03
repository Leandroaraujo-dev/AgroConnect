package com.example.agroconnect.DTO;

public class UsuarioResponse {

    public UsuarioResponse(){}

    private Long id;

    private String mensagem;

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioResponse(String mensagem, Long id) {
        this.id = id;
        this.mensagem = mensagem;

    }
}
