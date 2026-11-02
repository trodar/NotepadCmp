package com.trodar.domain.model

import notead.shared.generated.resources.Res
import notead.shared.generated.resources.black1
import notead.shared.generated.resources.black2
import notead.shared.generated.resources.black3
import notead.shared.generated.resources.black4
import notead.shared.generated.resources.black5
import notead.shared.generated.resources.black6
import notead.shared.generated.resources.blue1
import notead.shared.generated.resources.blue2
import notead.shared.generated.resources.blue3
import notead.shared.generated.resources.blue4
import notead.shared.generated.resources.blue5
import notead.shared.generated.resources.blue6
import notead.shared.generated.resources.green1
import notead.shared.generated.resources.green2
import notead.shared.generated.resources.green3
import notead.shared.generated.resources.green4
import notead.shared.generated.resources.green5
import notead.shared.generated.resources.green6
import notead.shared.generated.resources.pink1
import notead.shared.generated.resources.pink2
import notead.shared.generated.resources.pink3
import notead.shared.generated.resources.pink4
import notead.shared.generated.resources.pink5
import notead.shared.generated.resources.pink6
import notead.shared.generated.resources.purple1
import notead.shared.generated.resources.purple2
import notead.shared.generated.resources.purple3
import notead.shared.generated.resources.purple4
import notead.shared.generated.resources.purple5
import notead.shared.generated.resources.purple6
import notead.shared.generated.resources.yellow1
import notead.shared.generated.resources.yellow2
import notead.shared.generated.resources.yellow3
import notead.shared.generated.resources.yellow4
import notead.shared.generated.resources.yellow5
import notead.shared.generated.resources.yellow6
import org.jetbrains.compose.resources.DrawableResource

enum class AppTheme(val displayName: String) {
    GREEN("Зелёная"),
    DARK("Тёмная"),
    PINK("Розовая"),
    PURPLE("Фиолетовая"),
    BLUE("Голубая"),
    YELLOW("Жёлтая")
}

fun AppTheme.getBackgroundResource(index: Int): DrawableResource? {
    if (index !in 1..6) return null
    return when (this) {
        AppTheme.GREEN -> when (index) {
            1 -> Res.drawable.green1
            2 -> Res.drawable.green2
            3 -> Res.drawable.green3
            4 -> Res.drawable.green4
            5 -> Res.drawable.green5
            6 -> Res.drawable.green6
            else -> null
        }
        AppTheme.DARK -> when (index) {
            1 -> Res.drawable.black1
            2 -> Res.drawable.black2
            3 -> Res.drawable.black3
            4 -> Res.drawable.black4
            5 -> Res.drawable.black5
            6 -> Res.drawable.black6
            else -> null
        }
        AppTheme.PINK -> when (index) {
            1 -> Res.drawable.pink1
            2 -> Res.drawable.pink2
            3 -> Res.drawable.pink3
            4 -> Res.drawable.pink4
            5 -> Res.drawable.pink5
            6 -> Res.drawable.pink6
            else -> null
        }
        AppTheme.PURPLE -> when (index) {
            1 -> Res.drawable.purple1
            2 -> Res.drawable.purple2
            3 -> Res.drawable.purple3
            4 -> Res.drawable.purple4
            5 -> Res.drawable.purple5
            6 -> Res.drawable.purple6
            else -> null
        }
        AppTheme.BLUE -> when (index) {
            1 -> Res.drawable.blue1
            2 -> Res.drawable.blue2
            3 -> Res.drawable.blue3
            4 -> Res.drawable.blue4
            5 -> Res.drawable.blue5
            6 -> Res.drawable.blue6
            else -> null
        }
        AppTheme.YELLOW -> when (index) {
            1 -> Res.drawable.yellow1
            2 -> Res.drawable.yellow2
            3 -> Res.drawable.yellow3
            4 -> Res.drawable.yellow4
            5 -> Res.drawable.yellow5
            6 -> Res.drawable.yellow6
            else -> null
        }
    }
}
