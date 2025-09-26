package com.dataservices.ssoma.gestion_empresas.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);
            log.info("Directorio de archivos creado en: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio para almacenar archivos.", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        // Validar archivo
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        // Obtener información del archivo
        String originalFileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFileName);

        // Crear nombre único para el archivo
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String fileName = timestamp + "_" + uniqueId + "." + extension;

        try {
            // Verificar que el nombre no contiene caracteres peligrosos
            if (fileName.contains("..")) {
                throw new RuntimeException("El nombre del archivo contiene secuencia de ruta inválida: " + fileName);
            }

            // Copiar archivo al directorio de destino
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Archivo almacenado exitosamente: {} -> {}", originalFileName, fileName);
            return fileName;

        } catch (IOException ex) {
            log.error("Error almacenando archivo {}: {}", fileName, ex.getMessage());
            throw new RuntimeException("No se pudo almacenar el archivo " + fileName, ex);
        }
    }

    public Path loadFileAsPath(String fileName) {
        return fileStorageLocation.resolve(fileName).normalize();
    }

    public boolean deleteFile(String fileName) {
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("Error eliminando archivo {}: {}", fileName, ex.getMessage());
            return false;
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}
