package com.dataservices.ssoma.gestion_empresas.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "DOCUMENTOS_EMPRESA")
@Data
public class DocumentoEmpresa {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "documento_empresa_id", updatable = false, nullable = false)
    private UUID documentoEmpresaId;

    @Column(name = "empresa_id", nullable = false, insertable = false, updatable = false)
    private UUID empresaId;

    @Column(name = "nombre_documento", length = 255)
    private String nombreDocumento;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "documento_url", length = 500)
    private String documentoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Empresa empresa;
}
