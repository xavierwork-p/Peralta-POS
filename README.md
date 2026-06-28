# Peralta POS

Sistema empresarial de punto de venta, inventario, facturacion, cotizaciones, empleados, permisos y contabilidad para una ferreteria dominicana ficticia.

El proyecto esta pensado como un sistema comercial adaptable para escritorio y web:

- empleados trabajando desde una computadora local;
- duenos o directivos entrando desde web/movil;
- backend centralizado en Java;
- base de datos MySQL;
- modulos preparados para fiscalidad dominicana.

> Nota: Peralta POS es un proyecto de portafolio/desarrollo. La facturacion electronica DGII esta preparada en modo tecnico/simulado; para uso legal real requiere autorizacion, certificacion y certificado digital vigente.

---

## Funciones principales

### Punto de venta

- Registro de ventas.
- Pagos en efectivo, tarjeta, transferencia o credito.
- Descuento automatico de inventario.
- Impresion de factura.
- Factura con datos configurables de la empresa.
- NCF asignado automaticamente segun el tipo fiscal del cliente.

### Inventario

- Catalogo de productos.
- Stock actual y stock minimo.
- Movimientos de inventario.
- Reposicion.
- Conteos fisicos.
- Historial de entradas, salidas, ajustes y ventas.
- Reportes por modulo.

### Clientes y suplidores

- Registro de clientes.
- Perfil fiscal de clientes.
- RNC/Cedula.
- Limite de credito.
- Suplidores con RNC, telefono, correo y direccion.

### Cotizaciones

- Creacion de cotizaciones.
- Estados: borrador, enviada, aprobada, convertida, cancelada.
- Exportacion/impresion.
- Boton para facturar cotizaciones aprobadas.
- Conversion a factura con NCF.

### Facturacion RD

- Secuencias NCF.
- Tipos fiscales:
  - consumo;
  - credito fiscal;
  - gubernamental;
  - regimen especial;
  - notas de credito/debito preparadas.
- Flujo e-CF simulado:
  - genera XML de prueba;
  - genera hash;
  - agrega firma simulada;
  - guarda XML firmado;
  - simula envio DGII;
  - guarda TrackId, codigo de seguridad y acuse simulado.

### Contabilidad

Modulo contable operativo, inspirado en un flujo tipo ERP:

- catalogo de cuentas editable;
- diarios contables editables;
- cuentas por cobrar;
- cuentas por pagar;
- pagos/cobros parciales o totales;
- conciliacion de pagos;
- asientos automaticos desde ventas;
- asientos automaticos desde compras;
- asientos automaticos desde cobros/pagos;
- asientos manuales;
- balance de comprobacion;
- resumen de ingresos, gastos, resultado, activos, pasivos y capital.

### Empleados y permisos

- Creacion de empleados.
- Usuarios de acceso.
- Permisos por modulo.
- Acceso de lectura o escritura.
- Permiso para entrar por web/movil.
- Control para que cada empleado vea solo los modulos autorizados.

### Configuracion de empresa

- Nombre de empresa.
- Nombre comercial.
- RNC.
- Telefono.
- Correo.
- Direccion.
- Logo URL.
- Moneda.
- ITBIS base.

---

## Stack tecnico

### Backend

- Java 21.
- Spring Boot 3.
- Spring Security.
- Spring Data JPA.
- Flyway.
- MySQL.
- JasperReports.
- Apache POI.

### Frontend

- React.
- TypeScript.
- Vite.
- Lucide Icons.

### Escritorio

- Preparado para Tauri.

### Base de datos

- MySQL local o remoto.
- Migraciones Flyway.
- Datos demo iniciales.

---

## Estructura del proyecto

```text
apps/
  web/              Interfaz web React + Vite
  desktop/          App local Tauri

services/
  api/              Backend Java Spring Boot

database/
  mysql/            Scripts base de MySQL

docs/
  contexto-codex-peralta-pos.txt   Bitacora del desarrollo
  arquitectura.md                  Vision tecnica
  modulos.md                       Modulos del sistema
  fiscal-rd.md                     Alcance fiscal dominicano
  despliegue-web-y-escritorio.md   Guia para Vercel, Railway y escritorio
  roadmap.md                       Plan de trabajo

scripts/
  dev-web.ps1       Arranque de web con backend automatico
  build-web.ps1     Build web
  build-desktop-web.ps1 Build web con canal DESKTOP para Tauri
  create-web-launcher.ps1 Crea lanzador rapido hacia la web publicada
  dev-api-tools.ps1 Herramientas para prender/apagar backend
```

---

## Requisitos

- Node.js.
- npm.
- Java 21 o superior.
- Maven.
- MySQL.
- Docker Desktop opcional para levantar MySQL con `docker compose`.

En este entorno se ha usado Maven desde NetBeans:

```powershell
C:\Program Files\NetBeans-24\netbeans\java\maven\bin\mvn.cmd
```

---

## Instalacion

Desde la carpeta raiz del proyecto:

```powershell
npm run install:web
npm run install:desktop
```

Si vas a usar MySQL con Docker:

```powershell
npm run db:up
```

Si ya tienes MySQL instalado, configura estas variables si necesitas cambiar los valores por defecto:

```text
DB_URL=jdbc:mysql://localhost:3306/peralta_pos?createDatabaseIfNotExist=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
DB_USER=peralta
DB_PASSWORD=peralta123
```

