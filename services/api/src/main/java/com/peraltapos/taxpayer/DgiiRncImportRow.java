package com.peraltapos.taxpayer;

public record DgiiRncImportRow(
        String rnc,
        String razonSocial,
        String nombreComercial,
        String categoria,
        String regimenPago,
        String estado,
        String actividadEconomica,
        String fechaConstitucion,
        String administracionLocal
) {
}
