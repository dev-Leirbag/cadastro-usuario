package api.cad_funcionario.adapters.in.dto;

import api.cad_funcionario.application.domain.UsuarioRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDto {
    private String email;
    private String senha;
    private UsuarioRole role;

}
