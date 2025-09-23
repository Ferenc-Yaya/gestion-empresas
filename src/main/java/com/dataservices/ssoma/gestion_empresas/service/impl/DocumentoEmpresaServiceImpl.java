package com.dataservices.ssoma.gestion_empresas.service.impl;

import com.dataservices.ssoma.gestion_empresas.dto.DocumentoEmpresaDTO;
import com.dataservices.ssoma.gestion_empresas.entity.Empresa;
import com.dataservices.ssoma.gestion_empresas.entity.DocumentoEmpresa;
import com.dataservices.ssoma.gestion_empresas.exception.ResourceNotFoundException;
import com.dataservices.ssoma.gestion_empresas.mapper.DocumentoEmpresaMapper;
import com.dataservices.ssoma.gestion_empresas.repository.EmpresaRepository;
import com.dataservices.ssoma.gestion_empresas.repository.DocumentoEmpresaRepository;
import com.dataservices.ssoma.gestion_empresas.service.DocumentoEmpresaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentoEmpresaServiceImpl implements DocumentoEmpresaService {

    private final DocumentoEmpresaRepository documentoRepository;
    private final EmpresaRepository empresaRepository;
    private final DocumentoEmpresaMapper documentoMapper;

    @Override
    public DocumentoEmpresaDTO crearDocumento(DocumentoEmpresaDTO documentoDTO) {
        log.info("Creando nuevo documento para empresa: {}", documentoDTO.getEmpresaId());

        // Validar que la empresa existe
        Empresa empresa = empresaRepository.findById(documentoDTO.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + documentoDTO.getEmpresaId()));

        DocumentoEmpresa documento = documentoMapper.toEntity(documentoDTO);
        documento.setEmpresa(empresa);

        DocumentoEmpresa savedDocumento = documentoRepository.save(documento);

        log.info("Documento creado exitosamente con ID: {}", savedDocumento.getDocumentoEmpresaId());
        return documentoMapper.toDTO(savedDocumento);
    }

    @Override
    public DocumentoEmpresaDTO actualizarDocumento(UUID documentoId, DocumentoEmpresaDTO documentoDTO) {
        log.info("Actualizando documento con ID: {}", documentoId);

        DocumentoEmpresa documentoExistente = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + documentoId));

        // Validar que la empresa existe si se está cambiando
        if (!documentoExistente.getEmpresaId().equals(documentoDTO.getEmpresaId())) {
            Empresa nuevaEmpresa = empresaRepository.findById(documentoDTO.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + documentoDTO.getEmpresaId()));
            documentoExistente.setEmpresa(nuevaEmpresa);
        }

        // Actualizar campos
        documentoExistente.setNombreDocumento(documentoDTO.getNombreDocumento());
        documentoExistente.setFechaVencimiento(documentoDTO.getFechaVencimiento());
        documentoExistente.setDocumentoUrl(documentoDTO.getDocumentoUrl());

        DocumentoEmpresa updatedDocumento = documentoRepository.save(documentoExistente);

        log.info("Documento actualizado exitosamente: {}", documentoId);
        return documentoMapper.toDTO(updatedDocumento);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentoEmpresaDTO obtenerDocumentoPorId(UUID documentoId) {
        log.info("Obteniendo documento por ID: {}", documentoId);

        DocumentoEmpresa documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + documentoId));

        return documentoMapper.toDTO(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosPorEmpresa(UUID empresaId) {
        log.info("Obteniendo documentos por empresa ID: {}", empresaId);

        List<DocumentoEmpresa> documentos = documentoRepository.findByEmpresaId(empresaId);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentoEmpresaDTO> obtenerDocumentosPorEmpresa(UUID empresaId, Pageable pageable) {
        log.info("Obteniendo documentos paginados por empresa ID: {}", empresaId);

        Page<DocumentoEmpresa> documentosPage = documentoRepository.findByEmpresaId(empresaId, pageable);
        return documentosPage.map(documentoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosPorFecha(LocalDate fecha) {
        log.info("Obteniendo documentos por fecha: {}", fecha);

        List<DocumentoEmpresa> documentos = documentoRepository.findByFechaVencimiento(fecha);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Obteniendo documentos por rango de fechas: {} - {}", fechaInicio, fechaFin);

        List<DocumentoEmpresa> documentos = documentoRepository
                .findByFechaVencimientoBetween(fechaInicio, fechaFin);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosPorEmpresaYRangoFechas(
            UUID empresaId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Obteniendo documentos por empresa {} y rango de fechas: {} - {}",
                empresaId, fechaInicio, fechaFin);

        List<DocumentoEmpresa> documentos = documentoRepository
                .findByEmpresaIdAndFechaVencimientoBetween(empresaId, fechaInicio, fechaFin);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosVencidos() {
        return obtenerDocumentosVencidos(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosVencidos(LocalDate fecha) {
        log.info("Obteniendo documentos vencidos hasta: {}", fecha);

        List<DocumentoEmpresa> documentos = documentoRepository.findDocumentosVencidos(fecha);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosPorVencer(int diasAnticipacion) {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(diasAnticipacion);
        return obtenerDocumentosPorVencer(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosPorVencer(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Obteniendo documentos por vencer entre: {} - {}", fechaInicio, fechaFin);

        List<DocumentoEmpresa> documentos = documentoRepository
                .findDocumentosPorVencer(fechaInicio, fechaFin);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosVencidosPorEmpresa(UUID empresaId) {
        return obtenerDocumentosVencidosPorEmpresa(empresaId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosVencidosPorEmpresa(UUID empresaId, LocalDate fecha) {
        log.info("Obteniendo documentos vencidos por empresa {} hasta: {}", empresaId, fecha);

        List<DocumentoEmpresa> documentos = documentoRepository
                .findDocumentosVencidosByEmpresa(empresaId, fecha);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosPorVencerPorEmpresa(UUID empresaId, int diasAnticipacion) {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(diasAnticipacion);
        return obtenerDocumentosPorVencerPorEmpresa(empresaId, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> obtenerDocumentosPorVencerPorEmpresa(
            UUID empresaId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Obteniendo documentos por vencer por empresa {} entre: {} - {}",
                empresaId, fechaInicio, fechaFin);

        List<DocumentoEmpresa> documentos = documentoRepository
                .findDocumentosPorVencerByEmpresa(empresaId, fechaInicio, fechaFin);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> buscarDocumentosPorNombre(String nombreDocumento) {
        log.info("Buscando documentos por nombre: {}", nombreDocumento);

        List<DocumentoEmpresa> documentos = documentoRepository
                .findByNombreDocumentoContainingIgnoreCase(nombreDocumento);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentoEmpresaDTO> buscarDocumentosPorNombreYEmpresa(UUID empresaId, String nombreDocumento) {
        log.info("Buscando documentos por empresa {} y nombre: {}", empresaId, nombreDocumento);

        List<DocumentoEmpresa> documentos = documentoRepository
                .findByEmpresaIdAndNombreDocumentoContainingIgnoreCase(empresaId, nombreDocumento);
        return documentos.stream()
                .map(documentoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarDocumento(UUID documentoId) {
        log.info("Eliminando documento con ID: {}", documentoId);

        if (!documentoRepository.existsById(documentoId)) {
            throw new ResourceNotFoundException("Documento no encontrado con ID: " + documentoId);
        }

        documentoRepository.deleteById(documentoId);
        log.info("Documento eliminado exitosamente: {}", documentoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosPorEmpresa(UUID empresaId) {
        return documentoRepository.countByEmpresaId(empresaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosVencidos() {
        return contarDocumentosVencidos(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosVencidos(LocalDate fecha) {
        return documentoRepository.countDocumentosVencidos(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosPorVencer(int diasAnticipacion) {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(diasAnticipacion);
        return contarDocumentosPorVencer(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosPorVencer(LocalDate fechaInicio, LocalDate fechaFin) {
        return documentoRepository.countDocumentosPorVencer(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosVencidosPorEmpresa(UUID empresaId) {
        return contarDocumentosVencidosPorEmpresa(empresaId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosVencidosPorEmpresa(UUID empresaId, LocalDate fecha) {
        return documentoRepository.countDocumentosVencidosByEmpresa(empresaId, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosPorVencerPorEmpresa(UUID empresaId, int diasAnticipacion) {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(diasAnticipacion);
        return contarDocumentosPorVencerPorEmpresa(empresaId, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarDocumentosPorVencerPorEmpresa(
            UUID empresaId, LocalDate fechaInicio, LocalDate fechaFin) {
        return documentoRepository.countDocumentosPorVencerByEmpresa(empresaId, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasDocumentos() {
        log.info("Obteniendo estadísticas generales de documentos");

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalDocumentos", documentoRepository.count());
        estadisticas.put("documentosVencidos", contarDocumentosVencidos());
        estadisticas.put("documentosPorVencer30Dias", contarDocumentosPorVencer(30));
        estadisticas.put("documentosPorVencer7Dias", contarDocumentosPorVencer(7));

        return estadisticas;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasDocumentosPorEmpresa(UUID empresaId) {
        log.info("Obteniendo estadísticas de documentos para empresa: {}", empresaId);

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalDocumentos", contarDocumentosPorEmpresa(empresaId));
        estadisticas.put("documentosVencidos", contarDocumentosVencidosPorEmpresa(empresaId));
        estadisticas.put("documentosPorVencer30Dias", contarDocumentosPorVencerPorEmpresa(empresaId, 30));
        estadisticas.put("documentosPorVencer7Dias", contarDocumentosPorVencerPorEmpresa(empresaId, 7));

        return estadisticas;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerResumenVencimientos() {
        log.info("Obteniendo resumen de vencimientos");

        Map<String, Long> resumen = new HashMap<>();
        resumen.put("vencidos", contarDocumentosVencidos());
        resumen.put("vencenEn7Dias", contarDocumentosPorVencer(7));
        resumen.put("vencenEn15Dias", contarDocumentosPorVencer(15));
        resumen.put("vencenEn30Dias", contarDocumentosPorVencer(30));

        return resumen;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerResumenVencimientosPorEmpresa(UUID empresaId) {
        log.info("Obteniendo resumen de vencimientos para empresa: {}", empresaId);

        Map<String, Long> resumen = new HashMap<>();
        resumen.put("vencidos", contarDocumentosVencidosPorEmpresa(empresaId));
        resumen.put("vencenEn7Dias", contarDocumentosPorVencerPorEmpresa(empresaId, 7));
        resumen.put("vencenEn15Dias", contarDocumentosPorVencerPorEmpresa(empresaId, 15));
        resumen.put("vencenEn30Dias", contarDocumentosPorVencerPorEmpresa(empresaId, 30));

        return resumen;
    }
}
