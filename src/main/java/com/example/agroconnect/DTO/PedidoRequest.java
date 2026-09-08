package com.example.agroconnect.DTO;

public class PedidoRequest {

    public PedidoRequest() {}

    private Integer quantidadeComprada;
    private Long colheitaId;
    private Long compradorId;

    public Long getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(Long compradorId) {
        this.compradorId = compradorId;
    }

    public Integer getQuantidadeComprada() {
        return quantidadeComprada;
    }

    public void setQuantidadeComprada(Integer quantidadeComprada) {
        this.quantidadeComprada = quantidadeComprada;
    }

    public Long getColheitaId() {
        return colheitaId;
    }

    public void setColheitaId(Long colheitaId) {
        this.colheitaId = colheitaId;
    }
}
