package com.example.agroconnect.DTO;

public class PedidoResponse {

    public PedidoResponse() {
    }

    private Long id;
    private String mensagem;

    public PedidoResponse(String mensagem, Long id) {
        this.mensagem = mensagem;
        this.id = id;


    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
