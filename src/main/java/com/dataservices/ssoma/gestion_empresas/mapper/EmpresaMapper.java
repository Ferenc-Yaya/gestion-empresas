package com.dataservices.ssoma.gestion_empresas.mapper;

import com.dataservices.ssoma.gestion_empresas.dto.EmpresaDTO;
import com.dataservices.ssoma.gestion_empresas.entity.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {DocumentoEmpresaMapper.class}
)
public interface EmpresaMapper {

    @Mapping(target = "documentos", ignore = true)
    Empresa toEntity(EmpresaDTO empresaDTO);

    @Mapping(target = "documentos", source = "documentos")
    @Named("toDTOWithDocumentos")
    EmpresaDTO toDTO(Empresa empresa);

    @Mapping(target = "documentos", ignore = true)
    @Named("toDTOWithoutDocumentos")
    EmpresaDTO toDTOWithoutDocumentos(Empresa empresa);

    // Usar específicamente el método sin documentos para listas
    @Mapping(target = "documentos", ignore = true)
    List<EmpresaDTO> toDTOList(List<Empresa> empresas);

    List<Empresa> toEntityList(List<EmpresaDTO> empresaDTOs);

    @Mapping(target = "empresaId", ignore = true)
    @Mapping(target = "documentos", ignore = true)
    void updateEntityFromDTO(EmpresaDTO empresaDTO, @MappingTarget Empresa empresa);
}
