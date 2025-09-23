package com.dataservices.ssoma.gestion_empresas.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "EMPRESAS")
@Data
public class Empresa {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "empresa_id", updatable = false, nullable = false)
    private UUID empresaId;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "razon_social", length = 255)
    private String razonSocial;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "sector", length = 100)
    private String sector;

    @Column(name = "score_seguridad")
    private Integer scoreSeguridad;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<DocumentoEmpresa> documentos;
}
