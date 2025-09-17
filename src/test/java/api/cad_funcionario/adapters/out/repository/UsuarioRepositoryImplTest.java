package api.cad_funcionario.adapters.out.repository;

import api.cad_funcionario.adapters.out.entities.UsuarioEntity;
import api.cad_funcionario.application.domain.UsuarioRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioJpaRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    UsuarioJpaRepository repository;

    @Test
    @DisplayName("Deve receber um usuario e buscar por email")
    void findByEmailCase1() throws Exception {
        String email = "teste@teste.com";
        UsuarioEntity newUser = new UsuarioEntity(null,email,"1234", UsuarioRole.ADMIN);
        entityManager.persist(newUser);

        Optional<UsuarioEntity> founderUser = repository.findByEmail(email);

        assertThat(founderUser.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve falhar ao procurar o usuario por email")
    void findByEmailCase2() throws Exception{
        String email = "teste@teste.com";

        Optional<UsuarioEntity> founderUser = repository.findByEmail(email);

        assertThat(founderUser.isEmpty()).isTrue();
    }
}