# Guia de desarrollo

## Orden recomendado para trabajar

1. Encender MySQL local o conectar una base MySQL remota.
2. Encender el backend Java.
3. Encender la web.
4. Encender la app local solo cuando la web ya se vea bien.

Si quieres usar Docker como alternativa:

```bash
npm run db:up
```

Si quieres usar tu MySQL instalado, ejecuta primero `database/mysql/create-database.sql` en MySQL Workbench.

## Backend

El backend sigue esta organizacion:

```text
controller -> service -> repository -> database
```

Cuando agreguemos un modulo nuevo, se recomienda crear:

- Entity
- Request
- Response
- Repository
- Service
- Controller

Ejemplo:

```text
sales/invoice/Invoice.java
sales/invoice/InvoiceRequest.java
sales/invoice/InvoiceResponse.java
sales/invoice/InvoiceRepository.java
sales/invoice/InvoiceService.java
sales/invoice/InvoiceController.java
```

## Frontend

La interfaz esta en:

```text
apps/web/src
```

La app local no debe duplicar pantallas. Tauri solo empaqueta lo que vive en `apps/web`.

## Base de datos

Las migraciones se guardan en:

```text
services/api/src/main/resources/db/migration
```

Toda modificacion de tablas debe hacerse con una nueva migracion:

```text
V2__nombre_del_cambio.sql
V3__otro_cambio.sql
```

No conviene editar `V1__initial_schema.sql` cuando el proyecto ya este corriendo en una base real.

## Comentarios

Los comentarios se usan para:

- Reglas de negocio.
- Decisiones fiscales.
- Puntos que luego se reemplazaran por una version mas robusta.
- Integraciones externas futuras.

No se comentan lineas obvias como asignaciones simples, para que el codigo siga siendo legible.
