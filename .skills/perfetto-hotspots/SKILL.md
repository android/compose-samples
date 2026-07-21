---
name: perfetto-hotspots
description: Profile an Android application using Perfetto and find performance hotspots.
---

# Perfetto Hotspot Detection

Use this skill to profile an Android application, capture a Perfetto trace, and analyze it to find performance hotspots (such as long layout, measure, or recomposition passes).

---

## 1. Profile & Hotspot Detection

### A. Disable Development Interference
Before running the monkey runner or profiling:
*   **LeakCanary / SDK Testing Activities**: Check if `LeakCanary` or other development tools are enabled in the debug build. They often register `LAUNCHER` activities which the `monkey` tool will randomly launch, preventing it from exercising your app's main flow.
*   **Fix**: Comment out `debugImplementation(libs.leakcanary.android)` in `build.gradle.kts` and reinstall the app.

### B. Target Physical Device & Run Profiling
Prioritize physical Android devices (e.g. Pixel 10 Pro) over emulators to obtain realistic GPU/CPU performance traces.
Run the automated profiling script which automatically detects and targets physical devices, handles cleaning stale traces, starts Perfetto with the correct configuration, runs the monkey runner, and pulls the trace file:
```bash
./.skills/perfetto-hotspots/scripts/run_profiling.sh [package_name] [device_serial]
```
This will save the trace to `./.skills/perfetto-hotspots/scripts/trace.perfetto-trace`.

### C. Analyze Slices
Run the trace analysis script on the captured trace:
```bash
python3 ./.skills/perfetto-hotspots/scripts/analyze_trace.py ./.skills/perfetto-hotspots/scripts/trace.perfetto-trace [package_name]
```
This will print the top performance hotspots (slices taking >15ms). You can optionally pass the package name to filter the results.

To perform a deeper dive into what is causing a specific hotspot (such as a long `Compose:recompose` slice), query its descendants in the trace:
```sql
WITH parent AS (
    SELECT id FROM slice 
    WHERE name = 'Compose:recompose' 
    ORDER BY dur DESC LIMIT 1
)
SELECT d.name, d.dur / 1e6 as dur_ms 
FROM parent p 
JOIN descendant_slice(p.id) d 
ORDER BY d.dur DESC LIMIT 10;
```

Export a report of these for the user to read through in a separate markdown file, as an ordered list with the worst offender at the top.
