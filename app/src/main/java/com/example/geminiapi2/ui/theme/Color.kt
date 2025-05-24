package com.example.geminiapi2.ui.theme
import androidx.compose.ui.graphics.Color

// Figma Colors based on node 1:30912

// Primary
val primaryBrand = Color(0xFF0E33F3) // Main Primary
val primaryLightFigma = Color(0xFFA0CCF8)
val primaryDarkFigma = Color(0xFF57A4F2)

// Secondary
val secondaryGreenDark = Color(0xFF46987F)
val secondaryGreenLight = Color(0xFF9BF9D3)
val secondaryBlueLight = Color(0xFFD1F4FF)
val secondaryYellowLight = Color(0xFFFCE588)
val secondaryRedLight = Color(0xFFFFD7D7)
val secondarySoftOrange = Color(0xFFFDE1CE)
val secondaryDarkBrand = Color(0xFF395EEC) // Main Secondary
val secondaryRedDark = Color(0xFFC62B30)
val secondaryBrown = Color(0xFFB16F05)

// Neutrals
val neutralDark1 = Color(0xFF1F2933) // Main OnBackground/OnSurface
val neutralDark2 = Color(0xFF242D35) // Main Text/On... colors
val neutralGrey1 = Color(0xFF6B7580)
val neutralGrey2 = Color(0xFF9BA1A8)
val neutralGrey3 = Color(0xFFB0B8BF)
val neutralSoftGrey1 = Color(0xFFDCDFE3) // Main Outline
val neutralSoftGrey2 = Color(0xFFEBEEF0) // Main SurfaceVariant
val neutralSoftGrey3 = Color(0xFFFAFAFB) // Main Surface
val neutralWhite = Color(0xFFFFFFFF) // Main Background

// System
val systemRed = Color(0xFFEF4E4E) // Main Error
val systemYellow = Color(0xFFFBBE4A)
val systemGreen = Color(0xFF3EBD93)
val systemBlue = Color(0xFF37ABFF)


// --- Material 3 Mapping ---

// Light Theme Colors
val primaryLight = primaryBrand
val onPrimaryLight = neutralWhite // Text on Primary
val primaryContainerLight = primaryLightFigma // Lighter primary variant
val onPrimaryContainerLight = neutralDark2 // Text on lighter primary
val secondaryLight = secondaryDarkBrand // Main Secondary
val onSecondaryLight = neutralWhite // Text on Secondary
val secondaryContainerLight = secondaryBlueLight // Lighter secondary variant
val onSecondaryContainerLight = neutralDark2 // Text on lighter secondary
val tertiaryLight = secondaryGreenDark // Using Green as Tertiary
val onTertiaryLight = neutralWhite // Text on Tertiary
val tertiaryContainerLight = secondaryGreenLight // Lighter tertiary variant
val onTertiaryContainerLight = neutralDark2 // Text on lighter tertiary
val errorLight = systemRed
val onErrorLight = neutralWhite // Text on Error
val errorContainerLight = secondaryRedLight // Lighter error variant
val onErrorContainerLight = neutralDark2 // Text on lighter error
val backgroundLight = neutralWhite
val onBackgroundLight = neutralDark1
val surfaceLight = neutralSoftGrey3
val onSurfaceLight = neutralDark1
val surfaceVariantLight = neutralSoftGrey2
val onSurfaceVariantLight = neutralGrey1 // Darker text for variant surface
val outlineLight = neutralSoftGrey1
val outlineVariantLight = neutralSoftGrey1.copy(alpha = 0.6f) // Slightly dimmer outline
val scrimLight = neutralDark1.copy(alpha = 0.6f) // Scrim color
val inverseSurfaceLight = neutralDark1 // Inverse surface is dark
val inverseOnSurfaceLight = neutralWhite // Text on inverse surface is light
val inversePrimaryLight = primaryLightFigma // Inverse primary
val surfaceDimLight = neutralSoftGrey2 // Example dim surface
val surfaceBrightLight = neutralWhite // Example bright surface
val surfaceContainerLowestLight = neutralWhite
val surfaceContainerLowLight = neutralSoftGrey3
val surfaceContainerLight = neutralSoftGrey2
val surfaceContainerHighLight = neutralSoftGrey1
val surfaceContainerHighestLight = neutralGrey3

// --- Dark Theme Colors (Derived/Assumed as Figma doesn't specify dark theme) ---
// We'll invert/adjust the light theme colors

