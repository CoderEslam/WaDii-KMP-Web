package com.wadii.theme

import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element

/**
 * Web equivalent of FontFamily.
 * Each font maps to its Tailwind class name (registered in index.html tailwind config).
 */
object AppFont {
    const val inter   = "font-sans"     // Inter — default body font
    const val poppins = "font-poppins"  // Poppins — loaded from fonts/Poppins-*.woff2
}

/** Apply a font to any Compose Web element via attrs { font(AppFont.poppins) } */
fun AttrsScope<out Element>.font(appFont: String) = classes(appFont)
