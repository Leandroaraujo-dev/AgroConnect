package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.AtualizaStatusUsuarioRequest;
import com.example.agroconnect.DTO.UsuarioRequest;
import com.example.agroconnect.DTO.UsuarioResponse;
import com.example.agroconnect.entities.Usuario;
import com.example.agroconnect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UsuarioController {


    @Autowired
    private UsuarioRepository usuarioRepository;



    @GetMapping
    public List<Usuario> ConsutltaUsuario(){

        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")

    public ResponseEntity<Usuario> ConsultaUsuarioPorId(@PathVariable long id) {
        var usuario = usuarioRepository.findById(id).orElse(null);

        if(usuario == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);
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
        usuarioBanco.setSenha(usuarioRequest.getSenha());

        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");

        //salvando banco
        usuarioRepository.save(usuarioBanco);

        return ResponseEntity.ok(new UsuarioResponse("Cadastro com sucesso",usuarioBanco.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest) {

        //consulta no banco
        Usuario usuarioBanco =usuarioRepository.findById(id).orElse(null);

        if (usuarioBanco != null) {
            usuarioBanco.setnome(usuarioRequest.getNome());
            usuarioBanco.setCpf(usuarioRequest.getCpf());
            usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
            usuarioBanco.setDataAtualizacao(LocalDateTime.now());
            usuarioBanco.setSenha(usuarioRequest.getSenha());
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse("Usuario atualizado com sucesso",usuarioBanco.getId()));


        }

        return ResponseEntity.notFound().build();
    }

    @PatchMapping ("/{id}/status")
    public ResponseEntity<UsuarioResponse> AtualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusUsuarioRequest usuarioRequest) {

        //consulta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);

        if (usuarioBanco != null) {
            usuarioBanco.setStatus(usuarioBanco.getStatus());
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse("Status atualizado com sucesso",usuarioBanco.getId()));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<UsuarioResponse> AtualizarStatus(@PathVariable Long id){

        //consulta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);

        if (usuarioBanco != null) {
            usuarioBanco.setStatus("D");
            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}
