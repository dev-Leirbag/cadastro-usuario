package api.cad_funcionario.application.service;

import api.cad_funcionario.adapters.in.dto.LoginRequestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResponseDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResquestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioUpdateDto;
import api.cad_funcionario.adapters.in.mapper.Converter;
import api.cad_funcionario.adapters.in.mapper.UpdateConverter;
import api.cad_funcionario.adapters.out.entities.UsuarioEntity;
import api.cad_funcionario.application.domain.UsuarioDomain;
import api.cad_funcionario.application.domain.UsuarioRole;
import api.cad_funcionario.application.infra.config.TokenService;
import api.cad_funcionario.application.infra.exceptions.cadastro.EmailExistsException;
import api.cad_funcionario.application.infra.exceptions.login.EmailNotFoundExeption;
import api.cad_funcionario.application.infra.exceptions.login.SenhaIncorretException;
import api.cad_funcionario.porters.out.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private Converter converter;

    @Mock
    private UpdateConverter updateConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UsuarioServiceImpl service;


    @Test
    @DisplayName("Deve cadastrar um usuario com sucesso")
    void cadastroUsuarioCase1() {
        //Arrange (Preparação)
        var usuarioDto = new UsuarioResquestDTO("teste@teste.com", "123456", null);
        var usuarioDomain = new UsuarioDomain(null, "teste@teste.com", "123456", null);
        var senhaCriptografada = "secret-password-criptografada";
        var usuarioSalvoComId = new UsuarioDomain(UUID.randomUUID(), "teste@teste.com", senhaCriptografada, null);

        when(converter.dtoRequestParaDomain(usuarioDto)).thenReturn(usuarioDomain);
        when(repository.existsByEmail("teste@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn(senhaCriptografada);
        when(repository.salvaUsuario(usuarioDomain)).thenReturn(usuarioSalvoComId);
        when(converter.domainParaDtoRequest(usuarioSalvoComId)).thenReturn(usuarioDto);

        //Act (Ação)
        var resultado = service.cadastroUsuario(usuarioDto);

        //Assert (Verificação)
        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo("teste@teste.com");

        verify(converter, times(1)).dtoRequestParaDomain(usuarioDto);
        verify(repository, times(1)).existsByEmail("teste@teste.com");
        verify(passwordEncoder, times(1)).encode("123456");
        verify(repository, times(1)).salvaUsuario(usuarioDomain);
        verify(converter, times(1)).domainParaDtoRequest(usuarioSalvoComId);
    }

    @Test
    @DisplayName("Deve retornar um erro de email existente")
    void cadastroUsuarioCase2(){
        //Arrange (Preparação)
        var usuarioDto = new UsuarioResquestDTO("teste@teste.com", "123456", null);
        var usuarioDomain = new UsuarioDomain(null, "teste@teste.com", "123456", null);

        when(converter.dtoRequestParaDomain(usuarioDto)).thenReturn(usuarioDomain);
        when(repository.existsByEmail("teste@teste.com")).thenReturn(true);

        //Act (Ação)
        var excecao = assertThrows(EmailExistsException.class, () -> {
            service.cadastroUsuario(usuarioDto);
        });

        //Assert (Verificação)
        assertEquals("Já existe um usuário com esse email", excecao.getMessage());

        verify(converter, times(1)).dtoRequestParaDomain(usuarioDto);
        verify(repository, times(1)).existsByEmail("teste@teste.com");
        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).salvaUsuario(any());
    }

    @Test
    @DisplayName("Deve retornar um usuario com sucesso buscando pelo email")
    void buscaUsuarioPorEmailCase1(){
        var email = "teste@teste.com";
        var usuarioResponseDto = new UsuarioResponseDTO(null, email, "123456", null);
        var usuarioDomain = new UsuarioDomain(null, email, "123456", null);

        when(repository.findByEmail(email)).thenReturn(Optional.of(usuarioDomain));
        when(converter.domainParaDtoResponse(usuarioDomain)).thenReturn(usuarioResponseDto);

        var resultado = service.buscaUsuarioPorEmail(email);

        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo("teste@teste.com");

        verify(repository, times(1)).findByEmail(email);
        verify(converter, times(1)).domainParaDtoResponse(usuarioDomain);
        verify(converter, never()).dtoResponseParaDomain(any());
        verify(repository, never()).existsByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).salvaUsuario(any());
    }

    @Test
    @DisplayName("Deve retornar um erro ao buscar um email de usuario que não existe")
    void buscaUsuarioPorEmailCase2(){
        var email = "teste@teste.com";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        var excecao = assertThrows(EmailNotFoundExeption.class, () ->{
            service.buscaUsuarioPorEmail(email);
        });

        assertEquals("Não existe um usuário com esse email: " + email, excecao.getMessage());

        verify(repository, times(1)).findByEmail(email);
        verify(converter, never()).domainParaDtoResponse(any());
        verify(converter, never()).dtoResponseParaDomain(any());
        verify(repository, never()).existsByEmail(any());
        verify(repository, never()).salvaUsuario(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Deve retornar com sucesso quando o usuario faz login e gerar um token")
    void loginUsuarioCase1(){
        var loginDto = new LoginRequestDTO("teste@teste.com", "123456");
        var usuarioDomain = new UsuarioDomain(null, "teste@teste.com", "123456", null);
        var usuarioEntity = new UsuarioEntity(UUID.randomUUID(),"teste@teste.com", "123456", null);
        Authentication authMock = mock(Authentication.class);
        var token = "token-gerado";


        when(converter.loginDtoParaDomain(loginDto)).thenReturn(usuarioDomain);
        when(repository.existsByEmail("teste@teste.com")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(usuarioEntity);
        when(tokenService.generateToken(usuarioEntity)).thenReturn(token);

        var resultado = service.loginUsuario(loginDto);

        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(token);

        verify(converter, times(1)).loginDtoParaDomain(loginDto);
        verify(repository, times(1)).existsByEmail("teste@teste.com");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateToken(usuarioEntity);
    }

    @Test
    @DisplayName("Deve retornar um erro quando a senha estiver incorreta")
    void loginUsuarioCase2(){
        var loginDto = new LoginRequestDTO("teste@teste.com", "1234567");
        var usuarioDomain = new UsuarioDomain(null, "teste@teste.com", "123456", null);

        when(converter.loginDtoParaDomain(loginDto)).thenReturn(usuarioDomain);
        when(repository.existsByEmail("teste@teste.com")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Senha incorreta"));

        var excecao = assertThrows(SenhaIncorretException.class, () -> {
            service.loginUsuario(loginDto);
        });

        assertEquals("A senha esta incorreta. Tente Novamente!!", excecao.getMessage());

        verify(converter, times(1)).loginDtoParaDomain(loginDto);
        verify(repository, times(1)).existsByEmail("teste@teste.com");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve retornar um erro quando o email não existir")
    void loginUsuarioCase3(){
        String email = "naoexiste@email.com";
        var loginDto = new LoginRequestDTO(email, "1234567");
        var usuarioDomain = new UsuarioDomain(null, email, "123456", null);

        when(converter.loginDtoParaDomain(loginDto)).thenReturn(usuarioDomain);
        when(repository.existsByEmail(email)).thenReturn(false);

        var excecao = assertThrows(EmailNotFoundExeption.class, () -> {
            service.loginUsuario(loginDto);
        });

        assertEquals("Não existe um usuário com esse email", excecao.getMessage());

        verify(converter, times(1)).loginDtoParaDomain(loginDto);
        verify(repository, times(1)).existsByEmail(email);
        verify(authenticationManager, never()).authenticate(any());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve ter sucesso ao deletar um usuario pelo email")
    void deletaUsuarioPorEmailCase1(){
        var email = "teste@teste.com";

        when(repository.existsByEmail(email)).thenReturn(true);

        service.deletaUsuarioPorEmail(email);

        verify(repository, times(1)).existsByEmail(email);
        verify(repository, times(1)).deleteByEmail(email);
    }

    @Test
    @DisplayName("Deve retornar um erro ao tentar deletar um usuario que não existe")
    void deletaUsuarioPorEmailCase2(){
        var email = "naoexiste@email.com";

        when(repository.existsByEmail(email)).thenReturn(false);

        var excecao = assertThrows(EmailNotFoundExeption.class, () -> {
           service.deletaUsuarioPorEmail(email);
        });

        assertEquals("Não existe um usuário com esse email: " + email, excecao.getMessage());

        verify(repository, times(1)).existsByEmail(email);
        verify(repository, never()).deleteByEmail(any());
    }

    @Test
    @DisplayName("Deve retornar sucesso ao atualizar todos os campos do usuario")
    void atualzaUsuarioPorEmailCase1(){
        var emailOriginal = "teste@teste.com";
        var senhaOriginal = "123456";
        UsuarioRole roleOriginal = UsuarioRole.VENDEDOR;
        var senhaSecreta = "senha-secreta";
        var usuarioUpdate = new UsuarioUpdateDto("teste@update.com", "1234567", UsuarioRole.ADMIN);
        var usuarioDomain = new UsuarioDomain(UUID.randomUUID(), emailOriginal, senhaOriginal, roleOriginal);
        var usuarioEntity = new UsuarioEntity(usuarioDomain.getId(), usuarioDomain.getEmail()
                ,usuarioDomain.getSenha(),usuarioDomain.getRole());
        var usuarioAtualizado = new UsuarioDomain(usuarioEntity.getId(), usuarioUpdate.getEmail()
                ,usuarioUpdate.getSenha() , usuarioUpdate.getRole());

        when(passwordEncoder.encode("1234567")).thenReturn(senhaSecreta);
        when(repository.findByEmail(emailOriginal)).thenReturn(Optional.of(usuarioDomain));
        when(converter.domainParaEntity(usuarioDomain)).thenReturn(usuarioEntity);
        doNothing().when(updateConverter).updateConverter(usuarioUpdate, usuarioEntity);
        when(converter.entityParaDomain(usuarioEntity)).thenReturn(usuarioDomain);
        when(repository.atualzaUsuarioPorEmail(usuarioDomain)).thenReturn(usuarioAtualizado);
        when(converter.domainParaUpdateDto(usuarioAtualizado)).thenReturn(usuarioUpdate);

        var resultado = service.atualzaUsuarioPorEmail(usuarioUpdate, emailOriginal);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("teste@update.com");
        assertThat(resultado.getSenha()).isEqualTo(senhaSecreta);
        assertThat(resultado.getRole()).isEqualTo(usuarioUpdate.getRole());

        verify(passwordEncoder, times(1)).encode("1234567");
        verify(repository, times(1)).findByEmail(emailOriginal);
        verify(converter, times(1)).domainParaEntity(usuarioDomain);
        verify(updateConverter, times(1)).updateConverter(usuarioUpdate, usuarioEntity);
        verify(converter, times(1)).entityParaDomain(usuarioEntity);
        verify(repository, times(1)).atualzaUsuarioPorEmail(usuarioDomain);
        verify(converter, times(1)).domainParaUpdateDto(usuarioAtualizado);
    }

    @Test
    @DisplayName("Deve retornar sucesso ao atualizar apenas a senha do usuario")
    void atualzaUsuarioPorEmailCase2(){
        var senhaOriginal = "123456";
        var senhaSecreta = "senha-secreta";
        var usuarioUpdate = new UsuarioUpdateDto(null, "1234567", null);
        var usuarioDomain = new UsuarioDomain(UUID.randomUUID(), "teste@teste.com", senhaOriginal, UsuarioRole.ADMIN);
        var usuarioEntity = new UsuarioEntity(usuarioDomain.getId(), usuarioDomain.getEmail()
                ,usuarioDomain.getSenha(),usuarioDomain.getRole());
        var usuarioAtualizado = new UsuarioDomain(usuarioEntity.getId(), usuarioEntity.getEmail()
                ,usuarioUpdate.getSenha() , usuarioEntity.getRole());

        when(passwordEncoder.encode("1234567")).thenReturn(senhaSecreta);
        when(repository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuarioDomain));
        when(converter.domainParaEntity(usuarioDomain)).thenReturn(usuarioEntity);
        doNothing().when(updateConverter).updateConverter(usuarioUpdate, usuarioEntity);
        when(converter.entityParaDomain(usuarioEntity)).thenReturn(usuarioDomain);
        when(repository.atualzaUsuarioPorEmail(usuarioDomain)).thenReturn(usuarioAtualizado);
        when(converter.domainParaUpdateDto(usuarioAtualizado)).thenReturn(usuarioUpdate);

        var resultado = service.atualzaUsuarioPorEmail(usuarioUpdate, "teste@teste.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getSenha()).isEqualTo(senhaSecreta);

        verify(passwordEncoder, times(1)).encode("1234567");
        verify(repository, times(1)).findByEmail("teste@teste.com");
        verify(converter, times(1)).domainParaEntity(usuarioDomain);
        verify(updateConverter, times(1)).updateConverter(usuarioUpdate, usuarioEntity);
        verify(converter, times(1)).entityParaDomain(usuarioEntity);
        verify(repository, times(1)).atualzaUsuarioPorEmail(usuarioDomain);
        verify(converter, times(1)).domainParaUpdateDto(usuarioAtualizado);
    }

    @Test
    @DisplayName("Deve retornar sucesso ao atualizar apenas a role do usuario")
    void atualzaUsuarioPorEmailCase3(){
        var emailOriginal = "teste@teste.com";
        UsuarioRole roleOriginal = UsuarioRole.VENDEDOR;
        var usuarioUpdate = new UsuarioUpdateDto(null, null, UsuarioRole.ADMIN);
        var usuarioDomain = new UsuarioDomain(UUID.randomUUID(), emailOriginal, "123456", roleOriginal);
        var usuarioEntity = new UsuarioEntity(usuarioDomain.getId(), usuarioDomain.getEmail()
                ,usuarioDomain.getSenha(),usuarioDomain.getRole());
        var usuarioAtualizado = new UsuarioDomain(usuarioEntity.getId(), usuarioEntity.getEmail()
                ,usuarioEntity.getSenha() , usuarioUpdate.getRole());

        when(repository.findByEmail(emailOriginal)).thenReturn(Optional.of(usuarioDomain));
        when(converter.domainParaEntity(usuarioDomain)).thenReturn(usuarioEntity);
        doNothing().when(updateConverter).updateConverter(usuarioUpdate, usuarioEntity);
        when(converter.entityParaDomain(usuarioEntity)).thenReturn(usuarioDomain);
        when(repository.atualzaUsuarioPorEmail(usuarioDomain)).thenReturn(usuarioAtualizado);
        when(converter.domainParaUpdateDto(usuarioAtualizado)).thenReturn(usuarioUpdate);

        var resultado = service.atualzaUsuarioPorEmail(usuarioUpdate, emailOriginal);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getRole()).isEqualTo(usuarioUpdate.getRole());

        verify(passwordEncoder, never()).encode(any());
        verify(repository, times(1)).findByEmail(emailOriginal);
        verify(converter, times(1)).domainParaEntity(usuarioDomain);
        verify(updateConverter, times(1)).updateConverter(usuarioUpdate, usuarioEntity);
        verify(converter, times(1)).entityParaDomain(usuarioEntity);
        verify(repository, times(1)).atualzaUsuarioPorEmail(usuarioDomain);
        verify(converter, times(1)).domainParaUpdateDto(usuarioAtualizado);
    }

    @Test
    @DisplayName("Retorna um erro ao não encontrar o email do usuario")
    void atualzaUsuarioPorEmailCase4(){
        var emailOriginal = "naoexiste@email.com";
        var usuarioUpdate = new UsuarioUpdateDto(null, null, null);

        when(repository.findByEmail(emailOriginal)).thenReturn(Optional.empty());

        var excecao = assertThrows(EmailNotFoundExeption.class, () -> {
            service.atualzaUsuarioPorEmail(usuarioUpdate, emailOriginal);
        });

        assertEquals("Não existe um usuário com esse email", excecao.getMessage());

        verify(passwordEncoder, never()).encode(any());
        verify(repository, times(1)).findByEmail(emailOriginal);
    }
}