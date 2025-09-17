package api.cad_funcionario.application.service;

import api.cad_funcionario.adapters.in.dto.LoginRequestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResponseDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResquestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioUpdateDto;
import api.cad_funcionario.adapters.in.mapper.Converter;
import api.cad_funcionario.adapters.in.mapper.UpdateConverter;
import api.cad_funcionario.adapters.in.service.UsuarioService;
import api.cad_funcionario.adapters.out.entities.UsuarioEntity;
import api.cad_funcionario.application.domain.UsuarioDomain;
import api.cad_funcionario.application.domain.UsuarioRole;
import api.cad_funcionario.application.infra.config.TokenService;
import api.cad_funcionario.application.infra.exceptions.cadastro.EmailException;
import api.cad_funcionario.application.infra.exceptions.cadastro.EmailExistsException;
import api.cad_funcionario.application.infra.exceptions.cadastro.SenhaException;
import api.cad_funcionario.application.infra.exceptions.login.EmailNotFoundExeption;
import api.cad_funcionario.application.infra.exceptions.login.SenhaIncorretException;
import api.cad_funcionario.application.infra.exceptions.update.UsuarioUpdateException;
import api.cad_funcionario.porters.out.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final Converter converter;
    private final UpdateConverter updateConverter;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository repository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;


    @Override
    public UsuarioResquestDTO cadastroUsuario(UsuarioResquestDTO usuario) {
        UsuarioDomain novoUsuario = converter.dtoRequestParaDomain(usuario);

        verificaCamposCadastro(novoUsuario);

        novoUsuario.setSenha(passwordEncoder.encode(novoUsuario.getSenha()));

        UsuarioDomain usuarioSalvo = repository.salvaUsuario(novoUsuario);

        return converter.domainParaDtoRequest(usuarioSalvo);
    }

    @Override
    public List<UsuarioResponseDTO> buscaUsuarios() {
            List<UsuarioDomain> domainList;

            domainList = repository.findAll();

            List<UsuarioResponseDTO> responseDTOList = converter.domainsParaDtoResponse(domainList);

            return responseDTOList;
    }

    @Override
    public String loginUsuario(LoginRequestDTO usuario) {
        try {
            UsuarioDomain usuarioDomain = converter.loginDtoParaDomain(usuario);

            verificaCampoLogin(usuarioDomain);

            var usuarioSenha = new UsernamePasswordAuthenticationToken(usuarioDomain.getEmail(), usuarioDomain.getSenha());

            var auth = authenticationManager.authenticate(usuarioSenha);

            String token = tokenService.generateToken((UsuarioEntity) auth.getPrincipal());

            return token;
        }catch (BadCredentialsException e){
            throw new SenhaIncorretException("A senha esta incorreta. Tente Novamente!!");
        }
    }

    @Override
    public UsuarioResponseDTO buscaUsuarioPorEmail(String email) {
            UsuarioDomain usuario = repository.findByEmail(email).orElseThrow(
                    () -> new EmailNotFoundExeption("Não existe um usuário com esse email: " + email));

            return converter.domainParaDtoResponse(usuario);
    }

    @Override
    public void deletaUsuarioPorEmail(String email) {
            boolean emailVerificado = repository.existsByEmail(email);

            if (!emailVerificado) {
                throw new EmailNotFoundExeption("Não existe um usuário com esse email: " + email);
            }

            repository.deleteByEmail(email);
    }

    @Override
    public UsuarioUpdateDto atualzaUsuarioPorEmail(UsuarioUpdateDto usuario, String email) {
        try {
            usuario.setSenha(usuario.getSenha() != null ? passwordEncoder.encode(usuario.getSenha()) : null);

            UsuarioDomain usuarioDomain = repository.findByEmail(email).orElseThrow(
                    () -> new EmailNotFoundExeption("Não existe um usuário com esse email"));

            UsuarioEntity usuarioEntity = converter.domainParaEntity(usuarioDomain);

            updateConverter.updateConverter(usuario, usuarioEntity);

            var usuarioConvertido = converter.entityParaDomain(usuarioEntity);

            UsuarioDomain usuarioAtualizado = repository.atualzaUsuarioPorEmail(usuarioConvertido);

            return converter.domainParaUpdateDto(usuarioAtualizado);

        }catch (UsuarioUpdateException e){
            throw new UsuarioUpdateException("Erro ao atualizar o usuario");
        }

    }

    private void verificaCampoLogin(UsuarioDomain usuario){
        String email = usuario.getEmail();
        String senha = usuario.getSenha();

        if(email.isBlank()){
            throw new EmailException("O email não pode ser vazio");
        }

        if(!this.repository.existsByEmail(email)){
            throw new EmailNotFoundExeption("Não existe um usuário com esse email");
        }

        if(senha.isBlank()){
            throw new SenhaException("A senha não pode estar vazia");
        }
    }

    private void verificaCamposCadastro(UsuarioDomain usuario) {
        String email = usuario.getEmail();
        String senha = usuario.getSenha();
        UsuarioRole role = usuario.getRole();

        if (email.isBlank()) {
            throw new EmailException("O email não pode ser vazio");
        } else if (this.repository.existsByEmail(email)) {
            throw new EmailExistsException("Já existe um usuário com esse email");
        }

        if (senha.isBlank()) {
            throw new SenhaException("A senha não pode estar vazia");
        }

        if (senha.length() < 6) {
            throw new SenhaException("A senha deve conter no mínimo 6 caracteres");
        }

        if (role == null || role.equals("")) {
            usuario.setRole(UsuarioRole.VENDEDOR);
        }

    }

}
