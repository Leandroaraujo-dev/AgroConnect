package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.AtualizarEnderecoCompradorRequest;
import com.example.agroconnect.DTO.CompradorRequest;
import com.example.agroconnect.DTO.CompradorResponse;
import com.example.agroconnect.entities.Comprador;
import com.example.agroconnect.repository.CompradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/Comprador")

public class CompradorController {


    @Autowired
    public CompradorRepository compradorRepository;

    @GetMapping
    public List<Comprador> consultaComprador() {
        return compradorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comprador> consultaCompradorId(@PathVariable long id) {
        var comprador = compradorRepository.findById(id).orElse(null);
        if (comprador == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(comprador);
    }

    @PostMapping
    public ResponseEntity<CompradorResponse> cadastraComprador(@RequestBody CompradorRequest request) {
        Comprador compradorBanco = new Comprador();

        compradorBanco.setNome(request.getNome());
        compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());
        compradorBanco.setCpfouCnpj(request.getCnpjOuCpf());

        compradorRepository.save(compradorBanco);

        return ResponseEntity.ok(new CompradorResponse("Comprador cadastrado com sucesso", compradorBanco.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompradorResponse> atualizarComprador(@PathVariable Long id, @RequestBody CompradorRequest request) {
        Comprador compradorBanco = new Comprador();

        if (compradorBanco != null) {
            compradorBanco.setNome(request.getNome());
            compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());
            compradorBanco.setCpfouCnpj(request.getCnpjOuCpf());

            compradorRepository.save(compradorBanco);
            return ResponseEntity.ok(new CompradorResponse("Comprador atualizado com sucesso", compradorBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/endereco")
    public ResponseEntity<CompradorResponse> atualizarEndereco(@PathVariable Long id, @RequestBody AtualizarEnderecoCompradorRequest request) {
        Comprador compradorBanco = new Comprador();

        if (compradorBanco != null) {
            compradorBanco.setEnderecoEntrega(request.getEnderecoEntrega());

            compradorRepository.save(compradorBanco);
            return ResponseEntity.ok(new CompradorResponse("Endereço atualizado com sucesso", compradorBanco.getId()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CompradorResponse> AtualizarStatus(@PathVariable Long id) {

        Comprador compradorBanco = compradorRepository.findById(id).orElse(null);

        if (compradorBanco != null) {
            compradorBanco.setStatus("D");
            compradorRepository.save(compradorBanco);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}