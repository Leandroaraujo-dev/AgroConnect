package com.example.agroconnect.DTO;

public class ColheitaRequest {

    public ColheitaRequest(){}

    private String nomeProduto;
    private Integer quantidadeEstoque;
    private Double valorProduto;
    private String cpfProdutor;
    public String getNomeProduto() {
        return nomeProduto;
    }




    public String getCpfProdutor() {
        return cpfProdutor;
    }

    public void setCpfProdutor(String cpfProdutor) {
        this.cpfProdutor = cpfProdutor;
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
