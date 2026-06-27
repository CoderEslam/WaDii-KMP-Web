package com.wadii.theme

// Brand color hex strings — used as CSS values in Compose Web HTML.
// The app renders via DOM/CSS (not Skia), so androidx.compose.ui.graphics.Color
// is not available. Use these as attr("style", "color: $Yellow") if needed,
// or prefer Tailwind classes (e.g. text-amber-500) which the index.html already configures.

const val Yellow   = "#FFD300"   // Star gold — primary brand accent
const val Red      = "#BB0000"
const val Hint     = "#6B7E99"   // Dim starlight

// Space backgrounds
const val SpaceVoid    = "#000814"
const val SpaceDeep    = "#020C1B"
const val SpaceSurface = "#0A1930"
const val SpaceCard    = "#0D1F3C"

// Glassmorphism
const val GlassWhite     = "rgba(255,255,255,0.10)"
const val GlassBorder    = "rgba(255,255,255,0.20)"
const val GlassHighlight = "rgba(255,255,255,0.25)"

// Text
const val StarWhite  = "#E8F0FE"
const val StarSilver = "#C8D6E8"
const val HintColor  = "#6B7E99"

// Semantic accents
const val NebulaBlue  = "#1E40AF"
const val NebulaPurple = "#7C3AED"
const val CosmicTeal  = "#0891B2"