val primaryDark = primaryLightFigma // Lighter Blue for Dark theme Primary
val onPrimaryDark = neutralDark2 // Dark text on light Primary
val primaryContainerDark = primaryBrand.copy(alpha = 0.3f) // Darker container
val onPrimaryContainerDark = neutralSoftGrey3 // Light text on dark container
val secondaryDark = secondaryBlueLight // Light Blue for Dark theme Secondary
val onSecondaryDark = neutralDark2 // Dark text on light Secondary
val secondaryContainerDark = secondaryDarkBrand.copy(alpha = 0.3f) // Darker container
val onSecondaryContainerDark = neutralSoftGrey3 // Light text on dark container
val tertiaryDark = secondaryGreenLight // Light Green for Dark theme Tertiary
val onTertiaryDark = neutralDark2 // Dark text on light Tertiary
val tertiaryContainerDark = secondaryGreenDark.copy(alpha = 0.3f) // Darker container
val onTertiaryContainerDark = neutralSoftGrey3 // Light text on dark container
val errorDark = secondaryRedLight // Lighter Red for Dark theme Error
val onErrorDark = neutralDark2 // Dark text on light Error
val errorContainerDark = systemRed.copy(alpha = 0.3f) // Darker container
val onErrorContainerDark = neutralSoftGrey3 // Light text on dark container
val backgroundDark = neutralDark1
val onBackgroundDark = neutralSoftGrey3
val surfaceDark = neutralDark2 // Darker surface for dark theme
val onSurfaceDark = neutralSoftGrey3
val surfaceVariantDark = neutralGrey1 // Darker variant surface
val onSurfaceVariantDark = neutralSoftGrey1 // Lighter text on dark variant
val outlineDark = neutralGrey1
val outlineVariantDark = neutralGrey1.copy(alpha = 0.6f)
val scrimDark = Color.Black.copy(alpha = 0.6f)
val inverseSurfaceDark = neutralSoftGrey3 // Inverse surface is light
val inverseOnSurfaceDark = neutralDark1 // Text on inverse surface is dark
val inversePrimaryDark = primaryBrand // Inverse primary
val surfaceDimDark = neutralDark1
val surfaceBrightDark = neutralGrey1
val surfaceContainerLowestDark = Color.Black
val surfaceContainerLowDark = neutralDark1
val surfaceContainerDark = neutralDark2
val surfaceContainerHighDark = neutralGrey1
val surfaceContainerHighestDark = neutralGrey2

// ---- Old colors (kept for reference, can be removed) ----
// val primaryLight = Color(0xFF55933F)
// ... (rest of the old color definitions) ...

val primaryLightMediumContrast = Color(0xFF133665)
val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
val primaryContainerLightMediumContrast = Color(0xFF506DA0)
val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val secondaryLightMediumContrast = Color(0xFF2E3647)
val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
val secondaryContainerLightMediumContrast = Color(0xFF646D80)
val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryLightMediumContrast = Color(0xFF452E4A)
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFF7F6484)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF740006)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFCF2C27)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFF9F9FF)
val onBackgroundLightMediumContrast = Color(0xFF191C20)
val surfaceLightMediumContrast = Color(0xFFF9F9FF)
val onSurfaceLightMediumContrast = Color(0xFF0F1116)
val surfaceVariantLightMediumContrast = Color(0xFFE0E2EC)
val onSurfaceVariantLightMediumContrast = Color(0xFF33363E)
val outlineLightMediumContrast = Color(0xFF4F525A)
val outlineVariantLightMediumContrast = Color(0xFF6A6D75)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF2E3036)
val inverseOnSurfaceLightMediumContrast = Color(0xFFF0F0F7)
val inversePrimaryLightMediumContrast = Color(0xFFAAC7FF)
val surfaceDimLightMediumContrast = Color(0xFFC5C6CD)
val surfaceBrightLightMediumContrast = Color(0xFFF9F9FF)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFF3F3FA)
val surfaceContainerLightMediumContrast = Color(0xFFE7E8EE)
val surfaceContainerHighLightMediumContrast = Color(0xFFDCDCE3)
val surfaceContainerHighestLightMediumContrast = Color(0xFFD1D1D8)

