package api.cad_funcionario.porters.out;

import api.cad_funcionario.application.domain.UsuarioDomain;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    UsuarioDomain salvaUsuario (UsuarioDomain usuario);

    Optional<UsuarioDomain> findByEmail (String email);

    boolean existsByEmail (String email);

    List<UsuarioDomain> findAll();

    @Transactional
    void deleteByEmail(String email);

    UsuarioDomain atualzaUsuarioPorEmail(UsuarioDomain usuario);
}
