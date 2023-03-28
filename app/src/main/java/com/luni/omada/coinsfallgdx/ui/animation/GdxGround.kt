package com.luni.omada.coinsfallgdx.ui.animation

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.World

class GdxGround(
    val world: World,
    v1X: Float, v1Y: Float, v2X: Float, v2Y: Float,
) {

    val body: Body?

    init {
        val shape = EdgeShape()
        shape.set(v1X, v1Y, v2X, v2Y)
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        body = runCatching {
            world.createBody(bodyDef).also {
                it.createFixture(shape, DENSITY)
            }
        }.getOrNull()
        shape.dispose()
    }

    fun dispose() {
        world.destroyBody(body)
    }

    companion object {
        private const val DENSITY = 1.0f
    }
}