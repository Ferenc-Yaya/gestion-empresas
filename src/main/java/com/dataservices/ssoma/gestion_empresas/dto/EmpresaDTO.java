package com.dataservices.ssoma.gestion_empresas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class EmpresaDTO {

    @JsonProperty("empresa_id")
    private UUID empresaId;

    @JsonProperty("ruc")
    @Size(max = 20, message = "El RUC no puede exceder 20 caracteres")
    private String ruc;

    @JsonProperty("razon_social")
    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 255, message = "La razón social no puede exceder 255 caracteres")
    private String razonSocial;

    @JsonProperty("direccion")
    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String direccion;

    @JsonProperty("sector")
    @Size(max = 100, message = "El sector no puede exceder 100 caracteres")
    private String sector;

    @JsonProperty("score_seguridad")
    @Min(value = 0, message = "El score de seguridad debe ser mayor o igual a 0")
    @Max(value = 100, message = "El score de seguridad debe ser menor o igual a 100")
    private Integer scoreSeguridad;

    @JsonProperty("documentos")
    private List<DocumentoEmpresaDTO> documentos;
}
