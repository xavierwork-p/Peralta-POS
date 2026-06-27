package com.peraltapos.taxpayer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface DgiiRncRepository extends JpaRepository<DgiiRnc, String> {

    @Query("select max(record.actualizadoEn) from DgiiRnc record")
    Optional<OffsetDateTime> findLatestUpdate();
}
