package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.AtualizaStatusPedidoRequest;
import com.example.agroconnect.DTO.PedidoRequest;
import com.example.agroconnect.DTO.PedidoResponse;
import com.example.agroconnect.entities.Colheita;
import com.example.agroconnect.entities.Comprador;
import com.example.agroconnect.entities.Pedido;
import com.example.agroconnect.repository.ColheitaRepository;
import com.example.agroconnect.repository.CompradorRepository;
import com.example.agroconnect.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/Pedido")

    public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ColheitaRepository colheitaRepository;

    @Autowired
    private CompradorRepository compradorRepository;


    @GetMapping
    public List<Pedido> consultaTodosPedidos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> consultaPedidoPorID(@PathVariable long id) {
        var pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pedido);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> cadastrarPedido(@RequestBody PedidoRequest request) {
        Pedido pedidoBanco = new Pedido();

        pedidoBanco.setQuantidadeComprada(request.getQuantidadeComprada());
        pedidoBanco.setStatusPagamento("Pendente");
        pedidoBanco.setStatus("A");
        Colheita colheita = colheitaRepository.findByNomedeProduto(request.getNomeColheita()).orElse(null);
        Comprador comprador = compradorRepository.findByCpfouCnpj(request.getDocumentoComprador()).orElse(null);

        if (colheita == null || comprador == null) {
            return ResponseEntity.badRequest().build();
        }

        pedidoBanco.setColheitaComprada(colheita);
        pedidoBanco.setComprador(comprador);

        Pedido pedidoSalvo = pedidoRepository.save(pedidoBanco);
        return ResponseEntity.ok(new PedidoResponse("Pedido criado com sucesso", pedidoSalvo.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> atualizarPedidoCompleto(@PathVariable Long id, @RequestBody PedidoRequest request) {
        Pedido pedidoBanco = pedidoRepository.findById(id).orElse(null);

        if (pedidoBanco != null) {
            pedidoBanco.setQuantidadeComprada(request.getQuantidadeComprada());
            pedidoRepository.save(pedidoBanco);
            return ResponseEntity.ok(new PedidoResponse("Pedido atualizado com sucesso", pedidoBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusPedidoRequest request) {
        Pedido pedidoBanco = pedidoRepository.findById(id).orElse(null);

        if (pedidoBanco != null) {
            pedidoBanco.setStatusPagamento(request.getStatusPagamento());

            if ("Pagamento Confirmado".equalsIgnoreCase(request.getStatusPagamento())) {
                Colheita colheita = pedidoBanco.getColheitaComprada();

                if (colheita != null) {
                    int estoqueAtualizado = colheita.getQuantidadeEstoque() - pedidoBanco.getQuantidadeComprada();
                    colheita.setQuantidadeEstoque(estoqueAtualizado);
                    colheitaRepository.save(colheita);
                    pedidoRepository.save(pedidoBanco);

                    return ResponseEntity.ok(new PedidoResponse("Pagamento confirmado! O novo estoque da colheita agora é: " + estoqueAtualizado, pedidoBanco.getId()));
                }
            }
            pedidoRepository.save(pedidoBanco);
            return ResponseEntity.ok(new PedidoResponse("Status do pedido atualizado para: " + request.getStatusPagamento(), pedidoBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
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
