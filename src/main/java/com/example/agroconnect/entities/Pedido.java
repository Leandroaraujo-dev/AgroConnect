package com.example.agroconnect.entities;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class Pedido {

    private Long id;
    private Colheita colheitaComprada;
    private Comprador comprador;
    private Integer quantidadeComprada;
    private String statusPagamento;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Colheita getColheitaComprada() {
        return colheitaComprada;
    }

    public void setColheitaComprada(Colheita colheitaComprada) {
        this.colheitaComprada = colheitaComprada;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public Integer getQuantidadeComprada() {
        return quantidadeComprada;
    }

    public void setQuantidadeComprada(Integer quantidadeComprada) {
        this.quantidadeComprada = quantidadeComprada;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }
}
