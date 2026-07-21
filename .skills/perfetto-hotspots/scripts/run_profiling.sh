#!/bin/bash
set -e

# Get the directory of this script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Read package name from argument, default to com.android.developers.androidify
PACKAGE_NAME="${1:-com.android.developers.androidify}"
DEVICE_SERIAL="${2:-$ANDROID_SERIAL}"

if [ -z "$DEVICE_SERIAL" ]; then
    # Auto-detect physical device (non-emulator)
    PHYSICAL_DEVICE=$(adb devices | grep -v 'emulator' | grep 'device$' | awk '{print $1}' | head -n 1)
    if [ -n "$PHYSICAL_DEVICE" ]; then
        DEVICE_SERIAL="$PHYSICAL_DEVICE"
        echo "Auto-detected physical device: $DEVICE_SERIAL"
    else
        echo "No physical device found; defaulting to available adb target."
    fi
fi

if [ -n "$DEVICE_SERIAL" ]; then
    ADB="adb -s $DEVICE_SERIAL"
else
    ADB="adb"
fi

echo "Using target device: ${DEVICE_SERIAL:-default}"

echo "Cleaning up stale trace files..."
$ADB shell "rm -f /data/misc/perfetto-traces/trace.perfetto-trace"

echo "Starting Perfetto trace in background for package: $PACKAGE_NAME..."
# Replace placeholder in template and pipe to adb to bypass SELinux restrictions
sed "s/{{APP_PACKAGE}}/$PACKAGE_NAME/g" "$DIR/perfetto.config" | $ADB shell "perfetto -c - --txt -o /data/misc/perfetto-traces/trace.perfetto-trace" &
PERFETTO_PID=$!

# Sleep a moment to let Perfetto start
sleep 2

echo "Running monkey runner on $PACKAGE_NAME..."
$ADB shell monkey -p "$PACKAGE_NAME" --pct-syskeys 0 -v 500

echo "Waiting for Perfetto trace to complete..."
wait $PERFETTO_PID || true

echo "Pulling trace file..."
$ADB pull /data/misc/perfetto-traces/trace.perfetto-trace "$DIR/trace.perfetto-trace"

echo "Profiling complete. Trace saved to $DIR/trace.perfetto-trace"
