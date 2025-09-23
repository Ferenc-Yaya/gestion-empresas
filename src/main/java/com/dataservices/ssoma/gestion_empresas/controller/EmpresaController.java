package com.dataservices.ssoma.gestion_empresas.controller;

import com.dataservices.ssoma.gestion_empresas.dto.EmpresaDTO;
import com.dataservices.ssoma.gestion_empresas.dto.response.ApiResponse;
import com.dataservices.ssoma.gestion_empresas.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmpresaDTO>> crearEmpresa(@Valid @RequestBody EmpresaDTO empresaDTO) {
        log.info("POST /empresas - Creando nueva empresa: {}", empresaDTO.getRazonSocial());

        EmpresaDTO nuevaEmpresa = empresaService.crearEmpresa(empresaDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Empresa creada exitosamente", nuevaEmpresa));
    }

    @PutMapping("/{empresaId}")
    public ResponseEntity<ApiResponse<EmpresaDTO>> actualizarEmpresa(
            @PathVariable UUID empresaId,
            @Valid @RequestBody EmpresaDTO empresaDTO) {
        log.info("PUT /empresas/{} - Actualizando empresa", empresaId);

        EmpresaDTO empresaActualizada = empresaService.actualizarEmpresa(empresaId, empresaDTO);

        return ResponseEntity.ok(ApiResponse.success("Empresa actualizada exitosamente", empresaActualizada));
    }

    @GetMapping("/{empresaId}")
    public ResponseEntity<ApiResponse<EmpresaDTO>> obtenerEmpresaPorId(@PathVariable UUID empresaId) {
        log.info("GET /empresas/{} - Obteniendo empresa por ID", empresaId);

        EmpresaDTO empresa = empresaService.obtenerEmpresaPorId(empresaId);

        return ResponseEntity.ok(ApiResponse.success("Empresa encontrada", empresa));
    }

    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<ApiResponse<EmpresaDTO>> obtenerEmpresaPorRuc(@PathVariable String ruc) {
        log.info("GET /empresas/ruc/{} - Obteniendo empresa por RUC", ruc);

        EmpresaDTO empresa = empresaService.obtenerEmpresaPorRuc(ruc);

        return ResponseEntity.ok(ApiResponse.success("Empresa encontrada", empresa));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpresaDTO>>> obtenerTodasLasEmpresas() {
        log.info("GET /empresas - Obteniendo todas las empresas");

        List<EmpresaDTO> empresas = empresaService.obtenerTodasLasEmpresas();

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d empresas", empresas.size()), empresas));
    }

    @GetMapping("/paginado")
    public ResponseEntity<ApiResponse<Page<EmpresaDTO>>> obtenerEmpresasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "razonSocial") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("GET /empresas/paginado - Página: {}, Tamaño: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmpresaDTO> empresasPage = empresaService.obtenerEmpresasPaginadas(pageable);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Página %d de %d - Total: %d empresas",
                        page + 1, empresasPage.getTotalPages(), empresasPage.getTotalElements()),
                empresasPage));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<Page<EmpresaDTO>>> buscarEmpresasPorRazonSocial(
            @RequestParam String razonSocial,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "razonSocial") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("GET /empresas/buscar - Buscando por razón social: {}", razonSocial);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmpresaDTO> empresasPage = empresaService.buscarEmpresasPorRazonSocial(razonSocial, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d empresas", empresasPage.getTotalElements()),
                empresasPage));
    }

    @GetMapping("/sector/{sector}")
    public ResponseEntity<ApiResponse<List<EmpresaDTO>>> obtenerEmpresasPorSector(
            @PathVariable String sector) {
        log.info("GET /empresas/sector/{} - Obteniendo empresas por sector", sector);

        List<EmpresaDTO> empresas = empresaService.obtenerEmpresasPorSector(sector);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d empresas del sector %s", empresas.size(), sector),
                empresas));
    }

    @GetMapping("/sector/{sector}/paginado")
    public ResponseEntity<ApiResponse<Page<EmpresaDTO>>> obtenerEmpresasPorSectorPaginado(
            @PathVariable String sector,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "razonSocial") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("GET /empresas/sector/{}/paginado - Página: {}, Tamaño: {}", sector, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmpresaDTO> empresasPage = empresaService.obtenerEmpresasPorSector(sector, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Página %d de %d - Total: %d empresas del sector %s",
                        page + 1, empresasPage.getTotalPages(),
                        empresasPage.getTotalElements(), sector),
                empresasPage));
    }

    @GetMapping("/score")
    public ResponseEntity<ApiResponse<List<EmpresaDTO>>> obtenerEmpresasPorRangoScore(
            @RequestParam Integer scoreMin,
            @RequestParam Integer scoreMax) {
        log.info("GET /empresas/score - Obteniendo empresas por rango de score: {} - {}",
                scoreMin, scoreMax);

        List<EmpresaDTO> empresas = empresaService.obtenerEmpresasPorRangoScore(scoreMin, scoreMax);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d empresas con score entre %d y %d",
                        empresas.size(), scoreMin, scoreMax),
                empresas));
    }

    @GetMapping("/filtros")
    public ResponseEntity<ApiResponse<Page<EmpresaDTO>>> buscarEmpresasConFiltros(
            @RequestParam(required = false) String razonSocial,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) Integer scoreMin,
            @RequestParam(required = false) Integer scoreMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "razonSocial") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("GET /empresas/filtros - Buscando con filtros múltiples");

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmpresaDTO> empresasPage = empresaService.buscarEmpresasConFiltros(
                razonSocial, sector, scoreMin, scoreMax, pageable);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Se encontraron %d empresas", empresasPage.getTotalElements()),
                empresasPage));
    }

    @DeleteMapping("/{empresaId}")
    public ResponseEntity<ApiResponse<Void>> eliminarEmpresa(@PathVariable UUID empresaId) {
        log.info("DELETE /empresas/{} - Eliminando empresa", empresaId);

        empresaService.eliminarEmpresa(empresaId);

        return ResponseEntity.ok(ApiResponse.success("Empresa eliminada exitosamente", null));
    }

    // Endpoints de estadísticas
    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerEstadisticasGenerales() {
        log.info("GET /empresas/estadisticas - Obteniendo estadísticas generales");

        Map<String, Object> estadisticas = empresaService.obtenerEstadisticasGenerales();

        return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas", estadisticas));
    }

    @GetMapping("/sectores")
    public ResponseEntity<ApiResponse<List<String>>> obtenerSectoresDisponibles() {
        log.info("GET /empresas/sectores - Obteniendo sectores disponibles");

        List<String> sectores = empresaService.obtenerSectoresDisponibles();

        return ResponseEntity.ok(ApiResponse.success("Sectores obtenidos", sectores));
    }

    @GetMapping("/distribucion-sectores")
    public ResponseEntity<ApiResponse<Map<String, Long>>> obtenerDistribucionPorSector() {
        log.info("GET /empresas/distribucion-sectores - Obteniendo distribución por sector");

        Map<String, Long> distribucion = empresaService.obtenerDistribucionPorSector();

        return ResponseEntity.ok(ApiResponse.success("Distribución obtenida", distribucion));
    }

    @GetMapping("/top-score")
    public ResponseEntity<ApiResponse<List<EmpresaDTO>>> obtenerTop10EmpresasPorScore() {
        log.info("GET /empresas/top-score - Obteniendo top 10 empresas por score");

        List<EmpresaDTO> empresas = empresaService.obtenerTop10EmpresasPorScore();

        return ResponseEntity.ok(ApiResponse.success("Top 10 empresas obtenidas", empresas));
    }

    @GetMapping("/score-bajo")
    public ResponseEntity<ApiResponse<List<EmpresaDTO>>> obtenerEmpresasConScoreBajo(
            @RequestParam(defaultValue = "50") Integer scoreMinimo) {
        log.info("GET /empresas/score-bajo - Obteniendo empresas con score bajo");

        List<EmpresaDTO> empresas = empresaService.obtenerEmpresasConScoreBajo(scoreMinimo);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("Empresas con score menor a %d", scoreMinimo), empresas));
    }

    @GetMapping("/score-promedio")
    public ResponseEntity<ApiResponse<Double>> obtenerScorePromedioSeguridad() {
        log.info("GET /empresas/score-promedio - Obteniendo score promedio");

        Double scorePromedio = empresaService.obtenerScorePromedioSeguridad();

        return ResponseEntity.ok(ApiResponse.success("Score promedio obtenido", scorePromedio));
    }

    @GetMapping("/validar-ruc/{ruc}")
    public ResponseEntity<ApiResponse<Boolean>> validarRuc(@PathVariable String ruc) {
        log.info("GET /empresas/validar-ruc/{} - Validando RUC", ruc);

        boolean existe = empresaService.existeRuc(ruc);

        return ResponseEntity.ok(ApiResponse.success(
                existe ? "El RUC ya existe" : "El RUC está disponible",
                !existe));
    }

    @GetMapping("/{empresaId}/documentos/count")
    public ResponseEntity<ApiResponse<Long>> contarDocumentosPorEmpresa(@PathVariable UUID empresaId) {
        log.info("GET /empresas/{}/documentos/count - Contando documentos", empresaId);

        Long totalDocumentos = empresaService.contarDocumentosPorEmpresa(empresaId);

        return ResponseEntity.ok(ApiResponse.success("Total de documentos obtenido", totalDocumentos));
    }
}
