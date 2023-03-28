package com.luni.omada.coinsfallgdx.ui.animation

import android.util.Size
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.ScreenUtils
import com.omada.social.dailyrecap.animation.BgAsset
import com.omada.social.dailyrecap.animation.GdxItem
import com.omada.social.dailyrecap.animation.RecapFallAnimationController
import com.omada.social.dailyrecap.animation.RecapFallAnimationData
import kotlinx.coroutines.*
import kotlin.random.Random

class RecapFallAnimationAdapter : ApplicationAdapter(), RecapFallAnimationController {

    companion object {
        private const val VELOCITY_Y = -30f
        private const val VELOCITY_X = 0f

        private const val TIME_STEP = 1 / 60f
        private const val VELOCITY_ITERATIONS = 1
        private const val POSITION_ITERATIONS = 1

        const val PIXEL_PER_METER = 32f

        private const val ROLL_COEFF = 0.5f
        private const val PITCH_GRAVITY_COEFF = 0.4f

        val gdxConfiguration by lazy {
            AndroidApplicationConfiguration().apply {
                useGyroscope = false
                useAccelerometer = false
                useCompass = true
                useRotationVectorSensor = true
                disableAudio = true
            }
        }

        private const val WinningAssetPath: String = "recap-win.jpg"
        private const val LoseAssetPath: String = "recap-loose.jpg"
        private const val FullAssetPath: String = "recap-full.jpg"
        private const val FullPerfectPath: String = "recap_full_perfect.jpg"

        // TODO put the good path
        private const val CancelAssetPath: String = "recap-full.jpg"
        private const val CoinAssetPath: String = "coin.png"
        private const val PoopAssetPath: String = "poop.png"
    }

    private var _world: World? = null
    private val players = mutableListOf<GdxItem>()
    private val grounds = mutableListOf<GdxGround>()
    private lateinit var batch: SpriteBatch
    private lateinit var currentItemTexture: Texture
    private lateinit var itemCoinTexture: Texture
    private lateinit var itemPoopTexture: Texture

    private lateinit var bgWinningTexture: Texture
    private lateinit var bgLoseTexture: Texture
    private lateinit var bgFullTexture: Texture
    private lateinit var bgFullPerfectTexture: Texture
    private lateinit var bgCancelTexture: Texture

    private lateinit var currentBgTexture: Texture

    private var lastScreenSize: Size? = null
    private val startingY: Float
        get() = lastScreenSize?.height.orZero() / PIXEL_PER_METER + 3f

    private val startingX: Float
        get() {
            val width = (lastScreenSize?.width.orZero() / PIXEL_PER_METER).toInt()
            return Random.nextInt(3, width - 3).toFloat()
        }

    override fun create() {
        batch = SpriteBatch()
        _world = runCatching { World(Vector2(VELOCITY_X, VELOCITY_Y), false) }
            .getOrNull()

        bgWinningTexture = Texture(WinningAssetPath)
        bgLoseTexture = Texture(LoseAssetPath)
        bgFullTexture = Texture(FullAssetPath)
        bgFullPerfectTexture = Texture(FullPerfectPath)
        bgCancelTexture = Texture(CancelAssetPath)
        currentBgTexture = bgWinningTexture

        itemCoinTexture = Texture(CoinAssetPath)
        itemPoopTexture = Texture(PoopAssetPath)
        currentItemTexture = itemCoinTexture
    }

    private var isRunning = false
    private var lastRender = false

    private var animationJob: Job? = null
    override suspend fun restartAnimation(data: RecapFallAnimationData) {
        stopAnimationInternal(lastRenderer = false)
        startAnimation(data)
    }

    override suspend fun startAnimation(data: RecapFallAnimationData) = coroutineScope {
        val world = _world ?: return@coroutineScope

        if (animationJob == null) {
            animationJob = launch {
                setItemAsset(data.isWin)
                startUpdateGravity()

                isRunning = true
                data.groups.forEach {
                    Gdx.app.postRunnable {
                        players.addAll(
                            List(it.coins) {
                                if (data.isWin)
                                    GdxCoins(
                                        world = world,
                                        y = startingY,
                                        x = startingX,
                                    )
                                else
                                    GdxPoop(
                                        world = world,
                                        y = startingY,
                                        x = startingX,
                                    )
                            }
                        )
                    }
                    delay(it.delayMs)
                    if (!isActive) return@launch
                }
            }
        }
    }

