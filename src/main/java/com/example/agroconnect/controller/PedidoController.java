package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.*;
import com.example.agroconnect.entities.Colheita;
import com.example.agroconnect.entities.Comprador;
import com.example.agroconnect.entities.Pedido;
import com.example.agroconnect.repository.ColheitaRepository;
import com.example.agroconnect.repository.CompradorRepository;
import com.example.agroconnect.repository.PedidoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedido")
@Tag(name = "Pedido Controller", description = "Classe responsável pelo controle de CRUD de pedidos e pela orquestração da regra de baixa automática de estoque.")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ColheitaRepository colheitaRepository;

    @Autowired
    private CompradorRepository compradorRepository;


    @GetMapping
    @Operation(summary = "Listar todos os pedidos", description = "Busca no banco de dados e retorna todos os registros de pedidos cadastrados no sistema, formatados através do DTO.")
    public List<PedidoConsultaResponse> consultaTodosPedidos() {
        return pedidoRepository.findAll().stream().map(PedidoConsultaResponse::new).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Pesquisa um pedido específico usando o seu ID único. Retorna erro 404 (Not Found) se não for encontrado.")
    public ResponseEntity<PedidoConsultaResponse> consultaPedidoPorID(@PathVariable long id) {
        var pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PedidoConsultaResponse(pedido));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo pedido", description = "Cria um novo registro de pedido. Vincula automaticamente o pedido à respectiva Colheita e ao Comprador baseando-se no nome do produto e no documento (CPF/CNPJ). O status de pagamento inicial é definido como 'Pendente'.")
    public ResponseEntity<?> cadastrarPedido(@RequestBody PedidoRequest request) {
        if (request.getQuantidadeComprada() == null || request.getQuantidadeComprada() < 1) {
            return ResponseEntity.badRequest().body("A quantidade deve ser maior que zero.");
        }

        Pedido pedidoBanco = new Pedido();

        pedidoBanco.setQuantidadeComprada(request.getQuantidadeComprada());
        pedidoBanco.setStatusPagamento("Pendente");
        pedidoBanco.setStatus("A");
        Colheita colheita = colheitaRepository.findByNomedeProduto(request.getNomeColheita()).orElse(null);
        Comprador comprador = compradorRepository.findByCpfouCnpj(request.getDocumentoComprador()).orElse(null);

        if (colheita == null || comprador == null) {
            return ResponseEntity.badRequest().build();
        }
        if (colheita.getQuantidadeEstoque() == null ||
                request.getQuantidadeComprada() > colheita.getQuantidadeEstoque()) {
            int estoqueDisponivel = colheita.getQuantidadeEstoque() == null
                    ? 0
                    : colheita.getQuantidadeEstoque();
            return ResponseEntity.badRequest().body(
                    "Estoque insuficiente. Disponível: " + estoqueDisponivel + " unidade(s)."
            );
        }

        Double total = request.getQuantidadeComprada() * colheita.getValorProduto();
        pedidoBanco.setValorTotal(total);

        pedidoBanco.setColheitaComprada(colheita);
        pedidoBanco.setComprador(comprador);

        Pedido pedidoSalvo = pedidoRepository.save(pedidoBanco);
        return ResponseEntity.ok(new PedidoResponse("Pedido criado com sucesso", pedidoSalvo.getId(), pedidoSalvo.getValorTotal()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar quantidade do pedido", description = "Atualiza a quantidade comprada de um pedido existente com base no ID fornecido.")
    public ResponseEntity<PedidoResponse> atualizarPedidoCompleto(@PathVariable Long id, @RequestBody PedidoRequest request) {
        Pedido pedidoBanco = pedidoRepository.findById(id).orElse(null);

        if (pedidoBanco != null) {
            pedidoBanco.setQuantidadeComprada(request.getQuantidadeComprada());
            pedidoRepository.save(pedidoBanco);
            return ResponseEntity.ok(new PedidoResponse("Pedido atualizado com sucesso", pedidoBanco.getId(), pedidoBanco.getValorTotal()));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status de pagamento (Gatilho da Regra de Negócio)", description = "Altera o status de pagamento do pedido. REGRA DE NEGÓCIO: Se o status for alterado para 'Pagamento Confirmado', o sistema deduz automaticamente a quantidade comprada do estoque da respectiva colheita, bloqueando a ação caso o estoque seja insuficiente para evitar valores negativos.")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusPedidoRequest request) {
        Pedido pedidoBanco = pedidoRepository.findById(id).orElse(null);

        if (pedidoBanco != null) {
            String statusAntigo = pedidoBanco.getStatusPagamento();
            String novoStatus = request.getStatusPagamento();

            // 1. Verifica se mudou PARA "Pagamento Confirmado" e se JÁ NÃO ERA "Pagamento Confirmado"
            if ("Pagamento Confirmado".equalsIgnoreCase(novoStatus) &&
                    !"Pagamento Confirmado".equalsIgnoreCase(statusAntigo)) {

                Colheita colheita = pedidoBanco.getColheitaComprada();

                if (colheita != null) {
                    int estoqueAtualizado = colheita.getQuantidadeEstoque() - pedidoBanco.getQuantidadeComprada();

                    // 2. Trava de segurança para impedir estoque negativo
                    if (estoqueAtualizado < 0) {
                        return ResponseEntity.badRequest().body(new PedidoResponse("Erro: Estoque insuficiente para confirmar este pedido. Estoque atual: " + colheita.getQuantidadeEstoque(),id,pedidoBanco.getValorTotal()));
                    }

                    colheita.setQuantidadeEstoque(estoqueAtualizado);
                    colheitaRepository.save(colheita);
                }
            }

            // Atualiza o status do pedido e salva
            pedidoBanco.setStatusPagamento(novoStatus);
            pedidoRepository.save(pedidoBanco);

            return ResponseEntity.ok(new PedidoResponse("Status do pedido atualizado para: " + novoStatus, pedidoBanco.getId(),pedidoBanco.getValorTotal()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido (Exclusão Lógica)", description = "Altera o status do pedido para 'D' (Deletado/Inativo) em vez de apagá-lo fisicamente do banco de dados, mantendo o histórico de transações.")
    public ResponseEntity<PedidoResponse> deletarPedido(@PathVariable Long id) {

        Pedido pedidoBanco = pedidoRepository.findById(id).orElse(null);

        if (pedidoBanco != null) {
            pedidoBanco.setStatus("D");
            pedidoRepository.save(pedidoBanco);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}