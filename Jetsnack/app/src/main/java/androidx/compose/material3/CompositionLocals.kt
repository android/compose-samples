package androidx.compose.material3

import androidx.compose.runtime.ProvidableCompositionLocal
import java.lang.reflect.Method

// This file is only because we don't have this yet implemented in Material 3 theming.

// This will not be how it's done when its released :)

val LocalShapes: ProvidableCompositionLocal<Shapes>
    get() = getInternalLocal(
        className = "androidx.compose.material3.ShapesKt",
        methodName = "getLocalShapes",
        readableName = "LocalShapes"
    )

val LocalColorScheme: ProvidableCompositionLocal<ColorScheme>
    get() = getInternalLocal(
        className = "androidx.compose.material3.ColorSchemeKt",
        methodName = "getLocalColorScheme",
        readableName = "LocalColorScheme"
    )

val LocalTypography: ProvidableCompositionLocal<Typography>
    get() = getInternalLocal(
        className = "androidx.compose.material3.TypographyKt",
        methodName = "getLocalTypography",
        readableName = "LocalTypography"
    )

private fun <T> getInternalLocal(className: String, methodName: String, readableName: String): ProvidableCompositionLocal<T> {
    try {
        val clazz = Class.forName(className)
        val method: Method = clazz.getDeclaredMethod(methodName)
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return method.invoke(null) as ProvidableCompositionLocal<T>
    } catch (e: Exception) {
        throw RuntimeException("Failed to access internal $readableName via reflection", e)
    }
}