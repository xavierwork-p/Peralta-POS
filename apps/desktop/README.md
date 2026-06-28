# Peralta POS Desktop

La app de escritorio reutiliza la interfaz de `apps/web` y la empaqueta con Tauri.

## Ejecutar en desarrollo

```bash
npm install
npm --prefix apps/web install
npm --prefix apps/desktop install
npm run dev:desktop
```

La app local necesita conexion al backend Java, igual que la version web.

## Compilar para usar backend online

1. Copia:

```text
apps/web/.env.desktop.example
```

como:

```text
apps/web/.env.desktop
```

2. Cambia la URL:

```text
VITE_API_BASE_URL=https://tu-backend.up.railway.app/api
VITE_CLIENT_CHANNEL=DESKTOP
```

3. Compila el instalador:

```bash
npm run build:desktop
```

El build de escritorio usa `scripts/build-desktop-web.ps1`, que marca la app como canal `DESKTOP`. Asi los usuarios sin permiso web pueden seguir entrando desde una computadora si se les permite el modulo correspondiente.

Los instaladores quedan en:

```text
apps/desktop/src-tauri/target/release/bundle
```

## Lanzador rapido

Si solo necesitas abrir la web publicada desde Windows, crear un lanzador:

```bash
npm run create:web-launcher -- -Url https://tu-frontend.vercel.app
```

El resultado queda en:

```text
output/desktop-launcher
```
