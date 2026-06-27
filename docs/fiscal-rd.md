# Alcance fiscal para Republica Dominicana

Este documento define como se manejara la parte fiscal en el proyecto academico.

## Lo que se implementara

- Registro de RNC o cedula del cliente.
- Tipos de comprobantes usados en el flujo comercial.
- Secuencias NCF configurables.
- Facturas de consumo.
- Facturas con valor fiscal.
- Notas de credito.
- Notas de debito.
- Fecha de vencimiento de secuencias.
- Validaciones basicas para evitar emitir documentos incompletos.
- Reportes exportables inspirados en 606, 607 y 608.
- Facturacion electronica en modo demo tecnico.

## Lo que queda como demo

La facturacion electronica real requiere:

- Autorizacion formal como emisor electronico.
- Certificado digital.
- Pruebas con DGII.
- Firma digital del comprobante.
- Comunicacion con servicios oficiales.
- Manejo real de respuestas, rechazos y contingencias.

Por eso el proyecto mostrara una arquitectura preparada para e-CF, pero no dira que emite facturas electronicas legales en produccion.

## Tipos internos sugeridos

```text
CONSUMO
CREDITO_FISCAL
NOTA_CREDITO
NOTA_DEBITO
COMPRAS
GASTOS_MENORES
REGIMEN_ESPECIAL
GUBERNAMENTAL
EXPORTACION
PAGOS_EXTERIOR
```

## Reglas iniciales

- Una venta fiscal debe tener una secuencia NCF activa.
- Una factura con valor fiscal debe tener cliente con RNC o cedula.
- Una nota de credito debe referenciar una factura previa.
- Una secuencia vencida no debe usarse.
- Un NCF emitido no debe repetirse.
