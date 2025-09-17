package api.cad_funcionario.adapters.in.mapper;

import api.cad_funcionario.adapters.in.dto.UsuarioUpdateDto;
import api.cad_funcionario.adapters.out.entities.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UpdateConverter {

    void updateConverter(UsuarioUpdateDto dto, @MappingTarget UsuarioEntity entity);

}
