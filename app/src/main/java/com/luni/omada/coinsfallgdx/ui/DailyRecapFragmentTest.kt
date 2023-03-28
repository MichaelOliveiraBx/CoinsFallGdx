package com.omada.social.dailyrecap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.luni.omada.coinsfallgdx.databinding.RecapLayoutBinding
import com.luni.omada.coinsfallgdx.ui.animation.RecapFallAnimationAdapter
import com.omada.social.dailyrecap.animation.*
import kotlinx.coroutines.launch

class DailyRecapFragmentTest : AndroidFragmentApplication() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = RecapLayoutBinding.inflate(layoutInflater).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = RecapLayoutBinding.bind(view)

        val gdxAdapter = RecapFallAnimationAdapter()
        binding.frameLayout.addView(
            initializeForView(
                gdxAdapter,
                RecapFallAnimationAdapter.gdxConfiguration
            )
        )

        val controller: RecapFallAnimationController = gdxAdapter
        binding.composeView.setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .fillMaxHeight(0.4f)
                            .align(Alignment.Center)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(0.9f)
                                .align(Alignment.Center),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(
                                        color = Color(0xFF13C9B1),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        lifecycleScope.launch {
                                            controller.setBgAsset(BgAsset.Full)
                                            controller.stopAnimation()
                                        }
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(
                                        color = Color(0xFF1380C9),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        lifecycleScope.launch {
                                            controller.setBgAsset(BgAsset.Lose)
                                            controller.restartAnimation(
                                                animationData.copy(
                                                    isWin = false,
                                                )
                                            )
                                        }
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(
                                        color = Color(0xFFC97413),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        lifecycleScope.launch {
                                            controller.setBgAsset(BgAsset.Win)
                                            controller.restartAnimation(
                                                animationData.copy(
                                                    isWin = true,
                                                )
                                            )
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

private val animationData = RecapFallAnimationData(
    groups = List(3) {
        RecapFallAnimationGroupData(5, 300)
    },
    isWin = false,
)