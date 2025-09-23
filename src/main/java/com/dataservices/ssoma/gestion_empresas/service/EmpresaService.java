package com.dataservices.ssoma.gestion_empresas.service;

import com.dataservices.ssoma.gestion_empresas.dto.EmpresaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EmpresaService {

    EmpresaDTO crearEmpresa(EmpresaDTO empresaDTO);

    EmpresaDTO actualizarEmpresa(UUID empresaId, EmpresaDTO empresaDTO);

    EmpresaDTO obtenerEmpresaPorId(UUID empresaId);

    EmpresaDTO obtenerEmpresaPorRuc(String ruc);

    List<EmpresaDTO> obtenerTodasLasEmpresas();

    Page<EmpresaDTO> obtenerEmpresasPaginadas(Pageable pageable);

    List<EmpresaDTO> buscarEmpresasPorRazonSocial(String razonSocial);

    Page<EmpresaDTO> buscarEmpresasPorRazonSocial(String razonSocial, Pageable pageable);

    List<EmpresaDTO> obtenerEmpresasPorSector(String sector);

    Page<EmpresaDTO> obtenerEmpresasPorSector(String sector, Pageable pageable);

    List<EmpresaDTO> obtenerEmpresasPorRangoScore(Integer scoreMin, Integer scoreMax);

    Page<EmpresaDTO> buscarEmpresasConFiltros(
            String razonSocial, String sector, Integer scoreMin, Integer scoreMax, Pageable pageable);

    void eliminarEmpresa(UUID empresaId);

    // Métodos de estadísticas y consulta
    Double obtenerScorePromedioSeguridad();

    Long contarEmpresasPorRangoScore(Integer scoreMin, Integer scoreMax);

    List<String> obtenerSectoresDisponibles();

    Long contarEmpresasPorSector(String sector);

    List<EmpresaDTO> obtenerTop10EmpresasPorScore();

    List<EmpresaDTO> obtenerEmpresasConScoreBajo(Integer scoreMinimo);

    Map<String, Object> obtenerEstadisticasGenerales();

    Map<String, Long> obtenerDistribucionPorSector();

    boolean existeRuc(String ruc);

    boolean existeRucParaActualizar(String ruc, UUID empresaId);

    Long contarDocumentosPorEmpresa(UUID empresaId);
}
