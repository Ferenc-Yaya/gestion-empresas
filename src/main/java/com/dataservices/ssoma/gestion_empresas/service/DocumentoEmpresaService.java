package com.dataservices.ssoma.gestion_empresas.service;

import com.dataservices.ssoma.gestion_empresas.dto.DocumentoEmpresaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DocumentoEmpresaService {

    DocumentoEmpresaDTO crearDocumento(DocumentoEmpresaDTO documentoDTO);

    DocumentoEmpresaDTO actualizarDocumento(UUID documentoId, DocumentoEmpresaDTO documentoDTO);

    DocumentoEmpresaDTO obtenerDocumentoPorId(UUID documentoId);

    List<DocumentoEmpresaDTO> obtenerDocumentosPorEmpresa(UUID empresaId);

    Page<DocumentoEmpresaDTO> obtenerDocumentosPorEmpresa(UUID empresaId, Pageable pageable);

    List<DocumentoEmpresaDTO> obtenerDocumentosPorFecha(LocalDate fecha);

    List<DocumentoEmpresaDTO> obtenerDocumentosPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);

    List<DocumentoEmpresaDTO> obtenerDocumentosPorEmpresaYRangoFechas(
            UUID empresaId, LocalDate fechaInicio, LocalDate fechaFin);

    List<DocumentoEmpresaDTO> obtenerDocumentosVencidos();

    List<DocumentoEmpresaDTO> obtenerDocumentosVencidos(LocalDate fecha);

    List<DocumentoEmpresaDTO> obtenerDocumentosPorVencer(int diasAnticipacion);

    List<DocumentoEmpresaDTO> obtenerDocumentosPorVencer(LocalDate fechaInicio, LocalDate fechaFin);

    List<DocumentoEmpresaDTO> obtenerDocumentosVencidosPorEmpresa(UUID empresaId);

    List<DocumentoEmpresaDTO> obtenerDocumentosVencidosPorEmpresa(UUID empresaId, LocalDate fecha);

    List<DocumentoEmpresaDTO> obtenerDocumentosPorVencerPorEmpresa(UUID empresaId, int diasAnticipacion);

    List<DocumentoEmpresaDTO> obtenerDocumentosPorVencerPorEmpresa(
            UUID empresaId, LocalDate fechaInicio, LocalDate fechaFin);

    List<DocumentoEmpresaDTO> buscarDocumentosPorNombre(String nombreDocumento);

    List<DocumentoEmpresaDTO> buscarDocumentosPorNombreYEmpresa(UUID empresaId, String nombreDocumento);

    void eliminarDocumento(UUID documentoId);

    // Métodos de estadísticas y consulta
    Long contarDocumentosPorEmpresa(UUID empresaId);

    Long contarDocumentosVencidos();

    Long contarDocumentosVencidos(LocalDate fecha);

    Long contarDocumentosPorVencer(int diasAnticipacion);

    Long contarDocumentosPorVencer(LocalDate fechaInicio, LocalDate fechaFin);

    Long contarDocumentosVencidosPorEmpresa(UUID empresaId);

    Long contarDocumentosVencidosPorEmpresa(UUID empresaId, LocalDate fecha);

    Long contarDocumentosPorVencerPorEmpresa(UUID empresaId, int diasAnticipacion);

    Long contarDocumentosPorVencerPorEmpresa(
            UUID empresaId, LocalDate fechaInicio, LocalDate fechaFin);

    Map<String, Object> obtenerEstadisticasDocumentos();

    Map<String, Object> obtenerEstadisticasDocumentosPorEmpresa(UUID empresaId);

    Map<String, Long> obtenerResumenVencimientos();

    Map<String, Long> obtenerResumenVencimientosPorEmpresa(UUID empresaId);
}
