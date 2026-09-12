package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.AtualizarEnderecoCompradorRequest;
import com.example.agroconnect.DTO.CompradorRequest;
import com.example.agroconnect.DTO.CompradorResponse;
import com.example.agroconnect.entities.Comprador;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/Comprador")

public class CompradorController {
    @GetMapping
    public String ConsultaComprado() {
        return "comprador";
    }

    @GetMapping("/{id}")
    public Comprador consultaCompradorId(@PathVariable long id) {
        Comprador comprador = new Comprador();

        comprador.setId(id);
        comprador.setNome("");
        comprador.setCpfouCnpj("");
        comprador.setEnderecoEntrega("");

        return comprador;

    }

    @PostMapping
    public ResponseEntity<CompradorResponse> cadastraComprador(@RequestBody CompradorRequest request) {
        Comprador compradorBanco = new Comprador();

        compradorBanco.setNome(request.getNome());
        compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());
        compradorBanco.setCpfouCnpj(request.getCnpjOuCpf());

        return ResponseEntity.ok(new CompradorResponse("Comprador cadastrado com sucesso", compradorBanco.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompradorResponse> atualizarComprador(@PathVariable Long id, @RequestBody CompradorRequest request) {
        Comprador compradorBanco = new Comprador();

        if (compradorBanco != null) {
            compradorBanco.setNome(request.getNome());
            compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());
            compradorBanco.setCpfouCnpj(request.getCnpjOuCpf());

            return ResponseEntity.ok(new CompradorResponse("Comprador atualizado com sucesso", compradorBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/endereco")
    public ResponseEntity<CompradorResponse> atualizarEndereco(@PathVariable Long id, @RequestBody AtualizarEnderecoCompradorRequest request) {
        Comprador compradorBanco = new Comprador();

        if (compradorBanco != null) {
            compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());

            return ResponseEntity.ok(new CompradorResponse("Endereço atualizado com sucesso", compradorBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CompradorResponse> deletarComprador(@PathVariable Long id) {
        Comprador compradorBanco = new Comprador();
        if (compradorBanco != null) {

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();

    }
}