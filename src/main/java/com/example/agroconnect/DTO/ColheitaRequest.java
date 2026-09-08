package com.example.agroconnect.DTO;

public class ColheitaRequest {

    public ColheitaRequest(){}

    private String nomeProduto;
    private Integer quantidadeEstoque;
    private Double valorProduto;
    private String produtorRural;

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getProdutorRural() {
        return produtorRural;
    }

    public void setProdutorRural(String produtorRural) {
        this.produtorRural = produtorRural;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Double getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(Double valorProduto) {
        this.valorProduto = valorProduto;
    }
}
