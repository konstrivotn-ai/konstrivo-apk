package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Konstrivo Brand Colors - High Precision Construction Aesthetic
// Reference board (DESIGN_REFERENCES/app_design.png): bright construction
// yellow accents on deep navy. Primary Yellow #FFC72C replaces the legacy
// amber #F59E0B so buttons / FABs / chips match the reference collage.
val KonstrivoNavy = Color(0xFF0F172A)      // Target Background
val KonstrivoDarkNavy = Color(0xFF070C18)
val KonstrivoAmber = Color(0xFFFBBF24)     // Accent tint (icons/borders) — brightened to match reference yellow family
val KonstrivoGold = Color(0xFFFFC72C)      // R1 Design System Primary Yellow #FFC72C (reference buttons/FAB)
val KonstrivoBlue = Color(0xFF2563EB)
val KonstrivoCyan = Color(0xFF0284C7)
val KonstrivoEmerald = Color(0xFF10B981)
val KonstrivoSlate = Color(0xFF64748B)

// PHASE 2.1 — Functional color tokens (semantic aliases over the brand palette).
// Keep usage meaningful: success/warning/error/info map to UI states only.
val KonstrivoSuccess = Color(0xFF10B981) // = KonstrivoEmerald
val KonstrivoWarning = Color(0xFFF59E0B) // amber warning (semantic, not brand accent)
val KonstrivoError = Color(0xFFDC2626)
val KonstrivoInfo = Color(0xFF2563EB)    // = KonstrivoBlue

// Light Theme Palette
val ConstructionNavyPrimary = Color(0xFF0F172A)
val ConstructionAmberSecondary = Color(0xFFFBBF24)
val ConstructionGold = Color(0xFFFFC72C)
val ConstructionBlue = Color(0xFF2563EB)
val ConstructionSurfaceLight = Color(0xFFF8FAFC)
val ConstructionCardLight = Color(0xFFFFFFFF)
val ConstructionBorderLight = Color(0xFFE2E8F0)
val ConstructionTextPrimaryLight = Color(0xFF0F172A)
val ConstructionTextSecondaryLight = Color(0xFF475569)

// Dark Theme Palette
val ConstructionNavyDark = Color(0xFF070D1E)
val ConstructionCardDark = Color(0xFF131D31)
val ConstructionSurfaceDark = Color(0xFF0A1124)
val ConstructionBorderDark = Color(0xFF1E293B)
val ConstructionAmberLight = Color(0xFFFBBF24)
val ConstructionBlueLight = Color(0xFF60A5FA)
val ConstructionTextPrimaryDark = Color(0xFFF8FAFC)
val ConstructionTextSecondaryDark = Color(0xFF94A3B8)

// Trade Categories Colors
val PlacoTradeColor = Color(0xFF3B82F6)
val PeintureTradeColor = Color(0xFF8B5CF6)
val MaconnerieTradeColor = Color(0xFFEA580C)
val CarrelageTradeColor = Color(0xFF0D9488)
val MenuiserieTradeColor = Color(0xFFB45309)
val CrepissageTradeColor = Color(0xFF6366F1)

