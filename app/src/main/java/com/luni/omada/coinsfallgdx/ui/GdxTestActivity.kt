package com.luni.omada.coinsfallgdx.ui

import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.luni.omada.coinsfallgdx.databinding.DailyRecapActivityTestBinding

class GdxTestActivity : FragmentActivity(), AndroidFragmentApplication.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(DailyRecapActivityTestBinding.inflate(layoutInflater).root)
    }

    override fun exit() {}
}