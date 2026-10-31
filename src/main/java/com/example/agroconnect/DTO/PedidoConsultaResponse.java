package com.example.agroconnect.DTO;

import com.example.agroconnect.entities.Pedido;

public class PedidoConsultaResponse {

    public PedidoConsultaResponse() {}
    public PedidoConsultaResponse(Pedido pedido) {
        this.id = pedido.getId();
        this.quantidadeComprada = pedido.getQuantidadeComprada();
        if (pedido.getColheitaComprada() != null){
            this.nomeColheita = pedido.getColheitaComprada().getNomedeProduto();
        }
        if (pedido.getComprador() != null){
            this.documentoComprador = pedido.getComprador().getCpfouCnpj();
        }
        this.statusPagamento = pedido.getStatusPagamento();
        this.status = pedido.getStatus();

    }

    private Long id;
    private Integer quantidadeComprada;
    private String nomeColheita;
    private String documentoComprador;
    private String statusPagamento;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidadeComprada() {
        return quantidadeComprada;
    }

    public void setQuantidadeComprada(Integer quantidadeComprada) {
        this.quantidadeComprada = quantidadeComprada;
    }

    public String getDocumentoComprador() {
        return documentoComprador;
    }

    public void setDocumentoComprador(String documentoComprador) {
        this.documentoComprador = documentoComprador;
    }

    public String getNomeColheita() {
        return nomeColheita;
    }

    public void setNomeColheita(String nomeColheita) {
        this.nomeColheita = nomeColheita;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
