# Arquitectura de Peralta POS

## Vision general

Peralta POS se divide en tres piezas principales:

```text
Usuario web
   |
   v
React/Vite en navegador
   |
   v
API Java Spring Boot
   |
   v
MySQL local o remoto

Usuario escritorio
   |
   v
Tauri + misma interfaz React/Vite
   |
   v
API Java Spring Boot
   |
   v
MySQL local o remoto
```

La aplicacion local no trabaja offline. Abre como programa instalado, pero usa internet para consultar y guardar informacion.

## Por que esta arquitectura

- **Java Spring Boot**: da una base profesional, mantenible y facil de defender ante un profesor o cliente.
- **MySQL**: facilita la revision academica porque es una base SQL clasica y puedes inspeccionarla con MySQL Workbench.
- **MySQL remoto opcional**: para portafolio web se puede usar una base hospedada sin cambiar el codigo de negocio.
- **React + Vite**: sirve tanto para web como para escritorio sin duplicar interfaz.
- **Tauri**: empaqueta la interfaz como app de escritorio liviana.

## Capas del backend

```text
controller -> service -> repository -> database
```

- **Controller**: recibe peticiones HTTP.
- **Service**: concentra reglas de negocio.
- **Repository**: comunica con la base de datos.
- **Entity**: representa tablas principales.
- **DTO**: objetos de entrada/salida para no exponer entidades completas.

## Modulos backend iniciales

- Empresa y sucursales
- Seguridad, usuarios y empleados
- Clientes
- Proveedores
- Productos e inventario
- Ventas y caja
- Cotizaciones
- Facturacion fiscal dominicana
- Contabilidad basica
- Reportes y exportaciones
- Auditoria

## Convenciones

- Rutas API bajo `/api`.
- Tablas en plural y `snake_case`.
- Identificadores UUID para datos empresariales importantes.
- Fechas con zona horaria cuando representen eventos reales.
- Montos monetarios con `numeric(14,2)`.
- Comentarios en codigo donde haya reglas de negocio o decisiones fiscales.
