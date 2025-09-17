package api.cad_funcionario.adapters.in.service;

import api.cad_funcionario.adapters.in.dto.LoginRequestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResponseDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResquestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioUpdateDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface UsuarioService {

    UsuarioResquestDTO cadastroUsuario(UsuarioResquestDTO usuario);

    List<UsuarioResponseDTO> buscaUsuarios();

    String loginUsuario(LoginRequestDTO usuario);

    UsuarioResponseDTO buscaUsuarioPorEmail(String email);

    @Transactional
    void deletaUsuarioPorEmail(String email);

    UsuarioUpdateDto atualzaUsuarioPorEmail(UsuarioUpdateDto usuario, String email);
}
