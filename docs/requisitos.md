# Requisitos de desarrollo

## Necesarios

- Java 21 o superior.
- Maven.
- Node.js.
- npm.
- MySQL 8 o superior.
- MySQL Workbench recomendado.

## Opcionales

- Docker, si quieres levantar MySQL con `docker compose`.
- Rust y Cargo, si quieres compilar la app local con Tauri.

## Estado detectado en esta maquina

- Java: instalado.
- Node.js: instalado.
- npm: instalado.
- Maven: no detectado en PATH.
- Docker: no detectado en PATH.
- Rust/Cargo: no detectado en PATH.
- Cliente `mysql`: no detectado en PATH.

Aunque el cliente `mysql` no aparezca en PATH, puedes tener MySQL instalado y usarlo desde MySQL Workbench.

## MySQL local

Ejecuta este archivo en MySQL Workbench con un usuario administrador:

```text
database/mysql/create-database.sql
```

Despues inicia el backend; Flyway creara las tablas y cargara datos demo.
