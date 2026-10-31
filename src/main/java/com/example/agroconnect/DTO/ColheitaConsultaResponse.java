package com.example.agroconnect.DTO;

import com.example.agroconnect.entities.Colheita;

public class ColheitaConsultaResponse {


    public ColheitaConsultaResponse(){}

    public ColheitaConsultaResponse(Colheita colheita){
        this.nomeProduto = colheita.getNomedeProduto();
        this.quantidadeEstoque = colheita.getQuantidadeEstoque();
        this.valorProduto = colheita.getValorProduto();
        this.id = colheita.getId();
        this.status = colheita.getStatus();
        if (colheita.getUsuarioColheita() != null) {
            this.cpfProdutor = colheita.getUsuarioColheita().getCpf();
        }

    }

    private String nomeProduto;
    private Integer quantidadeEstoque;
    private Double valorProduto;
    private String cpfProdutor;
    private Long id;
    private String status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getCpfProdutor() {
        return cpfProdutor;
    }

    public void setCpfProdutor(String cpfProdutor) {
        this.cpfProdutor = cpfProdutor;
    }

    public Double getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(Double valorProduto) {
        this.valorProduto = valorProduto;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
