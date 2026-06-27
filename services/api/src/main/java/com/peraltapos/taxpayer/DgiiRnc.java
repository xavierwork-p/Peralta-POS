package com.peraltapos.taxpayer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "dgii_rnc")
public class DgiiRnc {

    @Id
    @Column(length = 20)
    private String rnc;

    @Column(name = "razon_social", nullable = false, length = 220)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 220)
    private String nombreComercial;

    @Column(length = 120)
    private String categoria;

    @Column(name = "regimen_pago", length = 120)
    private String regimenPago;

    @Column(length = 80)
    private String estado;

    @Column(name = "actividad_economica", length = 300)
    private String actividadEconomica;

    @Column(name = "fecha_constitucion", length = 40)
    private String fechaConstitucion;

    @Column(name = "administracion_local", length = 160)
    private String administracionLocal;

    @Column(nullable = false, length = 40)
    private String fuente = "DGII";

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    public static DgiiRnc fromImport(DgiiRncImportRow row) {
        DgiiRnc record = new DgiiRnc();
        record.rnc = row.rnc();
        record.updateFrom(row);
        return record;
    }

    public void updateFrom(DgiiRncImportRow row) {
        razonSocial = row.razonSocial();
        nombreComercial = row.nombreComercial();
        categoria = row.categoria();
        regimenPago = row.regimenPago();
        estado = row.estado();
        actividadEconomica = row.actividadEconomica();
        fechaConstitucion = row.fechaConstitucion();
        administracionLocal = row.administracionLocal();
        fuente = "DGII";
        actualizadoEn = OffsetDateTime.now();
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (creadoEn == null) {
            creadoEn = now;
        }
        if (actualizadoEn == null) {
            actualizadoEn = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }

    public String getRnc() {
        return rnc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getRegimenPago() {
        return regimenPago;
    }

    public String getEstado() {
        return estado;
    }

    public String getActividadEconomica() {
        return actividadEconomica;
    }

    public String getFechaConstitucion() {
        return fechaConstitucion;
    }

    public String getAdministracionLocal() {
        return administracionLocal;
    }

    public String getFuente() {
        return fuente;
    }

    public OffsetDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}