---

## Ejecutar en desarrollo

### Web con backend automatico

```powershell
npm run dev
```

Tambien puedes usar:

```powershell
npm run dev:web
```

Ese comando:

1. revisa si el backend esta corriendo;
2. si no esta corriendo, lo inicia;
3. levanta la web en Vite.

URL principal:

```text
http://127.0.0.1:5173
```

Backend:

```text
http://127.0.0.1:8080/api
```

Health check:

```text
http://127.0.0.1:8080/api/health
```

### Solo backend

```powershell
npm run dev:api
```

O manualmente:

```powershell
mvn -f services/api/pom.xml spring-boot:run
```

### App local

```powershell
npm run dev:desktop
```

---

## Usuarios demo

En la pantalla de login hay un bloque discreto llamado **Accesos de prueba**.

Usuarios disponibles:

```text
ana.peralta   / Peralta123!   Administradora
luis.mateo    / Peralta123!   Caja y ventas
rosa.jimenez  / Peralta123!   Almacen
```

La clave es temporal para desarrollo.

---

## Build

### Build web

```powershell
npm run build:web
```

Si estas dentro de `apps/web`, tambien puedes usar:

```powershell
npm exec -- tsc --noEmit
npm exec -- vite build --configLoader runner
```

### Build backend

```powershell
mvn -q -f services/api/pom.xml -DskipTests package
```

En este entorno:

```powershell
& 'C:\Program Files\NetBeans-24\netbeans\java\maven\bin\mvn.cmd' -q -f services/api/pom.xml -DskipTests package
```

---

## Despliegue web y escritorio

La ruta recomendada para publicar el sistema gratis como demo es:

```text
GitHub -> Aiven MySQL -> Render backend -> Vercel frontend -> Windows launcher/instalador
```

Archivos ya preparados:

- `Dockerfile`
- `render.yaml`
- `railway.json`
- `nixpacks.toml`
- `apps/web/vercel.json`
- `apps/web/.env.production.example`
- `apps/web/.env.desktop.example`
- `services/api/src/main/resources/application-render.yml`
- `services/api/src/main/resources/application-railway.yml`

Guia recomendada gratis:

```text
docs/despliegue-gratis-vercel-render-aiven.md
```

Guia alternativa con Railway:

```text
docs/despliegue-web-y-escritorio.md
```

Variables principales:

```text
Render backend:
SPRING_PROFILES_ACTIVE=render
JWT_SECRET=poner-una-clave-larga-y-segura
CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app,tauri://localhost
DGII_RNC_AUTO_SYNC_ENABLED=false
DB_URL=jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USER=usuario-de-aiven
DB_PASSWORD=password-de-aiven

Vercel frontend:
VITE_API_BASE_URL=https://tu-backend.onrender.com/api
VITE_CLIENT_CHANNEL=WEB
```

Crear un lanzador rapido para Windows:

```powershell
npm run create:web-launcher -- -Url https://tu-frontend.vercel.app
```

Compilar instalador de escritorio:

```powershell
npm run build:desktop
```

---

## Facturacion electronica DGII

El sistema ya tiene una estructura tecnica preparada:

- XML e-CF simulado.
- Firma simulada.
- TrackId simulado.
- Codigo de seguridad simulado.
- Acuse simulado guardado.

Para enviar facturas reales a DGII se necesita:

1. empresa autorizada como emisor electronico;
2. certificado digital tributario valido;
3. XML e-CF con estructura oficial vigente;
4. firma XML real;
5. ambiente de certificacion/homologacion DGII;
6. endpoints y credenciales oficiales;
7. guardar los acuses reales devueltos por DGII.

La clase simulada debe reemplazarse por un conector real de DGII cuando se tenga la autorizacion oficial.

---

## Comandos utiles

### Revisar frontend

```powershell
cd apps/web
npm exec -- tsc --noEmit
npm exec -- vite build --configLoader runner
```

### Revisar backend

```powershell
& 'C:\Program Files\NetBeans-24\netbeans\java\maven\bin\mvn.cmd' -q -f services/api/pom.xml -DskipTests package
```

### Verificar que no haya backend viejo en el puerto 8080

```powershell
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
```

---

## Subir a GitHub

Si todavia no esta inicializado como repositorio Git:

```powershell
git init
git add .
git commit -m "Actualizar Peralta POS"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/peralta-pos.git
git push -u origin main
```

Si ya existe el remoto:

```powershell
git add .
git commit -m "Actualizar Peralta POS"
git push
```

Cambia `TU_USUARIO` por tu usuario real de GitHub.

---

## Estado actual

Implementado:

- Dashboard.
- POS.
- Ventas.
- Cotizaciones.
- Clientes.
- Productos.
- Inventario.
- Reposicion.
- Compras.
- Movimientos.
- Conteos.
- Suplidores.
- Empleados.
- Usuarios y permisos.
- Configuracion de empresa.
- Facturacion RD con NCF y e-CF simulado.
- Contabilidad operativa.
- Reportes PDF/Excel/Word por modulos principales.

Pendiente o siguiente fase:

- conector real DGII;
- certificado digital real;
- mayor general mas detallado;
- estado de resultados formal;
- balance general formal;
- empaquetado final escritorio/web;
- modulo externo de licencias/limites de usuarios.

---

## Licencia

Proyecto privado/de portafolio. Ajustar esta seccion si se publica como codigo abierto.
