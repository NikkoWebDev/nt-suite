# Instan't — Instagram modificado, Dark Obsidian & Mint Glass

Build de Instagram **sin root** con motor Xposed integrado, tema Mint Glass autosembrado
y paquete de anti-distracción. La compilación deja el APK **listo para usar**: la
configuración (tema + preferencias) se aplica sola en el primer arranque, sin pasos manuales.

## Qué hay en dist/ tras compilar

| Archivo | Qué es |
|---|---|
| `Instant-S21.apk` / `Instant-UNIVERSAL.apk` | Instagram firmado con LSPatch (bypass nivel 2) |
| `seed-s21.apk` / `seed-universal.apk` | Módulo seeder embebido (perfil + paleta) |
| `palette-dark.json` | Paleta oscura canónica (15 slots AMGB como enteros con signo) |
| `palette-light.json` | Referencia clara (el modo claro usa un preset integrado del módulo) |

## Construir

```bash
./instant build s21        # perfil Samsung S21 FE tuned
./instant build universal  # perfil universal limpio
```

Salida no interactiva (sirve para CI/terminal sin TTY): imprime resumen y sale con código 0.

Ambos compilados incrustan **dos módulos** dentro del propio Instagram vía LSPatch:

1. `com.instant.themeseeder` (está en `scripts/seed-module/`) — escribe la configuración
   del perfil y la paleta en `instaeclipse_prefs` **antes** de que InstaEclipse las lea.
   Cada clave sembrada queda sellada con `__igts_<clave>`, así que un valor antiguo del
   companion nunca la pisa. Regenerado por `./scripts/build_seed_module.sh <perfil>`.
2. `ps.reso.instaeclipse` (`bin/InstaEclipse-Legacy.apk`) — el motor de mods (ghost mode,
   downloader, filtro de feeds, tema, etc.).

## Tema: qué ocurre al primer arranque

- **Modo oscuro**: paleta personalizada Mint Glass (`themePresetId=0` + `themePaletteJson`),
  15 slots, alphas de vidrio (`surface` al 95 %, divisores al 80 %). Además del fondo,
  pinta barra de estado, navegación, iconos, links y estados de error.
- **Modo claro**: el preset integrado **Mint Fresh Light** (`themeLightPresetId=113`).
  Restricción honesta del módulo InstaEclipse: con preset de modo claro (`> 0`) solo se
  pueden usar presets integrados; una paleta JSON personalizada en modo claro no es
  aplicable (issue #251 del módulo). `config/palette-light.json` documenta el equivalente.
- **Cambiar algo a mano**: cualquier opción del menú Instan't (mantener presionada la Lupa
  de Explore) manda sobre el seed; el seeder solo resiembra si cambias de build/perfil.
- **Apagón de emergencia**: `adb shell setprop debug.instaeclipse.off 1` carga el módulo
  sin instalar hooks.

El Jardín de la paleta vive en `scripts/generate_palettes.py`: reproduce las fórmulas de
`ThemePresets.toLight()` del módulo para que la variante clara y el seed hablen el mismo
idioma (IDs de preset verificados contra el código fuente del módulo, Mint Fresh = id 13,
color de fondo oscuro → su variante clara es 113).

## Instalar (con dispositivo)

```bash
./instant install s21
# o
adb install -r dist/Instant-S21.apk
```

Sin dispositivo, `./instant install` imprime el comando exacto y sale limpio.

## Anti-ban & firma

- Bypass de firma **nivel 2** (`pm` + `openat`) → Play Integrity no deja la app.
- Keystore persistente (`bin/instant.keystore`, pass `instant123`, alias `instant`),
  así las actualizaciones del build no obligan a reinstalar limpio.
- Los riesgos reales están documentados con `./instant antiban` (spam, scraping masivo,
  GPS falso frenético: eso sí banea; los hooks locales no).

## Estructura

```
bin/             lspatch.jar · InstaEclipse-Legacy.apk · keystore
config/          profiles s21/universal · paletas canónicas · theme seed
scripts/         build_seed_module.sh · generate_palettes.py · seed-module/ (java + stubs)
input/           instagram-base.apk
dist/            artefactos finales
```

Notas técnicas (módulo fuente verificado en /tmp/opencode/ie-src): el seeder es un módulo
legacy (`assets/xposed_init` + `classes.dex`, `de.robv.android.xposed.*`), sin stub en el dex
final (compilar con stubs pero con classpath aparte). El orden de `-m` no bloquea: InstaEclipse
relee las prefs en su after-hook de `Application.attach`. LSPatch lo confirma en
`embedModules()` y en el framework de carga de módulos embebidos.
