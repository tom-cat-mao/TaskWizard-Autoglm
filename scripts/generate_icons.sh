#!/bin/bash

# Icon Generation Script for TaskWizard
# Generates all mipmap icons from source Icon.png with alpha channel preservation

set -e  # Exit on error

# Source icon
SOURCE="resources/Icon.png"

# Check if source exists
if [ ! -f "$SOURCE" ]; then
    echo "❌ Error: Source icon not found at $SOURCE"
    exit 1
fi

# Output directories
MDPI="app/src/main/res/mipmap-mdpi"
HDPI="app/src/main/res/mipmap-hdpi"
XHDPI="app/src/main/res/mipmap-xhdpi"
XXHDPI="app/src/main/res/mipmap-xxhdpi"
XXXHDPI="app/src/main/res/mipmap-xxxhdpi"

echo "🎨 Generating launcher icons from $SOURCE..."
echo ""

# Generate icons with alpha channel preservation
echo "📱 Generating mdpi (48x48)..."
convert "$SOURCE" -resize 48x48 -background none -gravity center -extent 48x48 "$MDPI/ic_launcher.png"

echo "📱 Generating hdpi (72x72)..."
convert "$SOURCE" -resize 72x72 -background none -gravity center -extent 72x72 "$HDPI/ic_launcher.png"

echo "📱 Generating xhdpi (96x96)..."
convert "$SOURCE" -resize 96x96 -background none -gravity center -extent 96x96 "$XHDPI/ic_launcher.png"

echo "📱 Generating xxhdpi (144x144)..."
convert "$SOURCE" -resize 144x144 -background none -gravity center -extent 144x144 "$XXHDPI/ic_launcher.png"

echo "📱 Generating xxxhdpi (192x192)..."
convert "$SOURCE" -resize 192x192 -background none -gravity center -extent 192x192 "$XXXHDPI/ic_launcher.png"

echo ""
echo "✅ Icons generated successfully with alpha channel preserved!"
echo ""
echo "📊 Verifying generated icons..."

# Verify alpha channel in generated icons
for density in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
    icon_path="app/src/main/res/mipmap-$density/ic_launcher.png"
    if identify -format "%[channels]" "$icon_path" | grep -q "rgba"; then
        echo "  ✅ $density: RGBA (has alpha channel)"
    else
        echo "  ⚠️  $density: RGB (no alpha channel)"
    fi
done

echo ""
echo "🎉 Done! You can now build and test the app."
