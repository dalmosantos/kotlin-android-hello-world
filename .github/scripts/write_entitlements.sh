#!/bin/bash
set -euo pipefail

# Constants
ANDROID_SDK=${ANDROID_HOME:-$HOME/android-sdk}
TARGET_DIR="$ANDROID_SDK/emulator/qemu/darwin-aarch64"
FILE_PATH=entitlements.xml

# XML content for entitlements
XML_CONTENT='<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>com.apple.security.hypervisor</key>
    <true/>
</dict>
</plist>'

# Function to write entitlements file
write_entitlements() {
    echo "Writing entitlements to $TARGET_DIR/$FILE_PATH..."
    mkdir -p "$TARGET_DIR"
    echo "$XML_CONTENT" > "$TARGET_DIR/$FILE_PATH"
}

# Function to sign QEMU binary
sign_qemu() {
    echo "Signing QEMU binary..."
    codesign -s - --entitlements "$TARGET_DIR/$FILE_PATH" --force "$TARGET_DIR/qemu-system-aarch64"
}

# Main script execution
main() {
    echo "Starting entitlements setup..."
    write_entitlements
    sign_qemu
    echo "Entitlements setup completed successfully."
}

main