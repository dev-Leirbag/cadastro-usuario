package api.cad_funcionario.adapters.in.controller;

import api.cad_funcionario.adapters.in.dto.LoginRequestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResponseDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResquestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioUpdateDto;
import api.cad_funcionario.adapters.in.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuario")
@RequiredArgsConstructor
public class UsuarioControler {

    private final UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResquestDTO> cadastroUsuario (@RequestBody UsuarioResquestDTO data) {
        return ResponseEntity.ok(usuarioService.cadastroUsuario(data));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.buscaUsuarios());
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUsuario(@RequestBody LoginRequestDTO data){
        return ResponseEntity.ok(usuarioService.loginUsuario(data));
    }

    @GetMapping("/buscar/{email}")
    public ResponseEntity<UsuarioResponseDTO> buscaUsuarioPorEmail(@PathVariable("email") String email){
        return ResponseEntity.ok(usuarioService.buscaUsuarioPorEmail(email));
    }

    @DeleteMapping("/deletar/{email}")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable("email") String email){
        usuarioService.deletaUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/atualizar/{email}")
    public ResponseEntity<UsuarioUpdateDto> atualzaUsuarioPorEmail(@RequestBody UsuarioUpdateDto data,
                                                                   @PathVariable("email") String email){
        return ResponseEntity.ok(usuarioService.atualzaUsuarioPorEmail(data, email));
    }
}
