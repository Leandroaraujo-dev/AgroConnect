package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.AtualizaStatusUsuarioRequest;
import com.example.agroconnect.DTO.UsuarioRequest;
import com.example.agroconnect.DTO.UsuarioResponse;
import com.example.agroconnect.entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/usuario")

public class UsuarioController {

    @GetMapping
    public String ConsutltaUsuario() {
        return "Helo Word";
    }

    @GetMapping("/{id}")

    public Usuario ConsultaUsuarioPorId(@PathVariable long id) {
        Usuario usuario = new Usuario();

        usuario.setnome("Leandro");
        usuario.setCpf("999999999");
        usuario.setDataNascimento("04-11-91");

        return usuario;
    }

    @GetMapping("/empresa/{empresaId}")
    public Usuario ConsultaUsuarioPorEmpresaId(@PathVariable long empresaId) {
        Usuario usuarioContrutorCompleto = new Usuario("Leandro", "999999999", "04-11-91");

        return usuarioContrutorCompleto;
    }


    @PostMapping
    public ResponseEntity<UsuarioResponse> CadastrarUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        Usuario usuarioBanco = new Usuario();
        usuarioBanco.setnome(usuarioRequest.getNome());
        usuarioBanco.setCpf(usuarioRequest.getCpf());
        usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");


        return ResponseEntity.ok(new UsuarioResponse("Cadastro com sucesso",usuarioBanco.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest) {

        //consulta no banco
        Usuario usuarioBanco = new Usuario();

        if (usuarioBanco != null) {
            usuarioBanco.setnome(usuarioRequest.getNome());
            usuarioBanco.setCpf(usuarioRequest.getCpf());
            usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
            usuarioBanco.setDataAtualizacao(LocalDateTime.now());

            return ResponseEntity.ok(new UsuarioResponse("Usuario atualizado com sucesso",usuarioBanco.getId()));


        }

        return ResponseEntity.notFound().build();
    }

    @PatchMapping ("/{id}/status")
    public ResponseEntity<UsuarioResponse> AtualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusUsuarioRequest usuarioRequest) {

        //consulta no banco
        Usuario usuarioBanco = new Usuario();

        if (usuarioBanco != null) {
            usuarioBanco.setStatus(usuarioBanco.getStatus());

            return ResponseEntity.ok(new UsuarioResponse("Status atualizado com sucesso",usuarioBanco.getId()));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<UsuarioResponse> AtualizarStatus(@PathVariable Long id){

        //consulta no banco
        Usuario usuarioBanco = new Usuario();

        if (usuarioBanco != null) {
            usuarioBanco.setStatus("D");

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}
