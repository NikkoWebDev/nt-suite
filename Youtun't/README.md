# Youtun't — YouTube modificado, Dark Obsidian & Mint Glass

Build de YouTube **sin root** sobre el bundle oficial de parches ReVanced (v6.1.0 con
`revanced-cli` v6.0.0). La compilación valida cada nombre de parche y opción contra el
bundle **vivo** antes de aplicar nada, y firma el APK con una keystore persistente:
te deja `dist/` con la app lista para instalar, sin pasos manuales.

## Qué hay en dist/ tras compilar

| Archivo | Qué es |
|---|---|
| `Youtunt-S21.apk` / `Youtunt-UNIVERSAL.apk` | YouTube parcheado, firmado con `youtunt.keystore` |
| nada más | los temporales se purgan solos (`--purge`); dist queda limpio |

## Construir

```bash
./youtunt build s21        # etiqueta «Youtun´t S21»
./youtunt build universal  # etiqueta «Youtun´t»
./youtunt verify           # valida nombres de parches/opciones contra bin/patches-official.rvp
```

`build` ya incluye la validación: si un nombre de la config no existe en el
bundle, aborta (exit 1) en vez de aplicar el tema a medias. Salida no interactiva
(sin TTY) con código 0/1/2 — mismo contrato que `./instant`.

El APK base se escoge de `input/`: primero el recomendado exacto
(`input/youtube-20.14.43-nodpi.apk`, **YouTube 20.14.43**, variante nodpi), y solo si
falta, cae al último `youtube-*.apk` con aviso. Ese archivo va en
`.gitignore`; descárgalo de [APKMirror](https://www.apkmirror.com/apk/google-inc/youtube/youtube-20-14-43-release/).

## Parche real aplicado

La config (`config/youtunt-profile.json`) declara **8 opciones sobre 5 parches** y
**1 desactivación**: todo lo demás se comporta como el bundle oficial lo trae
(parches de YouTube activos por defecto: `Video ads`, `SponsorBlock`, `Return
YouTube Dislike`, `Remove background playback restrictions`, `Playback speed`,
`Spoof video streams`, etc. — verificados con `list-patches`).

| Opción (nombre exacto del parche) | Valor |
|---|---|
| Theme → Dark theme background color | `#080A0F` (Obsidian) |
| Theme → Light theme background color | `#F4FAF6` (blanco cálido Mint, **no** negro purista) |
| Custom branding → App name | `Youtun´t S21` / `Youtun´t` |
| Custom branding → Custom icon | `assets/branding` (iconos generados todas las densidades) |
| Change header → Custom header logo | `assets/header` (logos light + dark) |
| Hide Shorts components → Hide Shorts app shortcut | `true` |
| Hide Shorts components → Hide Shorts widget | `true` |
| GmsCore support → gmsCoreVendorGroupId | `app.revanced` |
| (−) Announcements | desactivado (es el único default-on que sobra) |

El color de tema lo pelea el bundle con `revanced_splash_background_color`, la
launchscreen (`quantum_launchscreen_youtube`) y los estilos `Theme.YouTube.Home`,
así el splash sale pintado con el color de tema, no gris YouTube.

## Ajustes de app (runtime, no build)

El bundle oficial no trae opcion de build para la marca menta del player; son
ajustes dentro de la app (**Ajustes → ReVanced**, pantallas verificadas en los
strings del bundle):

- **Seekbar**: activa `Enable custom seekbar color` y setea
  *Custom seekbar color* `#FF00F5A0` · *Custom seekbar accent color* `#FF2DD4BF`
  (el picker del parche `Theme` es `ColorPickerPreference`: acepta los ARGB).
- **Player**: *Player overlay opacity* = `12` (rango 0–100, 0 = transparente):
  el recuadro del reproductor queda casi transparente sobre el video.

## GmsCore (microG no vale)

El parche apoya GmsCore, y `bin/GmsCore.apk` es el **fork ReVanced** (package
`app.revanced.android.gms` — verificado contra el manifiesto del APK), y por eso
`gmsCoreVendorGroupId=app.revanced`. Instala siempre primero GmsCore, luego el APK:

```bash
adb install -r bin/GmsCore.apk
adb install -r dist/Youtunt-S21.apk     # o dist/Youtunt-UNIVERSAL.apk
```

`./youtunt install [s21|universal]` automatiza esos dos pasos si hay dispositivo;
sin dispositivo imprime los comandos exactos y sale con código 1.

## Firma

- Keystore persistente `youtunt.keystore` (pass `youtunt123`, alias `youtunt`):
  actualizaciones con build nuevo se instalan en la misma app sin desinstalar nada.
- Si pierdes la keystore, cualquier reinstalación tiene que ir limpia.

## Debloat de One UI (equipo S21 FE, opcional)

```bash
./youtunt debloat
```

Desinstala bloat Samsung vía `pm uninstall -k --user 0` (Servicios de
personalización, Push Samsung, Samsung Free, etc.) y lana las animaciones a 0.5x.
Reversible con `cmd package install-existing` — el propio script lo imprime.

## Estructura

```
youtunt              CLI (build, install, verify, debloat, branding, mint, status, menu)
bin/                 revanced-cli.jar · patches-official.rvp (v6.1.0) · GmsCore.apk (fork ReVanced)
                     rvx-cli.jar + patches.rvp quedan de la era anterior, NO se usan
config/              youtunt-profile.json (parches+tema) · profiles s21/universal · debloat
scripts/             generate_branding.py (re-dibuja los iconos Mint Glass)
assets/              branding (mipmaps) + header (logos) requeridos por los parches
input/               APK base de YouTube (20.14.43 nodpi, ignorado por git)
dist/                artefactos finales firmados
```

Solo difieren los perfiles en `app_name` y `output`; todo lo demás es compartido.
El «tuning» de hardware del S21 FE (VP9/AV1, 120Hz, 19.5:9) no es parche: lo
decide YouTube/One UI en el propio dispositivo, así el perfil S21 es honestamente
el mismo parche con otra etiqueta.
