# [App Name] Performance Optimization Report

This report documents the performance improvements achieved by applying the `performance-helper` skill to the **[App Name]** application.

We captured baseline metrics, identified Compose/Performance anti-patterns, implemented optimizations, and verified the results.


---

## 📋 Prerequisites & Application Audit Checklist

- ✅ **Latest Version of Jetpack Compose**: Up-to-date Compose runtime, UI, and compiler versions.
- ✅ **Baseline Profile Configured**: Baseline profile module and generator rules included and integrated in app build.
- ✅ **R8 Code & Resource Shrinking**: R8 minification enabled in release build configurations (`isMinifyEnabled = true`).
- ✅ **Development Interference Disabled**: Diagnostic tools (e.g., LeakCanary) disabled/removed from profiling builds.

---

## 📊 Before vs. After Comparison

The table below compares the frame timing and composition metrics before and after the optimizations, measured on a `[Device/Emulator Name]`:

### 1. [Journey Name] (`[benchmarkMethodName]`)

| Metric | Baseline (Before) | Optimized (After) | Improvement | Status |
| :--- | :---: | :---: | :---: | :---: |
| **Frame CPU Duration (P50)** | `[X.X ms]` | `[Y.Y ms]` | `[Z.Z]%` | [Status/Comment] |
| **Frame CPU Duration (P90)** | `[X.X ms]` | `[Y.Y ms]` | 🟢 **[Z.Z]% Faster** (-[W.W] ms) | [Status/Comment] |
| **Frame CPU Duration (P95)** | `[X.X ms]` | `[Y.Y ms]` | 🟢 **[Z.Z]% Faster** (-[W.W] ms) | [Status/Comment] |
| **Frame Overrun (P90)** | `[X.X ms]` | `[Y.Y ms]` | 🟢 **[Z.Z]% Lower** (-[W.W] ms) | [Status/Comment] |
| **Frame Overrun (P95)** | `[X.X ms]` | `[Y.Y ms]` | 🟢 **[Z.Z]% Lower** (-[W.W] ms) | [Status/Comment] |
| **[TraceSection]Count (Median)** | `[X.X]` | `[Y.Y]` | 🟢 **[Z.Z]% Lower** | [Status/Comment] |

> [!IMPORTANT]
> **[Key Takeaway Title]**
> [Explain what these numbers mean for the user experience, e.g., how dropping frame durations below 16.6ms prevents visible stuttering.]

---

## 🛠️ Optimizations Implemented

Based on our analysis, we implemented the following key performance optimizations:

### 1. [Optimization Title, e.g., Model Stability]
* **Change**: [Describe the code change clearly. Provide links to the modified files/classes using markdown file links, e.g. [MyFile.kt](file:///path/to/MyFile.kt)]
* **Impact**: [Explain the technical impact of this change on Compose phases (Composition, Layout, Draw), e.g., allowing Compose to skip recomposition when state is unchanged.]

### 2. [Optimization Title, e.g., Phase Deferral]
* **Change**: [Describe the code change, e.g. converting `Modifier.offset(x)` to `Modifier.offset { x }`]
* **Impact**: [Explain the impact, e.g. deferring state read to layout phase, bypassing composition.]

---

## 🏁 Conclusion

By [summarize the key changes, e.g., enforcing model stability, deferring state reads, and caching computations], we successfully stabilized [App Name]'s performance, slashing the [percentile, e.g. 90th-percentile] frame duration by **[Z.Z]%**.
