package com.dataservices.ssoma.gestion_empresas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class DocumentoEmpresaDTO {

    @JsonProperty("documento_empresa_id")
    private UUID documentoEmpresaId;

    @JsonProperty("empresa_id")
    @NotNull(message = "El ID de la empresa es obligatorio")
    private UUID empresaId;

    @JsonProperty("nombre_documento")
    @NotBlank(message = "El nombre del documento es obligatorio")
    @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
    private String nombreDocumento;

    @JsonProperty("fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @JsonProperty("documento_url")
    @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
    private String documentoUrl;
}
