package com.example.presentationmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.presentationmaker.R



val poppins = FontFamily(
    Font(resId = R.font.poppins_bold,
        weight = FontWeight.Bold
    ),

    Font(resId = R.font.poppins_regular,
        weight = FontWeight.Normal
    )
)

val clashGrotesk = FontFamily(
    Font(resId = R.font.clashgrotesk_bold,
        weight = FontWeight.Bold
    ),

    Font(resId = R.font.clashgrotesk_regular,
        weight = FontWeight.Normal
    )
)

val lato = FontFamily(

    Font(resId = R.font.lato_regular,
        weight = FontWeight.Normal
    ),

    Font(resId = R.font.lato_bold,
        weight = FontWeight.Bold
    ),

)

val ubuntu = FontFamily(
    Font(resId = R.font.ubuntu_bold,
        weight = FontWeight.Bold
    ),

    Font(resId = R.font.ubuntu_regular,
        weight = FontWeight.Normal
    ),


)

val lufga = FontFamily(
    Font(resId = R.font.lufga_regular,
        weight = FontWeight.Normal
    ),

    Font(resId = R.font.lufga_black,
        weight = FontWeight.Bold
    )
)

val Typography = Typography(

    headlineSmall = TextStyle(
        fontFamily = clashGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 1.sp,
    ),

    bodyLarge = TextStyle(
        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),

    titleLarge = TextStyle(
        fontFamily = clashGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 1.sp,
    ),

    bodySmall = TextStyle(
        fontFamily = lato,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),

    bodyMedium = TextStyle(
        fontFamily = lato,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,

    ),

    titleSmall = TextStyle(
        fontFamily = lufga,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),

    titleMedium = TextStyle(
        fontFamily = ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
)









