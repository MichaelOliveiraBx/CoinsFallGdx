package com.omada.social.dailyrecap.animation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont

class GdxText(
    filename: String
) {

    private lateinit var font: BitmapFont

    init {
        val texture = Texture(filename)
        val width = 4f * Gdx.graphics.ppcX
        val ratio = width / texture.width
        val height = texture.height * ratio
        val yPosition = 0.77f

//        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("abc_gravity_normal.ttf"))
//        font = fontGenerator.generateFont(
//            FreeTypeFontGenerator.FreeTypeFontParameter().apply {
//                size = 200
//            }
//        )

        //TODO How draw it in the renderer
//        font.draw(batch, "GAME", 460f, Gdx.graphics.height * yPosition)

    }
}