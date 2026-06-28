# Despliegue gratis para demo: Vercel + Render + Aiven

Esta es la ruta recomendada si se quiere compartir un link sin depender del trial de 30 dias de Railway.

```text
GitHub
  ├─ Aiven: MySQL gratis
  ├─ Render: backend Java/Spring Boot gratis con Docker
  └─ Vercel: frontend React gratis
```

Advertencia importante:

- Render gratis puede dormir el backend despues de unos minutos sin uso.
- Cuando alguien entra despues de estar dormido, puede tardar alrededor de 1 minuto en despertar.
- Aiven Free no tiene limite fijo de tiempo, pero puede apagar servicios inactivos despues de avisar.
- Esta ruta sirve para demo/portafolio. Para produccion real conviene un plan pago.

---

## 1. Crear MySQL gratis en Aiven

1. Entrar a Aiven.
2. Crear servicio nuevo.
3. Elegir `Aiven for MySQL`.
4. Elegir plan `Free`.
5. Esperar que el servicio quede `Running`.

En la pantalla del servicio busca la informacion de conexion:

```text
Host
Port
User
Password
Database
```

Normalmente la base inicial se llama:

```text
defaultdb
```

Guarda esos valores, porque se usaran en Render.

---

## 2. Crear backend en Render

El proyecto ya tiene:

- `Dockerfile`
- `render.yaml`
- `services/api/src/main/resources/application-render.yml`

En Render:

1. Click en `New`.
2. Elegir `Blueprint` si quieres usar `render.yaml`.
3. Conectar el repo:

```text
xavierwork-p/Peralta-POS
```

Si prefieres crear el servicio manual:

```text
Service type: Web Service
Runtime: Docker
Branch: main
Dockerfile Path: ./Dockerfile
Health Check Path: /api/health
Plan: Free
```

Variables en Render:

```text
SPRING_PROFILES_ACTIVE=render
JWT_SECRET=poner-una-clave-larga-y-segura
CORS_ALLOWED_ORIGINS=https://*.vercel.app,tauri://localhost
DGII_RNC_AUTO_SYNC_ENABLED=false
```

Variables de Aiven en Render:

```text
DB_USER=usuario-de-aiven
DB_PASSWORD=password-de-aiven
DB_URL=jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

Ejemplo:

```text
DB_URL=jdbc:mysql://mysql-demo-xavier.a.aivencloud.com:28123/defaultdb?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

Cuando Render termine, copiamos la URL publica del backend.

Ejemplo:

```text
https://peralta-pos-api.onrender.com
```

Probar:

```text
https://peralta-pos-api.onrender.com/api/health
```

Si responde `UP`, el backend esta vivo.

---

## 3. Crear frontend en Vercel

En Vercel:

1. Importar el repo de GitHub.
2. Configurar:

```text
Root Directory: apps/web
Framework: Vite
Build Command: npm run build
Output Directory: dist
Install Command: npm install
```

Variables en Vercel:

```text
VITE_API_BASE_URL=https://tu-backend.onrender.com/api
VITE_CLIENT_CHANNEL=WEB
```

Cuando Vercel genere la URL final, ejemplo:

```text
https://peralta-pos.vercel.app
```

Volver a Render y cambiar:

```text
CORS_ALLOWED_ORIGINS=https://peralta-pos.vercel.app,tauri://localhost
```

Luego reiniciar/redeployar el backend.

---

## 4. Crear lanzador de Windows

Cuando la web este publicada:

```powershell
npm run create:web-launcher -- -Url https://tu-frontend.vercel.app
```

Los archivos quedan en:

```text
output/desktop-launcher
```

Puedes enviar el `.url` o el `.cmd` para abrir el sistema desde Windows.

---

## 5. Crear instalador de escritorio

Copiar:

```text
apps/web/.env.desktop.example
```

como:

```text
apps/web/.env.desktop
```

Poner:

```text
VITE_API_BASE_URL=https://tu-backend.onrender.com/api
VITE_CLIENT_CHANNEL=DESKTOP
```

Compilar:

```powershell
npm run build:desktop
```

El instalador queda en:

```text
apps/desktop/src-tauri/target/release/bundle
```

---

## 6. Orden recomendado

1. Crear MySQL Free en Aiven.
2. Crear backend Docker Free en Render.
3. Probar `/api/health`.
4. Crear frontend en Vercel.
5. Cambiar CORS en Render con la URL exacta de Vercel.
6. Probar login online.
7. Crear lanzador Windows.
