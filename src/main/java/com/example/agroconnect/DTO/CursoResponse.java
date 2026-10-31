package com.example.agroconnect.DTO;

public class CursoResponse {
    public CursoResponse(String mensagem,Long id) {
        this.mensagem = mensagem;
        this.id = id;
    }

    public CursoResponse() {
    }

    private String mensagem;

    private Long id;

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
}
