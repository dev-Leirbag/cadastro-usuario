package api.cad_funcionario.adapters.out.entities;

import api.cad_funcionario.application.domain.UsuarioRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name = "tb_usuario")
@Entity(name = "tb_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String senha;

    private UsuarioRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == role.ADMIN){
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_FINANCEIRO"),
                    new SimpleGrantedAuthority("ROLE_GERENTE"),
                    new SimpleGrantedAuthority("ROLE_VENDEDOR"));
        }else if(this.role == role.FINANCEIRO){
            return List.of(new SimpleGrantedAuthority("ROLE_FINANCEIRO"));
        }else if(this.role == role.GERENTE){
            return List.of(new SimpleGrantedAuthority("ROLE_GERENTE"),
                    new SimpleGrantedAuthority("ROLE_VENDEDOR"),
                    new SimpleGrantedAuthority("ROLE_FINANCEIRO"));
        }else{
            return List.of(new SimpleGrantedAuthority("ROLE_VENDEDOR"));
        }
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
