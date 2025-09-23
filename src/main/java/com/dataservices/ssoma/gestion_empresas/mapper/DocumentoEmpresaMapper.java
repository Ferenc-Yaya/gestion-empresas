package com.dataservices.ssoma.gestion_empresas.mapper;

import com.dataservices.ssoma.gestion_empresas.dto.DocumentoEmpresaDTO;
import com.dataservices.ssoma.gestion_empresas.entity.DocumentoEmpresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DocumentoEmpresaMapper {

    @Mapping(target = "empresa", ignore = true)
    DocumentoEmpresa toEntity(DocumentoEmpresaDTO documentoDTO);

    @Mapping(source = "empresa.empresaId", target = "empresaId")
    DocumentoEmpresaDTO toDTO(DocumentoEmpresa documento);

    List<DocumentoEmpresaDTO> toDTOList(List<DocumentoEmpresa> documentos);

    List<DocumentoEmpresa> toEntityList(List<DocumentoEmpresaDTO> documentoDTOs);

    @Mapping(target = "documentoEmpresaId", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    void updateEntityFromDTO(DocumentoEmpresaDTO documentoDTO, @MappingTarget DocumentoEmpresa documento);
}
