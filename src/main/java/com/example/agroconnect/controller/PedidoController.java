package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.AtualizaStatusPedidoRequest;
import com.example.agroconnect.DTO.PedidoRequest;
import com.example.agroconnect.DTO.PedidoResponse;
import com.example.agroconnect.entities.Colheita;
import com.example.agroconnect.entities.Pedido;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequestMapping("/Pedido")

    public class PedidoController {
    @GetMapping
    public String ConsultaPedidoId(@PathVariable long id) {
        return "pedido" + id;
    }

    @GetMapping("/{id}")
    public Pedido consultaPedidoPorID(@PathVariable long id) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setQuantidadeComprada(100);
        pedido.setStatusPagamento("Pendete");

        Colheita colheitaSimulada = new Colheita();
        colheitaSimulada.setNomedeProduto("Milho");
        colheitaSimulada.setQuantidadeEstoque(500);

        pedido.setColheitaComprada(colheitaSimulada);

        return pedido;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> cadastrarPedido(@RequestBody PedidoRequest request) {
        Pedido pedidoBanco = new Pedido();

        pedidoBanco.setQuantidadeComprada(request.getQuantidadeComprada());
        pedidoBanco.setStatusPagamento("Pendente");


        return ResponseEntity.ok(new PedidoResponse("Pedido criado com sucesso", pedidoBanco.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> atualizarPedidoCompleto(@PathVariable Long id, @RequestBody PedidoRequest request) {
        Pedido pedidoBanco = new Pedido();
        pedidoBanco.setId(id);

        if (pedidoBanco != null) {
            pedidoBanco.setQuantidadeComprada(request.getQuantidadeComprada());
            return ResponseEntity.ok(new PedidoResponse("Pedido atualizado com sucesso", pedidoBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusPedidoRequest request) {
        Pedido pedidoBanco = new Pedido();
        pedidoBanco.setId(id);
        pedidoBanco.setQuantidadeComprada(100);

        Colheita colheitaBanco = new Colheita();
        colheitaBanco.setId(1L);
        colheitaBanco.setQuantidadeEstoque(500);

        pedidoBanco.setColheitaComprada(colheitaBanco);

        if (pedidoBanco != null) {
            pedidoBanco.setStatusPagamento(request.getStatusPagamento());

            if ("Pagamento Confirmado".equalsIgnoreCase(request.getStatusPagamento())) {
                Colheita colheita = pedidoBanco.getColheitaComprada();
                int estoqueAtualizado = colheita.getQuantidadeEstoque() - pedidoBanco.getQuantidadeComprada();
                colheita.setQuantidadeEstoque(estoqueAtualizado);

                return ResponseEntity.ok(new PedidoResponse("Pagamento confirmado! O novo estoque da colheita agora é: " + estoqueAtualizado, pedidoBanco.getId()));
            }

            return ResponseEntity.ok(new PedidoResponse("Status do pedido atualizado para: " + request.getStatusPagamento(), pedidoBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<PedidoResponse> deletarPedido(@PathVariable Long id) {
        Pedido pedidoBanco = new Pedido();
        pedidoBanco.setId(id);

        if (pedidoBanco != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}