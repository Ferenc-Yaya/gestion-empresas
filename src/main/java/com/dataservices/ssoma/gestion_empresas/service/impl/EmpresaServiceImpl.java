package com.dataservices.ssoma.gestion_empresas.service.impl;

import com.dataservices.ssoma.gestion_empresas.dto.EmpresaDTO;
import com.dataservices.ssoma.gestion_empresas.entity.Empresa;
import com.dataservices.ssoma.gestion_empresas.exception.ResourceNotFoundException;
import com.dataservices.ssoma.gestion_empresas.exception.BusinessException;
import com.dataservices.ssoma.gestion_empresas.mapper.EmpresaMapper;
import com.dataservices.ssoma.gestion_empresas.repository.EmpresaRepository;
import com.dataservices.ssoma.gestion_empresas.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    @Override
    public EmpresaDTO crearEmpresa(EmpresaDTO empresaDTO) {
        log.info("Creando nueva empresa: {}", empresaDTO.getRazonSocial());

        // Validar que el RUC sea único
        if (empresaDTO.getRuc() != null && empresaRepository.existsByRuc(empresaDTO.getRuc())) {
            throw new BusinessException("Ya existe una empresa con el RUC: " + empresaDTO.getRuc());
        }

        Empresa empresa = empresaMapper.toEntity(empresaDTO);
        Empresa savedEmpresa = empresaRepository.save(empresa);

        log.info("Empresa creada exitosamente con ID: {}", savedEmpresa.getEmpresaId());
        return empresaMapper.toDTO(savedEmpresa);
    }

    @Override
    public EmpresaDTO actualizarEmpresa(UUID empresaId, EmpresaDTO empresaDTO) {
        log.info("Actualizando empresa con ID: {}", empresaId);

        Empresa empresaExistente = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + empresaId));

        // Validar que el RUC sea único (excluyendo la empresa actual)
        if (empresaDTO.getRuc() != null &&
                empresaRepository.existsByRucAndEmpresaIdNot(empresaDTO.getRuc(), empresaId)) {
            throw new BusinessException("Ya existe otra empresa con el RUC: " + empresaDTO.getRuc());
        }

        // Actualizar campos
        empresaExistente.setRuc(empresaDTO.getRuc());
        empresaExistente.setRazonSocial(empresaDTO.getRazonSocial());
        empresaExistente.setDireccion(empresaDTO.getDireccion());
        empresaExistente.setSector(empresaDTO.getSector());
        empresaExistente.setScoreSeguridad(empresaDTO.getScoreSeguridad());

        Empresa updatedEmpresa = empresaRepository.save(empresaExistente);

        log.info("Empresa actualizada exitosamente: {}", empresaId);
        return empresaMapper.toDTO(updatedEmpresa);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaDTO obtenerEmpresaPorId(UUID empresaId) {
        log.info("Obteniendo empresa por ID: {}", empresaId);

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + empresaId));

        return empresaMapper.toDTO(empresa);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaDTO obtenerEmpresaPorRuc(String ruc) {
        log.info("Obteniendo empresa por RUC: {}", ruc);

        Empresa empresa = empresaRepository.findByRuc(ruc)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con RUC: " + ruc));

        return empresaMapper.toDTO(empresa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> obtenerTodasLasEmpresas() {
        log.info("Obteniendo todas las empresas");

        List<Empresa> empresas = empresaRepository.findAll();
        return empresas.stream()
                .map(empresaMapper::toDTOWithoutDocumentos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaDTO> obtenerEmpresasPaginadas(Pageable pageable) {
        log.info("Obteniendo empresas paginadas");

        Page<Empresa> empresasPage = empresaRepository.findAll(pageable);
        return empresasPage.map(empresaMapper::toDTOWithoutDocumentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> buscarEmpresasPorRazonSocial(String razonSocial) {
        log.info("Buscando empresas por razón social: {}", razonSocial);

        List<Empresa> empresas = empresaRepository.findByRazonSocialContainingIgnoreCase(razonSocial);
        return empresas.stream()
                .map(empresaMapper::toDTOWithoutDocumentos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaDTO> buscarEmpresasPorRazonSocial(String razonSocial, Pageable pageable) {
        log.info("Buscando empresas paginadas por razón social: {}", razonSocial);

        Page<Empresa> empresasPage = empresaRepository.findByRazonSocialContainingIgnoreCase(razonSocial, pageable);
        return empresasPage.map(empresaMapper::toDTOWithoutDocumentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> obtenerEmpresasPorSector(String sector) {
        log.info("Obteniendo empresas por sector: {}", sector);

        List<Empresa> empresas = empresaRepository.findBySector(sector);
        return empresas.stream()
                .map(empresaMapper::toDTOWithoutDocumentos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaDTO> obtenerEmpresasPorSector(String sector, Pageable pageable) {
        log.info("Obteniendo empresas paginadas por sector: {}", sector);

        Page<Empresa> empresasPage = empresaRepository.findBySector(sector, pageable);
        return empresasPage.map(empresaMapper::toDTOWithoutDocumentos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> obtenerEmpresasPorRangoScore(Integer scoreMin, Integer scoreMax) {
        log.info("Obteniendo empresas por rango de score: {} - {}", scoreMin, scoreMax);

        List<Empresa> empresas = empresaRepository.findByScoreSeguridadBetween(scoreMin, scoreMax);
        return empresas.stream()
                .map(empresaMapper::toDTOWithoutDocumentos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpresaDTO> buscarEmpresasConFiltros(
            String razonSocial, String sector, Integer scoreMin, Integer scoreMax, Pageable pageable) {

        log.info("Buscando empresas con filtros - Razón: {}, Sector: {}, Score: {}-{}",
                razonSocial, sector, scoreMin, scoreMax);

        Page<Empresa> empresasPage = empresaRepository.findByFiltros(
                razonSocial, sector, scoreMin, scoreMax, pageable);
        return empresasPage.map(empresaMapper::toDTOWithoutDocumentos);
    }

    @Override
    public void eliminarEmpresa(UUID empresaId) {
        log.info("Eliminando empresa con ID: {}", empresaId);

        if (!empresaRepository.existsById(empresaId)) {
            throw new ResourceNotFoundException("Empresa no encontrada con ID: " + empresaId);
        }

        empresaRepository.deleteById(empresaId);
        log.info("Empresa eliminada exitosamente: {}", empresaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerScorePromedioSeguridad() {
        return empresaRepository.findAverageScoreSeguridad();
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarEmpresasPorRangoScore(Integer scoreMin, Integer scoreMax) {
        return empresaRepository.countByScoreSeguridadBetween(scoreMin, scoreMax);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerSectoresDisponibles() {
        return empresaRepository.findDistinctSectores();
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarEmpresasPorSector(String sector) {
        return empresaRepository.countBySector(sector);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> obtenerTop10EmpresasPorScore() {
        log.info("Obteniendo top 10 empresas por score de seguridad");

        Pageable top10 = PageRequest.of(0, 10);
        List<Empresa> empresas = empresaRepository.findTop10ByOrderByScoreSeguridadDesc(top10);
        return empresas.stream()
                .map(empresaMapper::toDTOWithoutDocumentos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> obtenerEmpresasConScoreBajo(Integer scoreMinimo) {
        log.info("Obteniendo empresas con score bajo (menor a {})", scoreMinimo);

        List<Empresa> empresas = empresaRepository.findEmpresasConScoreBajo(scoreMinimo);
        return empresas.stream()
                .map(empresaMapper::toDTOWithoutDocumentos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasGenerales() {
        log.info("Obteniendo estadísticas generales de empresas");

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalEmpresas", empresaRepository.count());
        estadisticas.put("scorePromedio", obtenerScorePromedioSeguridad());
        estadisticas.put("empresasScoreAlto", contarEmpresasPorRangoScore(80, 100));
        estadisticas.put("empresasScoreMedio", contarEmpresasPorRangoScore(50, 79));
        estadisticas.put("empresasScoreBajo", contarEmpresasPorRangoScore(0, 49));
        estadisticas.put("totalSectores", obtenerSectoresDisponibles().size());

        return estadisticas;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerDistribucionPorSector() {
        log.info("Obteniendo distribución de empresas por sector");

        List<String> sectores = obtenerSectoresDisponibles();
        Map<String, Long> distribucion = new HashMap<>();

        for (String sector : sectores) {
            distribucion.put(sector, contarEmpresasPorSector(sector));
        }

        return distribucion;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeRuc(String ruc) {
        return empresaRepository.existsByRuc(ruc);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeRucParaActualizar(String ruc, UUID empresaId) {
        return empresaRepository.existsByRucAndEmpresaIdNot(ruc, empresaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosPorEmpresa(UUID empresaId) {
        return empresaRepository.countDocumentosByEmpresaId(empresaId);
    }
}
