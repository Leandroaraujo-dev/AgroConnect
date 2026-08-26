package com.example.agroconnect.entities;

public class Colheita {


private Long id;

private String nomedeProduto;

private Integer quantidadeEstoque;

private Double ValorProduto;

private String produtorRural;

public Long getId() {
    return this.id;
}

public String getNomedeProduto(){
        return this.nomedeProduto;
}

public void setNomedeProduto(String nomedeProduto){
    this.nomedeProduto = nomedeProduto;
}
public Integer getQuantidadeEstoque(){
    return this.quantidadeEstoque;
}

public void setQuantidadeEstoque(Integer quantidadeEstoque){
    this.quantidadeEstoque = quantidadeEstoque;
}

public String getProdutorRural(){
    return this.produtorRural;
}

public void  setProdutorRural(String produtorRural){
    this.produtorRural = produtorRural;
}

public Double getValorProduto() {
        return this.ValorProduto;
}

public void setValorProduto(Double valorProduto) {
       this.ValorProduto = valorProduto;
    }
}

