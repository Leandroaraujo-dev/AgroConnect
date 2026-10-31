package com.example.agroconnect.DTO;

import com.example.agroconnect.entities.Pedido;

public class PedidoResponse {

    public PedidoResponse() {
    }

    private Long id;
    private String mensagem;

    public PedidoResponse(String mensagem, Long id,Double valorTotal) {
        this.mensagem = mensagem;
        this.id = id;
        this.valorTotal = valorTotal;


    }
    private Double valorTotal;

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
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
