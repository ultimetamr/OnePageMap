package com.pico.swan.onepagemap.ui.components

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.Color

object RoadmapPalette {
    val Accent = Color(0xFFF2B84B) // design-style: fixed-figma-color visual-system-spec accent
    val Paper = Color(0xFFF5E9D2) // design-style: fixed-figma-color visual-system-spec paper
    val PaperMuted = Color(0xFFE3D5BC) // design-style: fixed-figma-color visual-system-spec paper-muted
    val Brand = Color(0xFF315B4C) // design-style: fixed-figma-color visual-system-spec brand
    val Ink = Color(0xFF20312B) // design-style: fixed-figma-color visual-system-spec ink
    val Risk = Color(0xFFC8443A) // design-style: fixed-figma-color visual-system-spec risk
    val RiskDark = Color(0xFF7B241E) // design-style: fixed-figma-color visual-system-spec risk-edge
    val Terrain = Color(0xFF203029) // design-style: fixed-figma-color visual-system-spec terrain
    val TerrainLine = Color(0xFF587064) // design-style: fixed-figma-color visual-system-spec terrain-line
    val Path = Color(0xFFD8C49F) // design-style: fixed-figma-color visual-system-spec path
    val NotStarted = Color(0xFF8A8175) // design-style: fixed-figma-color semantic not-started
    val InProgress = Color(0xFF2F77C4) // design-style: fixed-figma-color semantic in-progress
    val Completed = Color(0xFF2E8B57) // design-style: fixed-figma-color semantic completed
    val White = Color(0xFFFFFFFF) // design-style: fixed-figma-color semantic high-contrast
    val Shadow = Color(0x55000000) // design-style: fixed-figma-color decorative depth shadow
}

val RiskStoneShape = GenericShape { size, _ ->
    moveTo(size.width * .50f, 0f)
    lineTo(size.width * .88f, size.height * .28f)
    lineTo(size.width, size.height * .78f)
    lineTo(size.width * .72f, size.height)
    lineTo(size.width * .18f, size.height * .92f)
    lineTo(0f, size.height * .55f)
    lineTo(size.width * .16f, size.height * .12f)
    close()
}
