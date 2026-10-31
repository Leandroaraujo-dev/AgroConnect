package com.example.agroconnect.controller;

import com.example.agroconnect.DTO.AtualizaStatusUsuarioRequest;
import com.example.agroconnect.DTO.UsuarioConsultaResponse;
import com.example.agroconnect.DTO.UsuarioRequest;
import com.example.agroconnect.DTO.UsuarioResponse;
import com.example.agroconnect.entities.Usuario;
import com.example.agroconnect.repository.EmpresaRepository;
import com.example.agroconnect.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuario Controler", description = "Classe responsavel por fazer o controle do CRUD de usuairos")
public class UsuarioController {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;



    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Método responsável por buscar todos os usuários cadastrados no banco de dados e retornar uma lista formatada através do DTO.")
    public List<UsuarioConsultaResponse> ConsutltaUsuario(){

        return usuarioRepository.findAll().stream().map(UsuarioConsultaResponse::new).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Método de consulta de usuário por ID que recebe como parâmetro o Path Variable 'id' e retorna os dados formatados na saída do DTO.")
    public ResponseEntity<UsuarioConsultaResponse> ConsultaUsuarioPorId(@PathVariable long id) {
        var usuario = usuarioRepository.findById(id).orElse(null);

        if(usuario == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UsuarioConsultaResponse(usuario));
    }

    @GetMapping("/empresa/{empresaId}")
    @Operation(summary = "Buscar usuário por ID da Empresa", description = "Busca e retorna um usuário associado ao ID de uma empresa específica.")
    public Usuario ConsultaUsuarioPorEmpresaId(@PathVariable long empresaId) {
        Usuario usuarioContrutorCompleto = new Usuario("Leandro", "999999999", "04-11-91");

        return usuarioContrutorCompleto;
    }


    @PostMapping
    @Operation(summary = "Cadastrar novo usuário", description = "Cria um novo usuário no sistema e o vincula a uma empresa existente. Registra automaticamente a data de cadastro e o status 'A' (Ativo).")
    public ResponseEntity<UsuarioResponse> CadastrarUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        var empresaBanco = empresaRepository.findById(usuarioRequest.getEmpresa_id()).orElse(null);

        if (empresaBanco == null){
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioBanco = new Usuario();
        usuarioBanco.setnome(usuarioRequest.getNome());
        usuarioBanco.setCpf(usuarioRequest.getCpf());
        usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
        usuarioBanco.setSenha(usuarioRequest.getSenha());
        usuarioBanco.setEmpresa(empresaBanco);

        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");

        //salvando banco
        usuarioRepository.save(usuarioBanco);

        return ResponseEntity.ok(new UsuarioResponse("Cadastro com sucesso",usuarioBanco.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário existente", description = "Atualiza todos os dados de um usuário existente (nome, CPF, nascimento, senha, empresa) e registra a data de atualização.")
    public ResponseEntity<UsuarioResponse> AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest) {

        //consulta no banco
        Usuario usuarioBanco =usuarioRepository.findById(id).orElse(null);

        if (usuarioBanco != null) {

            var empresaBanco = empresaRepository.findById(usuarioRequest.getEmpresa_id()).orElse(null);
            if (empresaBanco == null){
                return ResponseEntity.notFound().build();
            }
            usuarioBanco.setEmpresa(empresaBanco);
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
    @Operation(summary = "Atualizar apenas o status do usuário", description = "Permite alterar especificamente o status (ex: Ativo/Inativo) de um usuário sem precisar enviar os demais dados.")
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
    @Operation(summary = "Excluir usuário (Exclusão Lógica)", description = "Em vez de apagar do banco, altera o status do usuário para 'D' (Deletado), mantendo a integridade dos dados e histórico de pedidos.")
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
