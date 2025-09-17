package api.cad_funcionario.adapters.in.dto;

import api.cad_funcionario.application.domain.UsuarioRole;
import java.util.UUID;

public record UsuarioResponseDTO(UUID id, String email, String senha, UsuarioRole role) {
}
