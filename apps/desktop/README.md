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
