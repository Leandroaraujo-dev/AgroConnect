package com.example.agroconnect.controller;

import com.example.agroconnect.entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")

public class UsuarioController {

    @GetMapping
    public  String ConsutltaUsuario(){
        return "Helo Word";
    }
    @GetMapping("/{id}")

    public Usuario ConsultaUsuarioPorId(@PathVariable long id){
        Usuario usuario = new Usuario();

        usuario.setnome("Leandro");
        usuario.setCpf("999999999");
        usuario.setDataNascimento("04-11-91");

        return usuario;
    }


    @GetMapping("/empresa/{empresaId}")
    public Usuario ConsultaUsuarioPorEmpresaId(@PathVariable long empresaId){
        Usuario usuarioContrutorCompleto = new Usuario("Leandro","999999999","04-11-91");

        return usuarioContrutorCompleto;
    }



    @PostMapping
    public ResponseEntity<Usuario> CadastrarUsuario(@RequestBody Usuario usuarioRequest){
        return ResponseEntity.ok(usuarioRequest);
    }

}
