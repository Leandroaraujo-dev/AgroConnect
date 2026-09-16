package com.example.agroconnect.entities;


import jakarta.persistence.*;

@Entity
public class Pedido {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "colheita_id")
    private Colheita colheitaComprada;

    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private Comprador comprador;
    private Integer quantidadeComprada;
    private String statusPagamento;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
