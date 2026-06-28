# Despliegue web y escritorio de Peralta POS

Este documento deja preparado el camino recomendado:

```text
GitHub
  ├─ Railway: backend Java + MySQL
  ├─ Vercel: frontend web React
  └─ Windows: lanzador o instalador de escritorio
```

La idea es que los empleados puedan usar escritorio y los dueños/directivos puedan entrar por web o móvil, respetando los permisos de usuario.

---

## 1. Subir el proyecto a GitHub

Si Git marca el proyecto como `dubious ownership`, ejecutar una sola vez:

```powershell
git config --global --add safe.directory 'C:/Users/peral/OneDrive/Escritorio/Proyectos Portafolio/peralta-pos'
```

Luego:

```powershell
git add .
git commit -m "Preparar despliegue web y escritorio"
git branch -M main
git remote add origin https://github.com/xavierwork-p/Peralta-POS.git
git push -u origin main
```

Si el remoto ya existe:

```powershell
git add .
git commit -m "Preparar despliegue web y escritorio"
git push
```

---

## 2. Backend en Railway

Crear un proyecto en Railway y conectar el repositorio de GitHub.

Agregar dos servicios:

1. Un servicio MySQL.
2. Un servicio para el backend Java/Spring Boot usando este mismo repositorio.

El repositorio ya tiene:

- `railway.json`
- `nixpacks.toml`
- `services/api/src/main/resources/application-railway.yml`

Variables recomendadas en el servicio del backend:

```text
SPRING_PROFILES_ACTIVE=railway
JWT_SECRET=poner-una-clave-larga-y-segura
CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app,tauri://localhost
DGII_RNC_AUTO_SYNC_ENABLED=false
```

Railway MySQL entrega estas variables automáticamente:

```text
MYSQLHOST
MYSQLPORT
MYSQLUSER
MYSQLPASSWORD
MYSQLDATABASE
MYSQL_URL
```

El backend usa las primeras cinco para formar la conexión JDBC.

Cuando Railway termine el deploy, copiar la URL pública del backend.

Ejemplo:

```text
https://peralta-pos-api.up.railway.app
```

La API quedaría:

```text
https://peralta-pos-api.up.railway.app/api
```

Health check:

```text
https://peralta-pos-api.up.railway.app/api/health
```

---

## 3. Frontend en Vercel

Crear un proyecto en Vercel conectado al mismo repositorio de GitHub.

Configuración recomendada:

```text
Root Directory: apps/web
Framework: Vite
Build Command: npm run build
Output Directory: dist
Install Command: npm install
```

Variables en Vercel:

```text
VITE_API_BASE_URL=https://tu-backend.up.railway.app/api
VITE_CLIENT_CHANNEL=WEB
```

Cuando Vercel genere la URL final, volver a Railway y actualizar:

```text
CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app,tauri://localhost
```

Después redeployar/reiniciar el backend en Railway.

---

## 4. Lanzador rápido de Windows

Cuando ya exista la URL de Vercel, se puede crear un lanzador simple:

```powershell
npm run create:web-launcher -- -Url https://tu-frontend.vercel.app
```

Esto crea archivos en:

```text
output/desktop-launcher
```

Puedes enviar el `.url` o el `.cmd` para abrir el sistema desde Windows.

---

## 5. Instalador de escritorio con Tauri

Para un ejecutable más formal:

1. Copiar:

```text
apps/web/.env.desktop.example
```

como:

```text
apps/web/.env.desktop
```

2. Cambiar:

```text
VITE_API_BASE_URL=https://tu-backend.up.railway.app/api
VITE_CLIENT_CHANNEL=DESKTOP
```

3. Instalar dependencias:

```powershell
npm run install:web
npm run install:desktop
```

4. Compilar instalador:

```powershell
npm run build:desktop
```

El instalador queda dentro de:

```text
apps/desktop/src-tauri/target/release/bundle
```

Notas:

- La web usa `VITE_CLIENT_CHANNEL=WEB`.
- El instalador usa `VITE_CLIENT_CHANNEL=DESKTOP`.
- Eso permite diferenciar usuarios con permiso web y usuarios solo de computadora.

---

## 6. Orden recomendado

1. Subir todo a GitHub.
2. Crear MySQL en Railway.
3. Crear backend en Railway.
4. Revisar `/api/health`.
5. Crear frontend en Vercel.
6. Poner la URL de Vercel en `CORS_ALLOWED_ORIGINS`.
7. Probar login online.
8. Crear el lanzador o instalador de escritorio.
