package api.cad_funcionario.adapters.in.mapper;

import api.cad_funcionario.adapters.in.dto.LoginRequestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResponseDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioResquestDTO;
import api.cad_funcionario.adapters.in.dto.UsuarioUpdateDto;
import api.cad_funcionario.adapters.out.entities.UsuarioEntity;
import api.cad_funcionario.application.domain.UsuarioDomain;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Converter {

    //UsuarioDomain

    UsuarioDomain dtoRequestParaDomain(UsuarioResquestDTO dto);
    UsuarioDomain dtoResponseParaDomain(UsuarioResponseDTO responseDTO);
    UsuarioDomain loginDtoParaDomain(LoginRequestDTO loginRequestDTO);
    UsuarioDomain entityParaDomain(UsuarioEntity entity);
    List<UsuarioDomain> entitiesParaDomain(List<UsuarioEntity> entities);

    //UsuarioEntity
    UsuarioEntity domainParaEntity(UsuarioDomain domain);
    List<UsuarioEntity> domainsParaEntity(List<UsuarioDomain> domains);

    //UsuarioRequestDTO
    UsuarioResquestDTO domainParaDtoRequest(UsuarioDomain domain);

    //UsuarioResponseDTO
    UsuarioResponseDTO domainParaDtoResponse(UsuarioDomain domain);
    List<UsuarioResponseDTO> domainsParaDtoResponse(List<UsuarioDomain> domains);

    //LoginRequestDTO
    LoginRequestDTO domainParaLoginDto(UsuarioDomain domain);

    //UpdateConverter
    UsuarioUpdateDto domainParaUpdateDto(UsuarioDomain domain);
}
