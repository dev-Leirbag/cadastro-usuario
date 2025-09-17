package api.cad_funcionario.adapters.in.dto;

import api.cad_funcionario.application.domain.UsuarioRole;

public record UsuarioResquestDTO(String email, String senha, UsuarioRole role) {
}
