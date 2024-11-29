package com.learning.android

import android.content.Intent
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.learning.android.databinding.ActivityDashboardMenuBinding
import com.learning.android.notification.NotificationActivity
import com.learning.android.utils.AppConstants
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.ui.login.LoginActivity
import com.learning.android.wallet.ui.passcode.PasscodeActivity
import com.learning.android.wallet.ui.welcome.WelcomeActivity

class DashboardMenuActivity : BaseActivity<ActivityDashboardMenuBinding>() {

    override fun getBinding(inflater: LayoutInflater): ActivityDashboardMenuBinding {
        return ActivityDashboardMenuBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityDashboardMenuBinding) {

    }

    override fun initViews(binding: ActivityDashboardMenuBinding) {
        binding.apply {
            rvDashboardMenu.adapter = DashboardMenuAdapter(createMenus()) {
                when (it.id) {
                    0 -> {
                        walletNavigation()
                    }

                    1 -> {
                        notificationNavigation()
                    }
                }
            }
        }
    }

    override fun setupToolbar(binding: ActivityDashboardMenuBinding) {
        binding.includeToolbar.apply {
            tvToolbarTitle.text = getString(R.string.learning_dashboard)
            tvToolbarTitle.isVisible = true
        }
    }

    override fun onClickListeners(binding: ActivityDashboardMenuBinding) {

    }

    override fun liveDataObservers(binding: ActivityDashboardMenuBinding) {

    }

    private fun createMenus(): List<DashboardMenuResponse> {
        val listOfMenus: ArrayList<DashboardMenuResponse> = arrayListOf()

        listOfMenus.add(
            DashboardMenuResponse(
                0, R.drawable.ic_wallet_icon_white, "Wallet"
            )
        )
        listOfMenus.add(
            DashboardMenuResponse(
                1, R.drawable.ic_notification_icon_white, "Notification"
            )
        )

        return listOfMenus
    }

    private fun walletNavigation() {
        val i = if (!sharedPref.getBoolean(AppConstants.IS_WELCOME)) {
            Intent(
                this@DashboardMenuActivity, WelcomeActivity::class.java
            ).setAction("your.custom.action")
        } else if (sharedPref.getBoolean(AppConstants.IS_LOGIN)) {
            Intent(this@DashboardMenuActivity, PasscodeActivity::class.java).putExtra(
                AppConstants.MPinActionStatus.STATUS_KEY, AppConstants.MPinActionStatus.SPLASH
            ).setAction("your.custom.action")
        } else {
            Intent(
                this@DashboardMenuActivity, LoginActivity::class.java
            ).setAction("your.custom.action")
        }
        startActivity(i)
    }

    private fun notificationNavigation() {
        startActivity(
            Intent(
                this@DashboardMenuActivity, NotificationActivity::class.java
            ).setAction("your.custom.action")
        )
    }
}