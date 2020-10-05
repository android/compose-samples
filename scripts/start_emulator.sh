#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if ! $ANDROID_HOME/tools/android list avd | grep -q Nexus_5X_API_26; then
    echo "No emulator for screenshot tests found, creating one..."
    $DIR/create_emulator.sh
fi

if $ANDROID_HOME/platform-tools/adb devices -l | grep -q emulator; then
    echo "Emulator already running"
    exit 0
fi

echo "Starting emulator..."
echo "no" | $ANDROID_HOME/emulator/emulator "-avd" "Nexus_5X_API_26" "-no-audio" "-no-boot-anim" &

$DIR/wait_for_emulator.sh

echo "Emulator ready, disabling animations!"

$DIR/disable_animations.sh

echo "Emulator started and ready to rock!"
