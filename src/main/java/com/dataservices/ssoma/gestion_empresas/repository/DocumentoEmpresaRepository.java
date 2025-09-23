package com.dataservices.ssoma.gestion_empresas.repository;

import com.dataservices.ssoma.gestion_empresas.entity.DocumentoEmpresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentoEmpresaRepository extends JpaRepository<DocumentoEmpresa, UUID> {

    List<DocumentoEmpresa> findByEmpresaId(UUID empresaId);

    Page<DocumentoEmpresa> findByEmpresaId(UUID empresaId, Pageable pageable);

    List<DocumentoEmpresa> findByFechaVencimiento(LocalDate fechaVencimiento);

    List<DocumentoEmpresa> findByFechaVencimientoBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<DocumentoEmpresa> findByEmpresaIdAndFechaVencimientoBetween(
            UUID empresaId, LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT de FROM DocumentoEmpresa de WHERE de.fechaVencimiento <= :fecha")
    List<DocumentoEmpresa> findDocumentosVencidos(@Param("fecha") LocalDate fecha);

    @Query("SELECT de FROM DocumentoEmpresa de WHERE de.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin")
    List<DocumentoEmpresa> findDocumentosPorVencer(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT de FROM DocumentoEmpresa de WHERE de.empresaId = :empresaId AND " +
            "de.fechaVencimiento <= :fecha")
    List<DocumentoEmpresa> findDocumentosVencidosByEmpresa(
            @Param("empresaId") UUID empresaId,
            @Param("fecha") LocalDate fecha);

    @Query("SELECT de FROM DocumentoEmpresa de WHERE de.empresaId = :empresaId AND " +
            "de.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin")
    List<DocumentoEmpresa> findDocumentosPorVencerByEmpresa(
            @Param("empresaId") UUID empresaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT de FROM DocumentoEmpresa de WHERE " +
            "LOWER(de.nombreDocumento) LIKE LOWER(CONCAT('%', :nombreDocumento, '%'))")
    List<DocumentoEmpresa> findByNombreDocumentoContainingIgnoreCase(
            @Param("nombreDocumento") String nombreDocumento);

    @Query("SELECT de FROM DocumentoEmpresa de WHERE de.empresaId = :empresaId AND " +
            "LOWER(de.nombreDocumento) LIKE LOWER(CONCAT('%', :nombreDocumento, '%'))")
    List<DocumentoEmpresa> findByEmpresaIdAndNombreDocumentoContainingIgnoreCase(
            @Param("empresaId") UUID empresaId,
            @Param("nombreDocumento") String nombreDocumento);

    @Query("SELECT COUNT(de) FROM DocumentoEmpresa de WHERE de.empresaId = :empresaId")
    Long countByEmpresaId(@Param("empresaId") UUID empresaId);

    @Query("SELECT COUNT(de) FROM DocumentoEmpresa de WHERE de.fechaVencimiento <= :fecha")
    Long countDocumentosVencidos(@Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(de) FROM DocumentoEmpresa de WHERE " +
            "de.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin")
    Long countDocumentosPorVencer(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(de) FROM DocumentoEmpresa de WHERE de.empresaId = :empresaId AND " +
            "de.fechaVencimiento <= :fecha")
    Long countDocumentosVencidosByEmpresa(
            @Param("empresaId") UUID empresaId,
            @Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(de) FROM DocumentoEmpresa de WHERE de.empresaId = :empresaId AND " +
            "de.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin")
    Long countDocumentosPorVencerByEmpresa(
            @Param("empresaId") UUID empresaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
