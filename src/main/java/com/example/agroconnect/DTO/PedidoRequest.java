package com.example.agroconnect.DTO;

public class PedidoRequest {

    public PedidoRequest() {
    }

    private Integer quantidadeComprada;
    private String nomeColheita;
    private String documentoComprador;


    public Integer getQuantidadeComprada() {
        return quantidadeComprada;
    }

    public void setQuantidadeComprada(Integer quantidadeComprada) {
        this.quantidadeComprada = quantidadeComprada;
    }

    public String getNomeColheita() {
        return nomeColheita;
    }

    public void setNomeColheita(String nomeColheita) {
        this.nomeColheita = nomeColheita;
    }

    public String getDocumentoComprador() {
        return documentoComprador;
    }

    public void setDocumentoComprador(String documentoComprador) {
        this.documentoComprador = documentoComprador;
    }
}


