package com.peraltapos.reporting;

import java.util.Locale;

enum InventoryReportSection {
    PRODUCTS("products", "productos"),
    REPLENISHMENT("replenishment", "ordenes-compra"),
    PURCHASES("purchases", "facturas-compra"),
    MOVEMENTS("movements", "movimientos-inventario"),
    COUNTS("counts", "conteos-fisicos"),
    SUPPLIERS("suppliers", "suplidores");

    private final String path;
    private final String filename;

    InventoryReportSection(String path, String filename) {
        this.path = path;
        this.filename = filename;
    }

    String filename() {
        return filename;
    }

    static InventoryReportSection fromPath(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        for (InventoryReportSection section : values()) {
            if (section.path.equals(normalized)) {
                return section;
            }
        }
        throw new IllegalArgumentException("La seccion de reporte solicitada no es valida");
    }
}