    private fun CoroutineScope.startUpdateGravity() {
        val world = _world ?: return

        launch(Dispatchers.Default) {
            while (true) {
                delay(100)
                val gravityX = Gdx.input.roll * ROLL_COEFF
                val gravityY = Gdx.input.pitch * PITCH_GRAVITY_COEFF
                Gdx.app.postRunnable {
                    world.gravity = Vector2(gravityX, gravityY)
                }
            }
        }
    }

    override fun stopAnimation() {
        stopAnimationInternal(lastRenderer = true)
    }

    private fun stopAnimationInternal(lastRenderer: Boolean = true) {
        Gdx.app.postRunnable {
            players.forEach { it.dispose() }
            players.clear()
            lastRender = lastRenderer
        }
        animationJob?.cancel()
        animationJob = null
    }

    override fun setBgAsset(bgAsset: BgAsset) {
        Gdx.app.postRunnable {
            currentBgTexture = when (bgAsset) {
                BgAsset.Win -> bgWinningTexture
                BgAsset.Lose -> bgLoseTexture
                BgAsset.Full -> bgFullTexture
                BgAsset.FullPerfect -> bgFullPerfectTexture
                BgAsset.Cancel -> bgCancelTexture
            }
        }
    }

    fun setItemAsset(isWin: Boolean) {
        Gdx.app.postRunnable {
            currentItemTexture = if (isWin) itemCoinTexture else itemPoopTexture
        }
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 0f)
        if (isRunning) {
            _world?.step(
                TIME_STEP,
                VELOCITY_ITERATIONS,
                POSITION_ITERATIONS
            )
        }

        batch.begin()
        batch.draw(
            currentBgTexture,
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )

        if (isRunning) {
            players.toMutableList().forEach {
                val body = it.body ?: return@forEach

                val rotation = body.angle * MathUtils.radiansToDegrees
                val x = body.position.x * PIXEL_PER_METER - (it.size.width) / 2
                val y = body.position.y * PIXEL_PER_METER - (it.size.height) / 2
                batch.draw(
                    currentItemTexture,
                    x,
                    y,
                    it.size.width / 2,
                    it.size.height / 2,
                    it.size.width,
                    it.size.height,
                    1f,
                    1f,
                    rotation,
                    0,
                    0,
                    currentItemTexture.width,
                    currentItemTexture.height,
                    false,
                    false,
                )
            }
        }
        batch.end()

        if (lastRender) {
            lastRender = false
            isRunning = false
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)

        val newScreeSize = Size(width, height)
        if (lastScreenSize == newScreeSize) return
        lastScreenSize = newScreeSize

        val widthMeter = width / PIXEL_PER_METER
        val heightMeter = height / PIXEL_PER_METER + 8f

        Gdx.app.postRunnable {
            val world = _world ?: return@postRunnable

            grounds.forEach {
                it.dispose()
            }
            grounds.clear()
            grounds.addAll(
                listOf(
                    GdxGround(
                        world,
                        0f, 0f, widthMeter, 0f,
                    ),
                    GdxGround(
                        world,
                        widthMeter, heightMeter, widthMeter, 0f,
                    ),
                    GdxGround(
                        world,
                        0f, heightMeter, 0f, 0f,
                    ),
                    GdxGround(
                        world,
                        0f, heightMeter, widthMeter, heightMeter,
                    )
                )
            )
        }

    }

    override fun dispose() {
        players.forEach { it.dispose() }
        grounds.forEach { it.dispose() }

        itemCoinTexture.dispose()
        itemPoopTexture.dispose()

        bgWinningTexture.dispose()
        bgLoseTexture.dispose()

        batch.dispose()
        _world?.dispose()
    }
}

private fun Int?.orZero() = this ?: 0