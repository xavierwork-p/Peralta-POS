fn main() {
    tauri::Builder::default()
        // Este plugin permite abrir enlaces o archivos externos desde la app local.
        .plugin(tauri_plugin_opener::init())
        .run(tauri::generate_context!())
        .expect("error al iniciar Peralta POS Desktop");
}
