package com.peraltapos.taxpayer;

import com.peraltapos.crm.customer.CustomerFiscalProfile;

import java.time.OffsetDateTime;

public record DgiiRncResponse(
        String rnc,
        String razonSocial,
        String name,
        String nombreComercial,
        String categoria,
        String regimenPago,
        String estado,
        String actividadEconomica,
        String fechaConstitucion,
        String administracionLocal,
        String source,
        OffsetDateTime actualizadoEn,
        CustomerFiscalProfile fiscalProfile,
        boolean verified
) {
    public static DgiiRncResponse from(DgiiRnc record, CustomerFiscalProfile fiscalProfile) {
        return new DgiiRncResponse(
                record.getRnc(),
                record.getRazonSocial(),
                record.getRazonSocial(),
                record.getNombreComercial(),
                record.getCategoria(),
                record.getRegimenPago(),
                record.getEstado(),
                record.getActividadEconomica(),
                record.getFechaConstitucion(),
                record.getAdministracionLocal(),
                record.getFuente(),
                record.getActualizadoEn(),
                fiscalProfile,
                true
        );
    }
}
