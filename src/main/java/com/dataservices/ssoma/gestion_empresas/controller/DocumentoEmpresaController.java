package com.dataservices.ssoma.gestion_empresas.controller;

import com.dataservices.ssoma.gestion_empresas.dto.DocumentoEmpresaDTO;
import com.dataservices.ssoma.gestion_empresas.dto.response.ApiResponse;
import com.dataservices.ssoma.gestion_empresas.service.DocumentoEmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documentos-empresa")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DocumentoEmpresaController {

    private final DocumentoEmpresaService documentoService;

    @PostMapping
    public ResponseEntity<ApiResponse<DocumentoEmpresaDTO>> crearDocumento(
            @Valid @RequestBody DocumentoEmpresaDTO documentoDTO) {
        log.info("POST /documentos-empresa - Creando nuevo documento para empresa: {}",
                documentoDTO.getEmpresaId());

        DocumentoEmpresaDTO nuevoDocumento = documentoService.crearDocumento(documentoDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Documento creado exitosamente", nuevoDocumento));
    }

    @PutMapping("/{documentoId}")
    public ResponseEntity<ApiResponse<DocumentoEmpresaDTO>> actualizarDocumento(
            @PathVariable UUID documentoId,
            @Valid @RequestBody DocumentoEmpresaDTO documentoDTO) {
        log.info("PUT /documentos-empresa/{} - Actualizando documento", documentoId);

        DocumentoEmpresaDTO documentoActualizado = documentoService
                .actualizarDocumento(documentoId, documentoDTO);

        return ResponseEntity.ok(ApiResponse.success(
                "Documento actualizado exitosamente", documentoActualizado));
    }

    @GetMapping("/{documentoId}")
    public ResponseEntity<ApiResponse<DocumentoEmpresaDTO>> obtenerDocumentoPorId(
            @PathVariable UUID documentoId) {
        log.info("GET /documentos-empresa/{} - Obteniendo documento por ID", documentoId);

        DocumentoEmpresaDTO documento = documentoService.obtenerDocumentoPorId(documentoId);

        return ResponseEntity.ok(ApiResponse.success("Documento encontrado", documento));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosPorEmpresa(
            @PathVariable UUID empresaId) {
        log.info("GET /documentos-empresa/empresa/{} - Obteniendo documentos por empresa", empresaId);

        List<DocumentoEmpresaDTO> documentos = documentoService.obtenerDocumentosPorEmpresa(empresaId);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos", documentos.size()),
                documentos));
    }

    @GetMapping("/empresa/{empresaId}/paginado")
    public ResponseEntity<ApiResponse<Page<DocumentoEmpresaDTO>>> obtenerDocumentosPorEmpresaPaginado(
            @PathVariable UUID empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaVencimiento") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("GET /documentos-empresa/empresa/{}/paginado - Página: {}, Tamaño: {}",
                empresaId, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentoEmpresaDTO> documentosPage = documentoService
                .obtenerDocumentosPorEmpresa(empresaId, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Página %d de %d - Total: %d documentos",
                        page + 1, documentosPage.getTotalPages(),
                        documentosPage.getTotalElements()),
                documentosPage));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        log.info("GET /documentos-empresa/fecha/{} - Obteniendo documentos por fecha", fecha);

        List<DocumentoEmpresaDTO> documentos = documentoService.obtenerDocumentosPorFecha(fecha);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos para la fecha %s",
                        documentos.size(), fecha),
                documentos));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        log.info("GET /documentos-empresa/rango-fechas - Rango: {} a {}", fechaInicio, fechaFin);

        List<DocumentoEmpresaDTO> documentos = documentoService
                .obtenerDocumentosPorRangoFechas(fechaInicio, fechaFin);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos en el rango de fechas",
                        documentos.size()),
                documentos));
    }

    @GetMapping("/empresa/{empresaId}/rango-fechas")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosPorEmpresaYRangoFechas(
            @PathVariable UUID empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        log.info("GET /documentos-empresa/empresa/{}/rango-fechas - Rango: {} a {}",
                empresaId, fechaInicio, fechaFin);

        List<DocumentoEmpresaDTO> documentos = documentoService
                .obtenerDocumentosPorEmpresaYRangoFechas(empresaId, fechaInicio, fechaFin);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos", documentos.size()),
                documentos));
    }

    @GetMapping("/vencidos")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosVencidos() {
        log.info("GET /documentos-empresa/vencidos - Obteniendo documentos vencidos");

        List<DocumentoEmpresaDTO> documentos = documentoService.obtenerDocumentosVencidos();

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos vencidos", documentos.size()),
                documentos));
    }

    @GetMapping("/vencidos/{fecha}")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosVencidosHasta(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        log.info("GET /documentos-empresa/vencidos/{} - Obteniendo documentos vencidos hasta", fecha);

        List<DocumentoEmpresaDTO> documentos = documentoService.obtenerDocumentosVencidos(fecha);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos vencidos hasta %s",
                        documentos.size(), fecha),
                documentos));
    }

    @GetMapping("/por-vencer")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosPorVencer(
            @RequestParam(defaultValue = "30") int diasAnticipacion) {
        log.info("GET /documentos-empresa/por-vencer - Documentos por vencer en {} días",
                diasAnticipacion);

        List<DocumentoEmpresaDTO> documentos = documentoService
                .obtenerDocumentosPorVencer(diasAnticipacion);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos por vencer en %d días",
                        documentos.size(), diasAnticipacion),
                documentos));
    }

    @GetMapping("/por-vencer/rango")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosPorVencerEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        log.info("GET /documentos-empresa/por-vencer/rango - Rango: {} a {}", fechaInicio, fechaFin);

        List<DocumentoEmpresaDTO> documentos = documentoService
                .obtenerDocumentosPorVencer(fechaInicio, fechaFin);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos por vencer", documentos.size()),
                documentos));
    }

    @GetMapping("/empresa/{empresaId}/vencidos")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosVencidosPorEmpresa(
            @PathVariable UUID empresaId) {
        log.info("GET /documentos-empresa/empresa/{}/vencidos - Documentos vencidos por empresa",
                empresaId);

        List<DocumentoEmpresaDTO> documentos = documentoService
                .obtenerDocumentosVencidosPorEmpresa(empresaId);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos vencidos", documentos.size()),
                documentos));
    }

    @GetMapping("/empresa/{empresaId}/por-vencer")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> obtenerDocumentosPorVencerPorEmpresa(
            @PathVariable UUID empresaId,
            @RequestParam(defaultValue = "30") int diasAnticipacion) {
        log.info("GET /documentos-empresa/empresa/{}/por-vencer - Por vencer en {} días",
                empresaId, diasAnticipacion);

        List<DocumentoEmpresaDTO> documentos = documentoService
                .obtenerDocumentosPorVencerPorEmpresa(empresaId, diasAnticipacion);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos por vencer", documentos.size()),
                documentos));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> buscarDocumentosPorNombre(
            @RequestParam String nombreDocumento) {
        log.info("GET /documentos-empresa/buscar - Buscando por nombre: {}", nombreDocumento);

        List<DocumentoEmpresaDTO> documentos = documentoService
                .buscarDocumentosPorNombre(nombreDocumento);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos", documentos.size()),
                documentos));
    }

    @GetMapping("/empresa/{empresaId}/buscar")
    public ResponseEntity<ApiResponse<List<DocumentoEmpresaDTO>>> buscarDocumentosPorNombreYEmpresa(
            @PathVariable UUID empresaId,
            @RequestParam String nombreDocumento) {
        log.info("GET /documentos-empresa/empresa/{}/buscar - Buscando por nombre: {}",
                empresaId, nombreDocumento);

        List<DocumentoEmpresaDTO> documentos = documentoService
                .buscarDocumentosPorNombreYEmpresa(empresaId, nombreDocumento);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d documentos", documentos.size()),
                documentos));
    }

    @DeleteMapping("/{documentoId}")
    public ResponseEntity<ApiResponse<Void>> eliminarDocumento(@PathVariable UUID documentoId) {
        log.info("DELETE /documentos-empresa/{} - Eliminando documento", documentoId);

        documentoService.eliminarDocumento(documentoId);

        return ResponseEntity.ok(ApiResponse.success("Documento eliminado exitosamente", null));
    }

    // Endpoints de estadísticas
    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerEstadisticasDocumentos() {
        log.info("GET /documentos-empresa/estadisticas - Obteniendo estadísticas generales");

        Map<String, Object> estadisticas = documentoService.obtenerEstadisticasDocumentos();

        return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas", estadisticas));
    }

    @GetMapping("/empresa/{empresaId}/estadisticas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerEstadisticasDocumentosPorEmpresa(
            @PathVariable UUID empresaId) {
        log.info("GET /documentos-empresa/empresa/{}/estadisticas - Estadísticas por empresa",
                empresaId);

        Map<String, Object> estadisticas = documentoService
                .obtenerEstadisticasDocumentosPorEmpresa(empresaId);

        return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas", estadisticas));
    }

    @GetMapping("/resumen-vencimientos")
    public ResponseEntity<ApiResponse<Map<String, Long>>> obtenerResumenVencimientos() {
        log.info("GET /documentos-empresa/resumen-vencimientos - Obteniendo resumen");

        Map<String, Long> resumen = documentoService.obtenerResumenVencimientos();

        return ResponseEntity.ok(ApiResponse.success("Resumen de vencimientos obtenido", resumen));
    }

    @GetMapping("/empresa/{empresaId}/resumen-vencimientos")
    public ResponseEntity<ApiResponse<Map<String, Long>>> obtenerResumenVencimientosPorEmpresa(
            @PathVariable UUID empresaId) {
        log.info("GET /documentos-empresa/empresa/{}/resumen-vencimientos - Resumen por empresa",
                empresaId);

        Map<String, Long> resumen = documentoService.obtenerResumenVencimientosPorEmpresa(empresaId);

        return ResponseEntity.ok(ApiResponse.success("Resumen de vencimientos obtenido", resumen));
    }
}
