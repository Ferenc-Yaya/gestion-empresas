package com.dataservices.ssoma.gestion_empresas.controller;

import com.dataservices.ssoma.gestion_empresas.dto.response.ApiResponse;
import com.dataservices.ssoma.gestion_empresas.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/archivos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        log.info("POST /archivos/upload - Subiendo archivo: {}", file.getOriginalFilename());

        try {
            // Validaciones básicas
            if (file.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("El archivo está vacío"));
            }

            // Validar tamaño (máximo 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("El archivo es muy grande. Máximo 10MB"));
            }

            // Guardar archivo
            String fileName = fileStorageService.storeFile(file);

            // Preparar respuesta
            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("originalName", file.getOriginalFilename());
            response.put("downloadUrl", "/api/v1/archivos/download/" + fileName);
            response.put("size", String.valueOf(file.getSize()));

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Archivo subido exitosamente", response));

        } catch (Exception ex) {
            log.error("Error subiendo archivo: {}", ex.getMessage(), ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error subiendo archivo: " + ex.getMessage()));
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        log.info("GET /archivos/download/{} - Descargando archivo", fileName);

        try {
            Path filePath = fileStorageService.loadFileAsPath(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            log.error("Error descargando archivo {}: {}", fileName, ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{fileName:.+}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String fileName) {
        log.info("DELETE /archivos/delete/{} - Eliminando archivo", fileName);

        try {
            boolean deleted = fileStorageService.deleteFile(fileName);

            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Archivo eliminado exitosamente", null));
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Archivo no encontrado"));
            }
        } catch (Exception ex) {
            log.error("Error eliminando archivo {}: {}", fileName, ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error eliminando archivo"));
        }
    }
}
