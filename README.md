# `n't` — Android De-bloated & Mint Glass Suite

> **The anti-bloatware suite for Android: Zero ads, zero corporate telemetry, Dark Obsidian & Mint Glass design, and strict hardware optimization for Samsung Galaxy S21 FE.**

---

## 🎯 Visión y Filosofía

Las aplicaciones oficiales más comunes en Android (Instagram, YouTube, WhatsApp, Spotify, TikTok) han evolucionado hacia contenedores masivos de telemetría comercial, algoritmos adictivos forzados, anuncios intrusivos y código de seguimiento que agotan la batería, saturan la RAM y calientan el procesador.

La suite **`n't`** rescata estas herramientas bajo cuatro principios fundamentales:

1. **Cero Publicidad & Cero Relleno**: Eliminación radical de banners, videos patrocinados, shopping forzado, Reels/Shorts invasivos, transmisiones basura y botones no solicitados (como Meta AI o Grok).
2. **Estética Unificada: Dark Obsidian & Mint Glass**: Sistema visual homogéneo inspirado en interfaces acristaladas de alto contraste. Fondo Obsidian (`#080A0F` / `#070D0A`) que apaga físicamente los subpíxeles en pantallas Dynamic AMOLED 2X, con acentos neón menta (`#00F5A0` / `#3DFFA0`) y bordes de refracción translúcida.
3. **Optimización Térmica y Batería (Galaxy S21 FE Tuned)**:
   - Prevención de sobrecalentamiento y *thermal throttling* en chipsets de 5nm (Snapdragon 888 / Exynos 2100).
   - Renderizado limpio a 120Hz sin recomposiciones parasitarias de CPU.
   - Decodificación acelerada por hardware (VP9 para video y Opus nativo para audio).
   - Supresión de WakeLocks y daemons de segundo plano que evitan el *Deep Sleep* del dispositivo.
4. **Privacidad Máxima & Anti-Baneo Pasivo**:
   - Modificaciones estrictamente locales en memoria RAM y bytecode visual.
   - Conservación intacta del cifrado de extremo a extremo (E2EE Signal Protocol) en WhatsApp.
   - Supresión de rastreadores (Facebook Graph, Firebase Analytics, Sentry Telemetry).
   - Firmas criptográficas persistentes (`.keystore`) para soportar actualizaciones sin reinstalación.

---

## 📱 Matriz de Proyectos

| Aplicación | Motor Tecnológico | Reemplaza a | Características Clave |
| :--- | :--- | :--- | :--- |
| [**Spotin't**](./Spotin't) | Kotlin 2.2 + Jetpack Compose + Media3 | Spotify | Pura música. Cero podcasts ni audiolibros. Sin anuncios. Fallback InnerTube (FLAC/Hi-Fi). `GlassCard` nativa. |
| [**Instan't**](./Instan't) | LSPatch L2 + InstaEclipse + ThemeSeeder | Instagram | Sin Meta AI. Sin ads. Ghost mode (visto, typing, historias). Anti-delete DM. Descarga original. Auto-seed Mint Glass. |
| [**Wasapn't**](./Wasapn't) | LSPatch L2 + WaEnhancerX | WhatsApp | Anti-delete de mensajes y estados. View-once ilimitado. Ghost mode. Sin Meta AI. **Canales preservados (`keepChannels: true`)**. |
| [**Youtun't**](./Youtun't) | ReVanced CLI 6.0 + Patches v6.1.0 | YouTube | Sin anuncios de video ni banners. SponsorBlock. Reproducción en segundo plano. Erradicación de Shorts. Seekbar Menta. |
| [**Tikton't**](./Tikton't) | ReVanced CLI 6.0 + Patches v6.1.0 | TikTok | Sin anuncios ni transmisiones en vivo. Modo pantalla limpia persistente (*Clear Display*). Descargas sin marca de agua. Seekbar menta. Cero telemetría Sentry. |

---

## 🎨 Sistema de Diseño: Dark Obsidian & Mint Glass

Toda la suite comparte una identidad gráfica coherente:

```
┌────────────────────────────────────────────────────────┐
│  Canvas Base: #080A0F (Obsidian Ultra-Dark, 0% OLED)   │
│  Superficie Glass: rgba(255, 255, 255, 0.08)          │
│  Borde Refracción: rgba(61, 255, 160, 0.20)           │
│  Acento Neón Primario: #00F5A0 / #3DFFA0               │
│  Texto Principal: #F2FFF8                              │
└────────────────────────────────────────────────────────┘
```

---

## 🛠️ Requisitos del Entorno (Host de Compilación)

Entorno validado en **CachyOS / Arch Linux x86_64**:

- **Java JDK**: OpenJDK 21 (`/usr/lib/jvm/java-21-openjdk`)
- **Android SDK**: `platform-tools`, `build-tools 35.0.0` (`/home/niko/Android/Sdk`)
- **Python**: Python 3.10+ con librería `Pillow` (para generación de assets y seeder de temas)
- **Android Debug Bridge (ADB)**: Para despliegue directo por USB

---

## 🚀 Guía Rápida de Compilación

Cada proyecto cuenta con su propia herramienta de línea de comandos robusta (`set -euo pipefail`), soporte de verificación estricta contra bundles vivos y ejecución interactiva o headless:

### 1. Spotin't (Cliente Nativo Compose)
```bash
cd "Spotin't/spotui-src"
./gradlew assembleDebug
# Salida: app/build/outputs/apk/debug/app-debug.apk
```

### 2. Instan't (Instagram Mod Engine)
```bash
cd "Instan't"
./instant build s21        # Build optimizado para Samsung S21 FE
./instant build universal  # Build genérico arm64
```

### 3. Wasapn't (WhatsApp Privacy Engine)
```bash
cd "Wasapn't"
./wasapnt status           # Verifica dependencias y estado de firma
./wasapnt build            # Empaqueta y firma con WaEnhancerX
```

### 4. Youtun't (YouTube ReVanced Engine)
```bash
cd "Youtun't"
./youtunt verify           # Valida opciones contra el bundle vivo
./youtunt build s21        # Compila con branding Mint Glass
```

### 5. Tikton't (TikTok Clean Engine)
```bash
cd "Tikton't"
./tiktont verify           # Valida los 13 parches oficiales
./tiktont build s21        # Genera Tiktont-S21.apk
```

---

## ⚖️ Política de Distribución y Código Ético

Este repositorio **no almacena ni distribuye binarios APK propietarios protegidos por derechos de autor**. 

La suite se compone exclusivamente de:
- Scripts de automatización y orquestación de ingeniería inversa local.
- Módulos inyectores de temas de código abierto.
- Código fuente nativo de clientes FOSS.
- Perfiles de configuración y assets visuales originales libres de derechos.

El usuario debe suministrar sus propios archivos APK legítimos (extraídos de su dispositivo o de fuentes públicas verificadas como APKMirror) en las carpetas `input/` correspondientes.

---

## 👤 Autoría
Desarrollado y mantenido por **nikko.dev** ("N").
Parte del ecosistema personal de herramientas de alto rendimiento.
