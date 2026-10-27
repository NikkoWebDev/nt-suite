# Wasapn't 🌿💬
> Custom WhatsApp Build Engine & Privacy Suite • Dark Obsidian & Mint Glass Edition

Diseñado con el sistema de diseño **Mint Glass** inspirado en [nikko.dev](https://www.nikko.dev/) e [IngeSoft](file:///home/niko/Proyectos/IngeSoft/index.html), integrado en la suite de redes `n't` junto a [Youtun't](file:///home/niko/Proyectos/Youtun´t) e [Instan't](file:///home/niko/Proyectos/Instan't). Impulsado por el motor de bytecode de **WaEnhancerX** ([GitHub](https://github.com/Xposed-Modules-Repo/com.waenhancer)) y el empaquetador **LSPatch** para despliegue no-root con firma criptográfica persistente.

---

## 🎨 Sistema de Diseño: Dark Obsidian & Mint Glass

* **Canvas de fondo:** `#080A0F` (Obsidian Ultra-Dark) en reemplazo del gris verdoso plano de WhatsApp.
* **Superficies y Paneles:** `#111827` con bordes translúcidos esmeralda `#1E293B`.
* **Burbujas de Chat:**
  * **Enviadas:** `#0B2920` (Obsidian Deep Mint) con contorno sutil menta neón.
  * **Recibidas:** `#111827` (Dark Surface) con tipografía clara `#F8FAFC`.
* **Acentos & Lectura:**
  * **Checks de Lectura (Read Ticks):** Reemplazados los ticks azules convencionales por **Menta Neón (`#00F5A0`)**.
  * **Badges y contadores de no leídos:** Menta Neón (`#00F5A0`) sobre fondo oscuro.
  * **Enlaces y menciones:** Cian Neón (`#58D8FF`).
  * **Aviso de mensaje revocado (Anti-Delete):** Carmesí suave (`#F43F5E`).
* **Iconografía adaptativa:** Set completo de iconos Android vectoriales generados con contorno frosted glass y globo de chat minimalista con auricular neón.

---

## 🛡️ Política de Seguridad & Anti-Baneo de WhatsApp

A diferencia de los clones y WhatsApp Mods tradicionales (GBWhatsApp, WhatsApp Plus, YoWhatsApp) que alteran los binarios completos, cambian el nombre del paquete o modifican el handshake criptográfico de Signal Protocol:

1. **Sin alteración de cifrado E2EE:** El protocolo Signal y las llaves de cifrado extremo a extremo permanecen intactas en su implementación nativa oficial de Google Play.
2. **Hooks pasivos en memoria RAM:** Modificaciones de visibilidad de UI locales. Cuando un mensaje es eliminado por el remitente, WhatsApp ya entregó el paquete a la base de datos local SQLite; WaEnhancerX simplemente cancela la directiva interna de ocultamiento.
3. **Firma criptográfica fija:** El empaquetador utiliza `bin/wasapnt.keystore` (RSA 2048-bit), lo que permite actualizar versiones futuras sin inconsistencias de firma o cierres forzados.
4. **Bypass Level 2 (`pm + openat`):** Evita la inspección de syscalls a bajo nivel protegiendo el entorno frente a comprobaciones de integridad.

---

## 🚀 Funcionalidades Integradas

### 1. 🛡️ Privacidad Extrema & Anti-Delete
* **Anti-Delete de Mensajes:** Los mensajes borrados ("Este mensaje fue eliminado") se conservan intactos en tu chat con una marca visual indicando que fueron revocados por el remitente.
* **Bypass de Visualización Única (View-Once):** Fotos y videos efímeros configurados para verse 1 sola vez pueden abrirse de manera ilimitada y guardarse directamente en el almacenamiento.
* **Bypass de Capturas de Pantalla:** Anula la directiva `FLAG_SECURE` para permitir capturas y grabaciones de pantalla en cualquier chat, foto efímera o estado.
* **Anti-Revoke de Estados:** Observa los estados/historias de tus contactos incluso si los eliminaron antes de las 24 horas.

### 2. 👻 Ghost Mode (Navegación Fantasma)
* **Ocultar Segundo Tick (Delivered Receipt):** Los mensajes recibidos no mostrarán el segundo tick hasta que decidas abrir el chat o responder.
* **Ocultar Ticks Azules (Read Receipts):** Lee chats sin enviar confirmación de lectura mientras sigues viendo cuándo los demás leen tus mensajes.
* **Ocultar "Escribiendo..." y "Grabando audio...":** Redacta mensajes sin revelar actividad en tiempo real.
* **Ver Estados en Modo Oculto:** Visualiza estados de contactos sin que tu perfil figure en su lista de espectadores.

### 3. 🧘 Anti-Meta AI & Interfaz Limpia
* **Erradicación Absoluta de Meta AI:** Oculta el botón flotante (FAB) de Meta AI, el anillo neón de la barra de búsqueda y las sugerencias automáticas.
* **Preservación Deliberada de Canales (`keepChannels: true`):** Por directiva explícita de Niko, los canales de WhatsApp se preservan deliberadamente activos en la configuración (`config/profile-s21.json`), permitiendo seguir canales informativos y de comunidades sin que sean suprimidos por la limpieza de interfaz.

### 4. ⚡ Integración Nativa en Ajustes
* **Sin menús flotantes invasivos:** Todas las opciones de configuración y personalización se inyectan directamente dentro del menú nativo de **Ajustes > WaEnhancerX** de WhatsApp, con soporte para el buscador de preferencias del sistema.

### 5. 📱 Perfil Tuned para Samsung Galaxy S21 FE
* **Desplazamiento fluido a 120Hz:** Optimización del viewport de chat para pantallas Dynamic AMOLED 2X.
* **Decodificación de audio y stickers:** Procesamiento eficiente en memoria RAM con menor consumo térmico de batería.

---

## 🛠️ Estructura del Proyecto

```
wasapn't/
├── wasapnt                  # CLI runner interactivo principal
├── bin/
│   ├── lspatch.jar          # Motor de inyección no-root LSPatch
│   ├── WaEnhancerX.apk      # Módulo hook oficial WaEnhancerX v2.0.4
│   └── wasapnt.keystore     # Keystore RSA 2048-bit persistente
├── config/
│   ├── wasapnt-theme.json   # Paleta Mint Glass (nikko.dev) & Tweak Rules
│   ├── profile-s21.json     # Perfil optimizado Samsung S21 FE 120Hz
│   └── profile-universal.json # Perfil universal Android API 24+
├── assets/
│   └── branding/            # Iconos adaptativos Android Mint Glass
├── scripts/
│   └── generate_branding.py # Generador procedural PIL de iconos
├── input/                   # Directorio para colocar el APK base de WhatsApp
└── dist/                    # Directorio de salida del APK compilado
```

---

## 📦 Instrucciones de Compilación y Despliegue

### 1. Obtener el APK base oficial
1. Descarga el APK oficial de WhatsApp desde APKMirror:
   * **URL:** [WhatsApp Messenger en APKMirror](https://www.apkmirror.com/apk/whatsapp-inc/whatsapp/)
   * Variante recomendada: **APK nodpi** oficial.
2. Coloca el archivo descargado en `input/`:
   ```bash
   cp /ruta/a/whatsapp.apk input/whatsapp-base.apk
   ```

### 2. Compilar con el CLI
Ejecuta el asistente interactivo:
```bash
./wasapnt
```
O compila directamente por comando:
```bash
./wasapnt build s21         # Para Samsung Galaxy S21 FE
./wasapnt build universal   # Para cualquier dispositivo Android
```

### 3. Instalar en tu teléfono vía ADB
Conecta tu dispositivo por USB con la depuración activa y ejecuta:
```bash
./wasapnt install
```

### 4. Activar Personalización Mint Glass
1. Abre **Wasapn't**.
2. Dirígete a **Ajustes > WaEnhancerX**.
3. En la sección de interfaz y temas, activa los colores correspondientes de `config/wasapnt-theme.json`.

### 5. 🌱 Modo Offline (uso sin internet)

**Causa raiz diagnosticada (WaEnhancerX 2.0.4):** en cada arranque de WhatsApp,
el hook de entrada del módulo `com.waenhancer.xposed.MainHook#handleLoadPackage`
(initializer de `Application.onCreate`) ejecuta una llamada síncrona al provider
`content://com.waenhancer.hookprovider` con el método `request_update_check`,
resuelto por `com.waenhancer.xposed.bridge.HookProvider.call` →
`com.waenhancer.update.UpdateCheckCoordinator.request(context, TRIGGER_WHATSAPP)`.
El gestor arranca con los checks automáticos **activados por defecto** (ver
`UpdateSettingsScreen.kt`: *Auto-Check for Updates*, *Check when module opens*,
*Check when WhatsApp opens*, todos `true`) y su corutina golpea la API de
releases (`https://api.github.com/repos/Xposed-Modules-Repo/com.waenhancer/releases?per_page=100`,
`https://waex.mubashar.dev/api/stargazers`) con `connectTimeout(15 s)`. Mientras,
el hilo principal de WhatsApp espera la IPC (cold-start del proceso del gestor)
y se añade `PreferenceBridgeClient.awaitInitialSync()`. Online no se nota; sin
internet, splash cuelgado o ANR. Los defaults viven en el store PRIVATE del
gestor, así que no se pueden sembrar desde el APK: se corrige una sola vez
desde la UI, sin root.

1. **Apagar los checks automáticos:** abre la app gestora **WaEnhancerX** →
   **Update Settings** → sección **AUTOMATED CHECKS** y desactiva los tres
   switches:
   * *Auto-Check for Updates* (`update_auto_check`)
   * *Check when module opens* (`update_check_module_open`)
   * *Check when WhatsApp opens* (`update_check_whatsapp_open`)
2. **Verificar offline:** con el móvil en modo avión (WiFi/datos OFF) y el S21 FE
   por USB:
   ```bash
   adb shell am force-stop com.whatsapp
   adb logcat -c; adb logcat | grep -E "LSPatch|WaEnhancerX|WAEX"
   ```
   Abre Wasapn't en avión y espera el arranque completo (splash breve +
   `WAEX` runtime `INJECTED` → `READY` en el log para `com.whatsapp`).
   **Esperado:** sin ANR de `com.whatsapp` en `Application.onCreate`; los
   intentos de fetch de releases fallan async y en silencio (no bloquean UI).
   Después: **Ajustes > WaEnhancerX** abre y los chats se leen igual que con
   red.
3. **Uso offline esperado:** la base local (SQLite msgstore) es la fuente viva:
   chats con caché legibles, anti-delete y ghost mode (hooks pasivos) funcionan
   sin red. Enviar mensajes requiere red (se quedan en cola cuando no hay).
   Tras el paso 1 el módulo deja de inyectar trabajo de red en el arranque.

> Alternativa sin móvil a mano: `./wasapnt offline` imprime esta misma guía en
> terminal (3 pasos + filtro logcat). Si tras el paso 1 persiste un spinner
> notable en avión, adjunta el logcat del paso 2 para el diagnóstico.

---

## 🌐 Suite de Redes n't

* [Youtun't](file:///home/niko/Proyectos/Youtun´t): Motor personalizado de YouTube sin Shorts, SponsorBlock y paleta Mint Glass.
* [Instan't](file:///home/niko/Proyectos/Instan't): Instagram sin Reels distractivos, descargas en alta fidelidad y Ghost Mode.
* **Wasapn't**: WhatsApp con privacidad total, anti-delete, bypass view-once y sin Meta AI.
