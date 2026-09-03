package com.example.agroconnect.entities;

public class Colheita {

private Long id;

private String nomedeProduto;

private Integer quantidadeEstoque;

private Double valorProduto;

private Usuario usuario;

public Long getId() {
    return this.id;
}

public void setId(Long id) {
        this.id = id;
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

public Usuario getUsuario(){
    return this.usuario;
}

public void  setUsuario(Usuario usuario){
    this.usuario = usuario;
}

public Double getValorProduto() {
        return this.valorProduto;
}

public void setValorProduto(Double valorProduto) {
       this.valorProduto = valorProduto;
    }
}

