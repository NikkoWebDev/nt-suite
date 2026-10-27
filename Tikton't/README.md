# Tikton't — TikTok modificado, Dark Obsidian & Mint Glass

Build de TikTok **sin root** sobre el bundle oficial de parches ReVanced (v6.1.0 con
`revanced-cli` v6.0.0). Diseñado para erradicar anuncios, transmisiones en vivo y
ruido algorítmico, desbloqueando descargas sin restricciones, barra de reproducción,
control de velocidad y privacidad total.

La compilación valida cada parche contra el bundle **vivo** antes de aplicar cambios,
compilando bajo firma BKS persistente (`tiktont.keystore`).

---

## 📦 Qué hay en dist/ tras compilar

| Archivo | Descripción |
|---|---|
| `Tiktont-S21.apk` | Build optimizado para Samsung Galaxy S21 FE (120Hz Dynamic AMOLED) |
| `Tiktont-UNIVERSAL.apk` | Build compatible con cualquier arquitectura arm64 (Android API 24+) |
| Limpieza automática | Archivos temporales purgados automáticamente (`--purge`) tras compilar |

---

## 🚀 Construir y Gestionar

```bash
./tiktont verify           # Valida dependencias, keystore y los 13 parches contra el bundle vivo
./tiktont build s21        # Compila con perfil Samsung S21 FE Tuned
./tiktont build universal  # Compila con perfil Universal arm64
./tiktont clean            # Limpia dist/ y la caché de trabajo
./tiktont status           # Inspecciona herramientas, versiones, keystore y ADB
./tiktont install [perfil] # Despliega vía ADB al dispositivo conectado
```

Si ejecutas `./tiktont` en una terminal interactiva sin argumentos, se abrirá el menú TUI interactivo.

---

## 🛡️ Parches Oficiales Verificados (13 parches)

El build opera en modo exclusivo (`--exclusive`), garantizando que únicamente se aplican
los 13 parches probados y aprobados para los paquetes objetivo `com.zhiliaoapp.musically`
y `com.ss.android.ugc.trill`:

| Parche | Alcance | Descripción |
|---|---|---|
| **Feed filter** | `musically` / `trill` | Filtra anuncios patrocinados, transmisiones en vivo (lives), historias y videos promocionales del feed. |
| **Remember clear display** | `musically` / `trill` | Mantiene el modo de visualización limpia (Clear Display) activo entre videos sucesivos. |
| **Downloads** | `musically` / `trill` | Habilita la descarga directa de videos sin marcas de agua ni restricciones geográficas. |
| **Show seekbar** | `musically` / `trill` | Despliega la barra de progreso y control de tiempo en todos los videos. |
| **Playback speed** | `musically` / `trill` | Habilita selector de velocidad de reproducción (0.5x, 1x, 1.5x, 2x) persistente. |
| **Disable login requirement** | `musically` / `trill` | Permite explorar feeds, perfiles y búsquedas sin forzar inicio de sesión. |
| **Fix Google login** | `musically` / `trill` | Resuelve la autenticación con cuentas de Google en clientes modificados. |
| **Settings** | `musically` / `trill` | Inyecta el panel de configuración de ReVanced dentro de los ajustes de TikTok. |
| **Sanitize sharing links** | `musically` / `trill` | Purga parámetros de seguimiento (`tracking query params`) al compartir enlaces. |
| **Disable Sentry telemetry** | Universal | Bloquea el envío de reportes de errores y telemetría a los servidores de Sentry. |
| **Remove screen capture restriction** | Universal | Suprime las restricciones de captura de audio y transmisión de pantalla. |
| **Prevent screenshot detection** | Universal | Anula los listeners de captura de pantalla para evitar avisos o bloqueos. |
| **Remove screenshot restriction** | Universal | Elimina la restricción `FLAG_SECURE` para permitir capturas en cualquier pantalla. |

---

## 📥 Obtención del APK Base

1. Descarga el APK oficial de TikTok (`com.zhiliaoapp.musically`):
   * Recomendado: variante **nodpi** oficial desde [APKMirror](https://www.apkmirror.com/apk/tiktok-pte-ltd/tiktok/).
2. Coloca el archivo descargado en el directorio `input/`:
   ```bash
   cp /ruta/a/tiktok.apk input/tiktok-base.apk
   ```
   *El motor también reconoce patrones automáticos como `input/tiktok-*.apk` o `input/musically-*.apk`.*

---

## 🛠️ Estructura del Proyecto

```
Tikton't/
├── tiktont                  # CLI runner interactivo y ejecutable
├── bin/
│   ├── revanced-cli.jar     # Symlink a ReVanced CLI v6.0.0
│   └── patches-official.rvp # Symlink al bundle oficial de parches v6.1.0
├── config/
│   ├── tiktont-profile.json # Definición del bundle, parches activos y colores Mint Glass
│   ├── profile-s21.json     # Perfil afinado para Galaxy S21 FE (120Hz)
│   └── profile-universal.json # Perfil universal arm64
├── scripts/
│   └── KsTool.java          # Generador y verificador criptográfico de keystores BKS
├── input/                   # Directorio para alojar el APK base
├── dist/                    # Directorio de salida del APK compilado
└── tiktont.keystore         # Keystore RSA 2048-bit (formato BKS/BouncyCastle)
```

---

## 🌐 Suite de Redes n't

* [Youtun't](file:///home/niko/Proyectos/n't/Youtun't): YouTube sin anuncios, sin Shorts, SponsorBlock y Mint Glass.
* [Instan't](file:///home/niko/Proyectos/n't/Instan't): Instagram sin Reels, descargas nativas, Ghost Mode y paleta Dark Obsidian.
* [Wasapn't](file:///home/niko/Proyectos/n't/Wasapn't): WhatsApp con privacidad total, anti-delete, bypass view-once, sin Meta AI y con canales preservados (`keepChannels: true`).
* **Tikton't**: TikTok sin anuncios, sin lives, descargas limpias, seekbar, velocidad y privacidad total.
