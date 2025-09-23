package com.dataservices.ssoma.gestion_empresas.repository;

import com.dataservices.ssoma.gestion_empresas.entity.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    Optional<Empresa> findByRuc(String ruc);

    List<Empresa> findByRazonSocialContainingIgnoreCase(String razonSocial);

    Page<Empresa> findByRazonSocialContainingIgnoreCase(String razonSocial, Pageable pageable);

    List<Empresa> findBySector(String sector);

    Page<Empresa> findBySector(String sector, Pageable pageable);

    List<Empresa> findByScoreSeguridadBetween(Integer scoreMin, Integer scoreMax);

    @Query("SELECT e FROM Empresa e WHERE " +
            "(:razonSocial IS NULL OR LOWER(e.razonSocial) LIKE LOWER(CONCAT('%', :razonSocial, '%'))) AND " +
            "(:sector IS NULL OR e.sector = :sector) AND " +
            "(:scoreMin IS NULL OR e.scoreSeguridad >= :scoreMin) AND " +
            "(:scoreMax IS NULL OR e.scoreSeguridad <= :scoreMax)")
    Page<Empresa> findByFiltros(
            @Param("razonSocial") String razonSocial,
            @Param("sector") String sector,
            @Param("scoreMin") Integer scoreMin,
            @Param("scoreMax") Integer scoreMax,
            Pageable pageable);

    @Query("SELECT AVG(e.scoreSeguridad) FROM Empresa e WHERE e.scoreSeguridad IS NOT NULL")
    Double findAverageScoreSeguridad();

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.scoreSeguridad BETWEEN :scoreMin AND :scoreMax")
    Long countByScoreSeguridadBetween(@Param("scoreMin") Integer scoreMin, @Param("scoreMax") Integer scoreMax);

    @Query("SELECT DISTINCT e.sector FROM Empresa e WHERE e.sector IS NOT NULL ORDER BY e.sector")
    List<String> findDistinctSectores();

    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.sector = :sector")
    Long countBySector(@Param("sector") String sector);

    @Query("SELECT e FROM Empresa e ORDER BY e.scoreSeguridad DESC")
    List<Empresa> findTop10ByOrderByScoreSeguridadDesc(Pageable pageable);

    @Query("SELECT e FROM Empresa e WHERE e.scoreSeguridad < :scoreMinimo ORDER BY e.scoreSeguridad ASC")
    List<Empresa> findEmpresasConScoreBajo(@Param("scoreMinimo") Integer scoreMinimo);

    boolean existsByRuc(String ruc);

    boolean existsByRucAndEmpresaIdNot(String ruc, UUID empresaId);

    @Query("SELECT COUNT(de) FROM DocumentoEmpresa de WHERE de.empresaId = :empresaId")
    Long countDocumentosByEmpresaId(@Param("empresaId") UUID empresaId);
}
