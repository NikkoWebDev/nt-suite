# Spotin't — Documentación Técnica de Arquitectura, Diseño y Entorno

> **Destinatario**: Agentes de Inteligencia Artificial y Desarrolladores del Proyecto.  
> **Propósito**: Proveer el contexto completo del fork, decisiones arquitectónicas, árbol de componentes, sistema de diseño "Mint Glass", resolución de incidencias críticas (audio/streaming) y procedimientos de despliegue en dispositivo físico.

---

## 1. Ficha del Proyecto

- **Nombre**: Spotin't
- **Creador / Contexto de Autoría**: nikko.dev ("N")
- **Repositorio Base Original**: [Spotui/Spotui](https://github.com/Spotui/Spotui) (derivado a su vez de *Neptune*).
- **Ruta de Trabajo Local**: `/home/niko/Proyectos/Spotin't/`
- **Código Fuente**: `/home/niko/Proyectos/Spotin't/spotui-src/`
- **Propósito del Fork**: 
  1. Cliente alternativo a Spotify enfocado exclusivamente en **música pura**.
  2. **Eliminación y exclusión** de Podcasts y Audiolibros.
  3. **Sin publicidad (Ad-Free)** ni interrupciones comerciales.
  4. Rediseño visual radical basado en **Glassmorphism Verde Menta (Mint Glass)** con inspiración en iOS 26 / widgets acristalados translúcidos.
  5. Soporte de audio de alta fidelidad (FLAC / YouTube Music extractor fallback).

---

## 2. Stack Tecnológico & Entorno de Compilación

| Capa | Tecnología / Versión | Notas de Integración |
|---|---|---|
| **Sistema Operativo** | CachyOS (Arch Linux x86_64) | Host del desarrollador |
| **Lenguaje** | Kotlin 2.2.0 | Multiplataforma / JVM target 21 |
| **JDK de compilación** | OpenJDK 21 (`/usr/lib/jvm/java-21-openjdk`) | **Crítico**: el repo original especificaba Java 17; fue actualizado a Java 21 para sincronizar tasks Kotlin/Javac. |
| **Android SDK** | `compileSdk = 35`, `targetSdk = 35`, `minSdk = 26` | Ruta SDK: `/home/niko/Android/Sdk` |
| **UI Toolkit** | Jetpack Compose (Declarativo 100%) | Material 3 como base + sobrescrituras custom |
| **Inyección de Dependencias**| Hilt (Dagger) + KSP | Gestores de ViewModels y Repositorios |
| **Networking & HTTP** | Ktor 3.4.2 + OkHttp | Endpoints REST, GraphQL de Spotify e interceptores |
| **Carga de Imágenes** | Glide Compose (`@OptIn(ExperimentalGlideComposeApi::class)`) | Carátulas y avatares con placeholder |
| **Extracción de Paleta** | AndroidX Palette (`com.music.spotui.di.Palette`) | Extrae colores dominantes de la carátula para el fondo dinámico |
| **Motor de Reproducción** | Jetpack Media3 / ExoPlayer | `PlaybackService` con foreground service |
| **Dispositivo de Prueba** | Infinix HOT 50 5G (`Infinix X6720B`, serie: `131537053X006436`) | Conexión vía ADB sobre USB |

---

## 3. Módulos del Proyecto

El proyecto está estructurado como un multi-módulo Gradle:

```
spotui-src/
├── app/          # Módulo principal Android (UI, Navegación, ViewModels, Theme)
├── spotify/      # Cliente Kotlin puro para Spotify (GraphQL + REST endpoints + OAuth sp_dc)
└── innertube/    # Extractor de audio basado en YouTube Music (InnerTube API + NewPipe)
```

---

## 4. Sistema de Diseño: Mint Glass (Glassmorphism)

### 4.1. Filosofía Estética
- **Fondo base**: Oscuro con tinte esmeralda profundo (`#070D0A`), evitando negros puros `#000000` de Spotify clásico.
- **Degradado atmosférico**: Iluminación radial central esmeralda (`#0D2E1B` a `#070D0A`).
- **Superficies Glass (Vidrio Translúcido)**: Tarjetas y paneles con opacidades lineales (10% a 15% blanco o verde menta oscuro), bordes finos de 1dp a 1.2dp con degradados reflejantes (`GlassBorder` = `rgba(255,255,255,0.22)`).
- **Acento Primario**: Verde Menta Neón (`#3DFFA0`) reemplazando el verde oficial Spotify (`#1ED760`).
- **Navegación Flotante**: Bottom bar cápsula (Pill Shape) con elevación, sombra tintada y desenfoque simulado.
- **Mini-Player Apple-Music Inspired**: Barra flotante suspendida con esquinas de 20dp, indicador FLAC y borde acristalado.

### 4.2. Tokens de Color (`com.music.spotui.ui.theme.Color.kt`)
```kotlin
val MintPrimary           = Color(0xFF3DFFA0)  // Verde menta vibrante (Acentos, Play, Sliders)
val MintAccent            = Color(0xFF00E87A)  // Acento secundario y bordes activos
val MintGlow              = Color(0xFF1DB87A)  // Resplandor difuso
val MintDeep              = Color(0xFF052E1B)  // Fondo verde oscuro
val AppBackground         = Color(0xFF070D0A)  // Fondo principal de la app
val SurfaceDark           = Color(0xFF0D1A14)  // Base de contenedores glass
val GridBackground        = Color(0xFF13241C)  // Tarjetas secundarias
val GlassSurface          = Color(0x1AFFFFFF)  // 10% blanco translúcido
val GlassBorder           = Color(0x33FFFFFF)  // Borde acristalado 20%
val GlassBorderMint       = Color(0x663DFFA0)  // Borde brillante menta
val TextPrimary           = Color(0xFFF2FFF8)  // Blanco menta claro
val TextSecondary         = Color(0xFF8ABFA3)  // Texto atenuado verde suave
```

### 4.3. Componente Reutilizable: `GlassCard.kt`
Ubicación: `app/src/main/java/com/music/spotui/ui/components/GlassCard.kt`
Implementa:
- Sombras ambientales dobles (negra + halo menta sutil).
- Fondo con gradiente degradado translúcido.
- Borde vectorial degradado para simular refracción de luz en los bordes del cristal.

---

## 5. Historial de Bugs Críticos Resueltos

Cualquier IA que trabaje en este código debe conocer estas soluciones para **no revertirlas**:

### Bug 1: Inconsistencia JVM Target en Compilación
- **Problema**: El compilador Java usaba target 21, mientras que Kotlin requería 17 por defecto en `spotify/build.gradle.kts`, `innertube/build.gradle.kts` y `app/build.gradle.kts`.
- **Solución**: Se unificaron todos los `jvmToolchain` y `jvmTarget` a `JavaVersion.VERSION_21` / `JvmTarget.JVM_21`.

### Bug 2: Fallo de Audio "Missing stream expire time" (InnerTube Fallback)
- **Síntoma**: La app cargaba las carátulas y metadatos desde Spotify, pero al reproducir salía el Toast: `Couldn't find a playable stream for this track`.
- **Causa Raíz**: YouTube Music modificó sus respuestas JSON de streaming (`PlayerResponse.StreamingData`). El campo `expiresInSeconds` dejó de enviarse de forma estricta. La app tenía este campo tipado como `val expiresInSeconds: Int` (no nulo) y en `YTPlayerUtils.kt` lanzaba `Exception("Missing stream expire time")`, descartando todos los clientes de streaming.
- **Archivos Modificados**:
  1. `innertube/src/main/kotlin/com/metrolist/innertube/models/response/PlayerResponse.kt`:
     ```kotlin
     // ANTES: val expiresInSeconds: Int,
     // AHORA:
     val expiresInSeconds: Int? = null,
     ```
  2. `app/src/main/java/com/metrolist/music/utils/YTPlayerUtils.kt`:
     ```kotlin
     // ANTES: if (streamExpiresInSeconds == null) throw Exception("Missing stream expire time")
     // AHORA:
     val finalExpiresInSeconds = streamExpiresInSeconds ?: 21600
     ```

### Bug 3: Rate Limiting (429) de Spotify GraphQL / REST
- **Causa**: Al realizar llamadas masivas para poblar el feed sin caché o ante saturación de sesión, `Spotify.kt` tenía `failFastOn429 = true` en llamadas como `topTracks` y `topArtists`.
- **Solución**: Se cambió a `failFastOn429 = false` para permitir reintentos con backoff progresivo sin tumbar el Home.

### Bug 4: Tarjetas sin estilo Glassmorphism
- **Causa**: Las cards del carrusel en `HomeScreen.kt` (`HomeFeedCard`) utilizaban `Column` sin modifiers de elevación, fondo ni bordes.
- **Solución**: Se encapsuló la card con `shadow(spotColor = MintPrimary.copy(0.2f))`, borde `GlassBorder`, esquinas `RoundedCornerShape(16.dp)` y fondo con gradiente `#142B21` a `#09140E`.

---

## 6. Procedimientos de Construcción y Despliegue

### Variables de Entorno Requeridas:
```bash
export ANDROID_HOME=/home/niko/Android/Sdk
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
```

### Compilar APK Debug:
```bash
cd /home/niko/Proyectos/Spotin\'t/spotui-src
$JAVA_HOME/bin/java -version # Verificar Java 21
./gradlew assembleDebug
```
*Ubicación del artefacto resultante*:  
`app/build/outputs/apk/debug/app-debug.apk`

### Instalación en el Dispositivo (Infinix Hot 50 5G):
```bash
adb -s 131537053X006436 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s 131537053X006436 shell "am force-stop com.music.spotui && am start -n com.music.spotui/.MainActivity"
```

---

## 7. Directrices para Futuras Tareas de IA

1. **Mantener la disciplina No-Podcasts**: Si se agregan nuevas pantallas o se refactoriza `MyNavHost.kt`, no reinstanciar rutas de podcasts, shows ni audiolibros.
2. **Consistencia de Color**: No utilizar `Color.Black` ni `Color(0xFF1ED760)`. Usar siempre los tokens de `com.music.spotui.ui.theme.Color.kt` (`AppBackground`, `MintPrimary`, `GlassBorder`, etc.).
3. **Manejo de Errores de Audio**: El audio depende de la resolución de fuentes secundarias (YouTube Music / SpotiFLAC / Deezer). Nunca hacer obligatorios parámetros efímeros de stream URL que cambien en las APIs externas.
4. **Respetar la Identidad**: El branding de la aplicación es `Spotin't` con el avatar/icono distintivo `N` (nikko.dev).