val primaryLightHighContrast = Color(0xFF052659)
val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast = Color(0xFF2A497A)
val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
val secondaryLightHighContrast = Color(0xFF232C3D)
val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast = Color(0xFF41495B)
val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
val tertiaryLightHighContrast = Color(0xFF3A2440)
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF59405E)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF600004)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF98000A)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFF9F9FF)
val onBackgroundLightHighContrast = Color(0xFF191C20)
val surfaceLightHighContrast = Color(0xFFF9F9FF)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFE0E2EC)
val onSurfaceVariantLightHighContrast = Color(0xFF000000)
val outlineLightHighContrast = Color(0xFF292C33)
val outlineVariantLightHighContrast = Color(0xFF464951)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF2E3036)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFAAC7FF)
val surfaceDimLightHighContrast = Color(0xFFB8B8BF)
val surfaceBrightLightHighContrast = Color(0xFFF9F9FF)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFF0F0F7)
val surfaceContainerLightHighContrast = Color(0xFFE2E2E9)
val surfaceContainerHighLightHighContrast = Color(0xFFD3D4DB)
val surfaceContainerHighestLightHighContrast = Color(0xFFC5C6CD)

val primaryDarkMediumContrast = Color(0xFFCDDDFF)
val onPrimaryDarkMediumContrast = Color(0xFF002551)
val primaryContainerDarkMediumContrast = Color(0xFF7491C7)
val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
val secondaryDarkMediumContrast = Color(0xFFD4DCF2)
val onSecondaryDarkMediumContrast = Color(0xFF1D2636)
val secondaryContainerDarkMediumContrast = Color(0xFF8891A5)
val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
val tertiaryDarkMediumContrast = Color(0xFFF3D2F7)
val onTertiaryDarkMediumContrast = Color(0xFF331D39)
val tertiaryContainerDarkMediumContrast = Color(0xFFA487A9)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFD2CC)
val onErrorDarkMediumContrast = Color(0xFF540003)
val errorContainerDarkMediumContrast = Color(0xFFFF5449)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF111318)
val onBackgroundDarkMediumContrast = Color(0xFFE2E2E9)
val surfaceDarkMediumContrast = Color(0xFF111318)
val onSurfaceDarkMediumContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkMediumContrast = Color(0xFF44474E)
val onSurfaceVariantDarkMediumContrast = Color(0xFFDADCE6)
val outlineDarkMediumContrast = Color(0xFFAFB2BB)
val outlineVariantDarkMediumContrast = Color(0xFF8E9099)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFE2E2E9)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF282A2F)
val inversePrimaryDarkMediumContrast = Color(0xFF294878)
val surfaceDimDarkMediumContrast = Color(0xFF111318)
val surfaceBrightDarkMediumContrast = Color(0xFF43444A)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF06070C)
val surfaceContainerLowDarkMediumContrast = Color(0xFF1B1E22)
val surfaceContainerDarkMediumContrast = Color(0xFF26282D)
val surfaceContainerHighDarkMediumContrast = Color(0xFF313238)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF3C3E43)

val primaryDarkHighContrast = Color(0xFFEBF0FF)
val onPrimaryDarkHighContrast = Color(0xFF000000)
val primaryContainerDarkHighContrast = Color(0xFFA6C3FC)
val onPrimaryContainerDarkHighContrast = Color(0xFF000B20)
val secondaryDarkHighContrast = Color(0xFFEBF0FF)
val onSecondaryDarkHighContrast = Color(0xFF000000)
val secondaryContainerDarkHighContrast = Color(0xFFBAC3D8)
val onSecondaryContainerDarkHighContrast = Color(0xFF030B1A)
val tertiaryDarkHighContrast = Color(0xFFFFE9FF)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFFD8B8DC)
val onTertiaryContainerDarkHighContrast = Color(0xFF16041D)
val errorDarkHighContrast = Color(0xFFFFECE9)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFAEA4)
val onErrorContainerDarkHighContrast = Color(0xFF220001)
val backgroundDarkHighContrast = Color(0xFF111318)
val onBackgroundDarkHighContrast = Color(0xFFE2E2E9)
val surfaceDarkHighContrast = Color(0xFF111318)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF44474E)
val onSurfaceVariantDarkHighContrast = Color(0xFFFFFFFF)
val outlineDarkHighContrast = Color(0xFFEEEFF9)
val outlineVariantDarkHighContrast = Color(0xFFC0C2CC)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFE2E2E9)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF294878)
val surfaceDimDarkHighContrast = Color(0xFF111318)
val surfaceBrightDarkHighContrast = Color(0xFF4E5056)
val surfaceContainerLowestDarkHighContrast = Color(0xFF000000)
val surfaceContainerLowDarkHighContrast = Color(0xFF1D2024)
val surfaceContainerDarkHighContrast = Color(0xFF2E3036)
val surfaceContainerHighDarkHighContrast = Color(0xFF393B41)
val surfaceContainerHighestDarkHighContrast = Color(0xFF45474C)







