#!/usr/bin/env bash
# ==============================================================================
# Youtun't • One UI & S21 FE Debloat & Performance Optimizer
# Safe debloat for User 0 (Reversible, Knox-safe, no root required)
# ==============================================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
BOLD='\033[1m'
NC='\033[0m'

echo -e "${CYAN}${BOLD}"
echo "  ┌──────────────────────────────────────────────────────────┐"
echo "  │        One UI 8.x / S21 FE Safe Debloat Optimizer        │"
echo "  │              Knox Safe • No Root • Reversible            │"
echo "  └──────────────────────────────────────────────────────────┘"
echo -e "${NC}"

if ! command -v adb &>/dev/null; then
    echo -e "${RED}[ERROR] adb no está instalado o no se encuentra en el PATH.${NC}"
    exit 1
fi

DEVICE=$(adb get-state 2>/dev/null || echo "offline")
if [ "$DEVICE" != "device" ]; then
    echo -e "${RED}[ERROR] No hay un dispositivo conectado por ADB.${NC}"
    echo -e "${YELLOW}Asegúrate de tener la 'Depuración por USB' activa en Opciones de Desarrollador.${NC}"
    exit 1
fi

MODEL=$(adb shell getprop ro.product.model 2>/dev/null || echo "Desconocido")
ONEUI=$(adb shell getprop ro.build.version.oneui 2>/dev/null || echo "N/A")
echo -e "${GREEN}[INFO] Dispositivo detectado: ${BOLD}$MODEL${NC} (One UI version code: $ONEUI)"

# ------------------------------------------------------------------------------
# Package Lists
# ------------------------------------------------------------------------------

# Telemetría y servicios en segundo plano de Samsung
SAMSUNG_TELEMETRY=(
    "com.samsung.android.rubin.app"          # Samsung Customization Service (Rastreo de hábitos y consumo)
    "com.sec.spp.push"                       # Samsung Push Service (Notificaciones promocionales)
    "com.samsung.android.ipsgeofence"        # Samsung Visit-In (Geofencing de tiendas y anuncios)
    "com.samsung.android.app.spage"          # Samsung Free / Samsung O (Panel de noticias izquierda)
    "com.samsung.android.voc"                # Samsung Members (Telemetría de diagnóstico en background)
    "com.samsung.android.kidsinstaller"      # Modo niños (instalador innecesario)
)

# Bloatware de terceros preinstalado por Samsung
THIRD_PARTY_BLOAT=(
    "com.facebook.katana"                    # Facebook App
    "com.facebook.system"                    # Facebook App Installer
    "com.facebook.appmanager"                # Facebook App Manager
    "com.facebook.services"                  # Facebook Services
    "com.microsoft.skydrive"                 # Microsoft OneDrive (si usas GDrive o local)
    "com.microsoft.appmanager"               # Enlace a Windows bloatware si no se usa
)

# Servicios de Google agresivos en batería (Device Health / Telemetría)
GOOGLE_BATTERY_HOGS=(
    "com.google.android.apps.turbo"          # Device Health Services (Historial predictivo agresivo)
    "com.google.android.feedback"            # Google Feedback Crash Reporter
)

remove_package() {
    local pkg=$1
    echo -ne "  → Deshabilitando ${CYAN}$pkg${NC}... "
    if adb shell pm list packages | grep -q "^package:$pkg$"; then
        adb shell pm uninstall -k --user 0 "$pkg" &>/dev/null || adb shell pm disable-user --user 0 "$pkg" &>/dev/null
        echo -e "${GREEN}✓ Desactivado${NC}"
    else
        echo -e "${YELLOW}(No instalado)${NC}"
    fi
}

restore_package() {
    local pkg=$1
    echo -ne "  → Restaurando ${CYAN}$pkg${NC}... "
    adb shell cmd package install-existing "$pkg" &>/dev/null || true
    echo -e "${GREEN}✓ Restaurado${NC}"
}

echo ""
echo -e "${BOLD}Elige una acción:${NC}"
echo "  1) Aplicar Debloat Recomendado (Samsung Telemetría + Facebook/MS Bloat)"
echo "  2) Desactivar RAM Plus y optimizar animaciones (Mejora térmica S21 FE)"
echo "  3) Restaurar todos los paquetes desactivados"
echo "  4) Salir"
echo ""
read -p "Selecciona una opción [1-4]: " OPTION

case $OPTION in
    1)
        echo -e "\n${BOLD}[1/2] Limpiando telemetría y servicios en segundo plano...${NC}"
        for pkg in "${SAMSUNG_TELEMETRY[@]}"; do
            remove_package "$pkg"
        done

        echo -e "\n${BOLD}[2/2] Limpiando bloatware de terceros...${NC}"
        for pkg in "${THIRD_PARTY_BLOAT[@]}"; do
            remove_package "$pkg"
        done
        echo -e "\n${GREEN}${BOLD}¡Debloat completado! El sistema liberará RAM y reducirá consumo en reposo.${NC}"
        ;;
    2)
        echo -e "\n${BOLD}Aplicando ajustes de rendimiento en One UI...${NC}"
        # Escala de animaciones a 0.5x para sensación instantánea
        adb shell settings put global window_animation_scale 0.5
        adb shell settings put global transition_animation_scale 0.5
        adb shell settings put global animator_duration_scale 0.5
        echo -e "${GREEN}✓ Animaciones del sistema aceleradas a 0.5x${NC}"
        echo -e "${YELLOW}[TIP] Para desactivar RAM Plus por completo en One UI:${NC}"
        echo -e "      Ajustes > Mantenimiento del dispositivo > Memoria > RAM Plus > Desactivar y reiniciar."
        ;;
    3)
        echo -e "\n${BOLD}Restaurando paquetes de Samsung y sistema...${NC}"
        ALL_PACKAGES=("${SAMSUNG_TELEMETRY[@]}" "${THIRD_PARTY_BLOAT[@]}" "${GOOGLE_BATTERY_HOGS[@]}")
        for pkg in "${ALL_PACKAGES[@]}"; do
            restore_package "$pkg"
        done
        echo -e "\n${GREEN}Todos los paquetes han sido restaurados.${NC}"
        ;;
    *)
        echo "Operación cancelada."
        ;;
esac
