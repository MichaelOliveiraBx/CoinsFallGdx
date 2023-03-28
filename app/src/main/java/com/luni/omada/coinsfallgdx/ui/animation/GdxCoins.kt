package com.luni.omada.coinsfallgdx.ui.animation

import android.util.SizeF
import com.badlogic.gdx.physics.box2d.*
import com.omada.social.dailyrecap.animation.GdxItem
import kotlin.random.Random

class GdxCoins(
    private val world: World,
    x: Float = Random.nextInt(3, 30).toFloat(),
    y: Float = PLAYER_START_Y,
    radius: Float = Random.nextDouble(1.4, 3.3).toFloat(),
    velocityX: Float = Random.nextInt(-15, 15).toFloat(),
    velocityY: Float = Random.nextInt(-55, -30).toFloat(),
) : GdxItem {

    private val coeff = 90f

    override val body: Body?
    override val size: SizeF = SizeF(radius * coeff, radius * coeff)

    init {
        val bdef = BodyDef()
        bdef.type = BodyDef.BodyType.DynamicBody
        bdef.position[x] = y
        bdef.linearVelocity.x = velocityX
        bdef.linearVelocity.y = velocityY
        val shape = CircleShape()
        shape.radius = radius

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = 1f
        fixtureDef.restitution = 0.2f

        body = runCatching {
            world.createBody(bdef).also {
                it.createFixture(fixtureDef)
            }
        }.getOrNull()
    }

    override fun dispose() {
        world.destroyBody(body)
    }

    companion object {
        private const val PLAYER_DENSITY = 1.0f
        private const val PLAYER_START_Y = 70f

        private const val RESTITUTION: Float = 0.0f
    }
}