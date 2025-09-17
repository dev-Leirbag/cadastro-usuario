package api.cad_funcionario.adapters.out.repository;

import api.cad_funcionario.adapters.in.mapper.Converter;
import api.cad_funcionario.adapters.out.entities.UsuarioEntity;
import api.cad_funcionario.application.domain.UsuarioDomain;
import api.cad_funcionario.porters.out.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final Converter converter;

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public UsuarioDomain salvaUsuario(UsuarioDomain usuario) {
        UsuarioEntity usuarioEntity = converter.domainParaEntity(usuario);

        UsuarioEntity usuarioSalvo = jpaRepository.save(usuarioEntity);

        return converter.entityParaDomain(usuarioSalvo);
    }

    @Override
    public Optional<UsuarioDomain> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(converter::entityParaDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<UsuarioDomain> findAll() {
        List<UsuarioEntity> entityList;

        entityList = jpaRepository.findAll();

        List<UsuarioDomain> domainList = converter.entitiesParaDomain(entityList);

        return domainList;
    }

    @Override
    public void deleteByEmail(String email) {
        jpaRepository.deleteByEmail(email);
    }

    @Override
    public UsuarioDomain atualzaUsuarioPorEmail(UsuarioDomain usuario) {
        UsuarioEntity usuarioEntity = converter.domainParaEntity(usuario);

        UsuarioEntity usuarioAtualizado = jpaRepository.save(usuarioEntity);

        return converter.entityParaDomain(usuarioAtualizado);
    }
}
