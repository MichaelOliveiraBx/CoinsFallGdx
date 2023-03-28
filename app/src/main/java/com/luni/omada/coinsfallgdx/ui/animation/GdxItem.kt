package com.omada.social.dailyrecap.animation

import android.util.SizeF
import com.badlogic.gdx.physics.box2d.Body

interface GdxItem {
    val body: Body?
    val size: SizeF

    fun dispose()
}