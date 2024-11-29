package com.learning.android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.learning.android.databinding.ActivitySplashBinding
import com.learning.android.wallet.ui.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun getBinding(inflater: LayoutInflater): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivitySplashBinding) {

    }

    override fun initViews(binding: ActivitySplashBinding) {
        installSplashScreen()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, DashboardMenuActivity::class.java).setAction("your.custom.action"))
        }, 2000)
    }

    override fun setupToolbar(binding: ActivitySplashBinding) {}

    override fun onClickListeners(binding: ActivitySplashBinding) {

    }

    override fun liveDataObservers(binding: ActivitySplashBinding) {

    }

}