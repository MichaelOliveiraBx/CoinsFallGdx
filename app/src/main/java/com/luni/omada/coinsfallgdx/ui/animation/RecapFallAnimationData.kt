package com.omada.social.dailyrecap.animation

data class RecapFallAnimationGroupData(
    val coins: Int,
    val delayMs: Long = 300,
)

data class RecapFallAnimationData(
    val groups: List<RecapFallAnimationGroupData> = emptyList(),
    val isWin: Boolean = false,
)

enum class BgAsset {
    Win,
    Lose,
    Full,
    FullPerfect,
    Cancel,
}