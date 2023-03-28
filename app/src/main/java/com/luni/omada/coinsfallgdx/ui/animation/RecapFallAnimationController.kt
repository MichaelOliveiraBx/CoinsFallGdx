package com.omada.social.dailyrecap.animation

interface RecapFallAnimationController {
    suspend fun restartAnimation(data: RecapFallAnimationData)

    suspend fun startAnimation(data: RecapFallAnimationData)

    fun stopAnimation()

    fun setBgAsset(bgAsset: BgAsset)
}