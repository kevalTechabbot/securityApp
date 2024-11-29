package com.learning.android.wallet.ui.dashboard

import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import com.learning.android.BuildConfig
import com.learning.android.R
import com.learning.android.databinding.ActivityDashboardBinding
import com.learning.android.wallet.ui.balancecheck.BalanceCheckActivity
import com.learning.android.wallet.ui.banklist.BankListActivity
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.ui.dashboard.adapter.DashboardAdapter
import com.learning.android.wallet.ui.dashboard.adapter.DashboardItemData
import com.learning.android.wallet.ui.flexiblecontrol.FlexibleControlsActivity
import com.learning.android.wallet.ui.passcode.PasscodeActivity
import com.learning.android.wallet.ui.resetpassword.ResetPasswordActivity
import com.learning.android.wallet.ui.virtualcard.VirtualCardActivity
import com.learning.android.utils.AppConstants
import com.learning.android.utils.AppUtils.doLogout

class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {

    private lateinit var allItems: List<DashboardItemData>

    override fun getBinding(inflater: LayoutInflater): ActivityDashboardBinding {
        return ActivityDashboardBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityDashboardBinding) {
        
    }

    override fun initViews(binding: ActivityDashboardBinding) {
        binding.apply {
            allItems = arrayListOf(
                DashboardItemData(
                    getString(R.string.check_virtual_wallet_balance), R.drawable.ic_virtual_card
                ),
                DashboardItemData(getString(R.string.check_a_wallet_balance), R.drawable.ic_card),
                DashboardItemData(
                    getString(R.string.drawdown_digital_cash), R.drawable.ic_hand_card
                ),
                DashboardItemData(getString(R.string.flexible_controls), R.drawable.ic_setting)
            )
            tvDashboardVersionName.text = getString(
                R.string.version_code_version_name,
                BuildConfig.VERSION_CODE.toString(),
                BuildConfig.VERSION_NAME
            )
            rvDashboardList.adapter = DashboardAdapter(allItems) { position ->
                when (position) {
                    0 -> {
                        startActivity(
                            Intent(
                                this@DashboardActivity, VirtualCardActivity::class.java
                            ).setAction(
                                "your.custom.action"
                            )
                        )
                    }

                    1 -> {
                        startActivity(
                            Intent(
                                this@DashboardActivity, BalanceCheckActivity::class.java
                            ).setAction(
                                "your.custom.action"
                            )
                        )
                    }

                    2 -> {
                        startActivity(
                            Intent(
                                this@DashboardActivity, BankListActivity::class.java
                            ).setAction(
                                "your.custom.action"
                            )
                        )
//                        showConfirmationDialogForSingleClick(getString(R.string.new_feature_),
//                            getString(
//                                R.string.this_feature_will_come_soon
//                            ))
                    }

                    3 -> {
                        startActivity(
                            Intent(
                                this@DashboardActivity, FlexibleControlsActivity::class.java
                            ).setAction(
                                "your.custom.action"
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onClickListeners(binding: ActivityDashboardBinding) {
    }

    override fun liveDataObservers(binding: ActivityDashboardBinding) {

    }

    override fun setupToolbar(binding: ActivityDashboardBinding) {
        binding.apply {
            includeToolbar.apply {
                tvToolbarTitle.isVisible = true
                ivToolbarMenu.isVisible = true
                tvToolbarTitle.text = getString(R.string.dashboard)
                ivToolbarMenu.setOnClickListener {
                    showActionBarMenu()
                }
            }
        }
    }


    override fun onBackPress() {
        super.onBackPress()
        finishAffinity()
    }

    private fun showActionBarMenu() {
        val popupMenu = PopupMenu(this@DashboardActivity, binding.includeToolbar.ivToolbarMenu)
        popupMenu.menuInflater.inflate(R.menu.dashboard_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.actionResetMPin -> {
                    startActivity(
                        Intent(
                            this@DashboardActivity, PasscodeActivity::class.java
                        ).putExtra(
                            AppConstants.MPinActionStatus.STATUS_KEY,
                            AppConstants.MPinActionStatus.RESET
                        ).setAction(
                            "your.custom.action"
                        )
                    )
                    return@setOnMenuItemClickListener true
                }

                R.id.actionResetPassword -> {
                    startActivity(
                        Intent(
                            this@DashboardActivity, ResetPasswordActivity::class.java
                        ).putExtra(
                            AppConstants.MPinActionStatus.STATUS_KEY,
                            AppConstants.MPinActionStatus.RESET
                        ).setAction(
                            "your.custom.action"
                        )
                    )
                    return@setOnMenuItemClickListener true
                }

                R.id.actionLogout -> {
                    doLogout()
                    return@setOnMenuItemClickListener true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

}