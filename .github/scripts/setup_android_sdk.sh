#!/bin/bash

set -e  # Exit immediately on error

# -------------------------
# Global Variables
# -------------------------
ANDROID_HOME_DEFAULT="$HOME/Library/Android/sdk"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-mac-11.0_latest.zip"
SHELL_RC=""
LOG_FILE="android_sdk_install.log"

# -------------------------
# Logging Functions
# -------------------------
log() {
    echo "[INFO] $1" | tee -a "$LOG_FILE"
}

error() {
    echo "[ERROR] $1" | tee -a "$LOG_FILE" >&2
}

# -------------------------
# Dependency Installation
# -------------------------
check_command() {
    command -v "$1" &> /dev/null
}

install_homebrew_dependencies() {
    log "Checking and installing Homebrew dependencies..."

    if ! check_command "brew"; then
        log "Homebrew not found. Installing Homebrew..."
        /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    fi

    for pkg in curl unzip; do
        if ! check_command "$pkg"; then
            log "Installing missing package: $pkg"
            brew install "$pkg"
        fi
    done
}

# -------------------------
# Environment Setup
# -------------------------
configure_android_home() {
    log "Configuring ANDROID_HOME..."

    if [ -z "$ANDROID_HOME" ]; then
        export ANDROID_HOME="$ANDROID_HOME_DEFAULT"
        mkdir -p "$ANDROID_HOME"

        if [ -n "$ZSH_VERSION" ]; then
            SHELL_RC="$HOME/.zshrc"
        else
            SHELL_RC="$HOME/.bash_profile"
        fi

        echo "export ANDROID_HOME=$ANDROID_HOME" >> "$SHELL_RC"
        echo "export PATH=\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools:\$PATH" >> "$SHELL_RC"
        log "ANDROID_HOME set and exported in $SHELL_RC"
    fi
}

# -------------------------
# SDK Tools Download
# -------------------------
download_cmdline_tools() {
    log "Downloading Android Command Line Tools..."
    curl -o cmdline-tools.zip "$CMDLINE_TOOLS_URL"
    unzip -q cmdline-tools.zip -d "$ANDROID_HOME"

    mkdir -p "$ANDROID_HOME/cmdline-tools/latest"
    mv "$ANDROID_HOME/cmdline-tools/"* "$ANDROID_HOME/cmdline-tools/latest" 2>/dev/null

    export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
    rm -f cmdline-tools.zip
    log "Command Line Tools installed."
}

# -------------------------
# SDK Components Installation
# -------------------------
accept_licenses() {
    log "Accepting Android SDK licenses..."
    yes | sdkmanager --licenses || log "License acceptance might have partial failures."
}

update_sdk_manager() {
    log "Updating sdkmanager to the latest stable channel..."
    sdkmanager --update --channel=stable
}

install_basic_components() {
    log "Installing basic components..."
    sdkmanager --channel=stable \
        "platform-tools" \
        "emulator" \
        "extras;google;google_play_services"
}

install_build_tools() {
    log "Installing build-tools..."
    local versions=(
      "36.0.0" "35.0.0" "34.0.0" "33.0.2" "33.0.3" "32.0.0"
      "31.0.0" "30.0.3" "29.0.3" "28.0.3" "27.0.3" "26.0.3" "25.0.3" "24.0.3"
    )

    for version in "${versions[@]}"; do
        sdkmanager --channel=stable "build-tools;$version" || log "build-tools;$version not available"
    done
}

install_platforms() {
    log "Installing platforms..."
    local apis=(36 35 34 33 32 31 30 29 28 27 26 25 24)

    for api in "${apis[@]}"; do
        sdkmanager --channel=stable "platforms;android-$api" || log "platforms;android-$api not available"
    done
}

install_system_images() {
    log "Installing system images..."
    local apis=(36 35 34 33 32 31 30 29 28 27 26 25 24)

    for api in "${apis[@]}"; do
        local image_type="google_apis;x86_64"
        [[ "$api" -ge 28 ]] && image_type="google_apis_playstore;x86_64"

        sdkmanager --channel=stable "system-images;android-$api;$image_type" || log "system-images;android-$api;$image_type not available"
    done
}

install_extensions() {
    log "Installing extension platforms..."
    local extensions=(
        "platforms;android-35-ext14"
        "platforms;android-35-ext15"
        "platforms;android-34-ext8"
        "platforms;android-34-ext10"
        "platforms;android-34-ext11"
        "platforms;android-34-ext12"
        "platforms;android-33-ext4"
        "platforms;android-33-ext5"
        "platforms;android-32-ext5"
        "platforms;android-31-ext4"
        "platforms;android-31-ext5"
        "platforms;android-30-ext4"
        "platforms;android-30-ext5"
        "platforms;android-29-ext4"
        "platforms;android-29-ext5"
        "platforms;android-28-ext4"
        "platforms;android-28-ext5"
    )

    for ext in "${extensions[@]}"; do
        sdkmanager --channel=stable "$ext" || log "$ext not available"
    done
}

# -------------------------
# Main Script
# -------------------------
main() {
    log "Starting Android SDK setup..."

    install_homebrew_dependencies
    configure_android_home
    download_cmdline_tools
    update_sdk_manager    # <- Update FIRST
    accept_licenses       # <- Accept after first update
    install_basic_components
    install_build_tools
    install_platforms
    install_system_images
    install_extensions

    log "Android SDK installation completed successfully."
    log "Please add the following to your shell configuration file ($SHELL_RC) if not already present:"
    echo "export ANDROID_HOME=$ANDROID_HOME" | tee -a "$LOG_FILE"
    echo "export PATH=\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools:\$PATH" | tee -a "$LOG_FILE"

    echo ""
    echo "To apply changes immediately, run:"
    echo "source $SHELL_RC"
    echo ""
    echo "Or restart your terminal."
}

# Run the script
main
