package com.learning.android.wallet.ui.webview

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.learning.android.R
import com.learning.android.databinding.ActivityWebViewBinding
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.utils.AppConstants
import com.learning.android.utils.ExtensionUtils.Common.show

class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {

    private val title: String? by lazy {
        intent.getStringExtra(AppConstants.BundleConstants.INTENT_WEB_TITLE)
    }

    private val url: String? by lazy {
        intent.getStringExtra(AppConstants.BundleConstants.INTENT_WEB_URL)
    }

    override fun getBinding(inflater: LayoutInflater): ActivityWebViewBinding {
        return ActivityWebViewBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityWebViewBinding) {
        
    }

    override fun initViews(binding: ActivityWebViewBinding) {
        binding.wvWebView.loadUrl(url!!)
    }

    override fun setupToolbar(binding: ActivityWebViewBinding) {
        binding.apply {
            includeToolbar.apply {
                tvToolbarTitle.show()
                tvToolbarTitle.text = title
                ivToolbarMenu.show()
                ivToolbarMenu.setOnClickListener {
                    showActionBarMenu()
                }
                ivToolbarBack.show()
                ivToolbarBack.setOnClickListener {
                    onBackPress()
                }
            }
        }
    }

    private fun showActionBarMenu() {
        val popupMenu = PopupMenu(this@WebViewActivity, binding.includeToolbar.ivToolbarMenu)
        popupMenu.menuInflater.inflate(R.menu.webview_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.actionRefresh -> {
                    binding.wvWebView.loadUrl(url!!)
                    return@setOnMenuItemClickListener true
                }

                R.id.actionOpenInBrowser -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                    return@setOnMenuItemClickListener true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onClickListeners(binding: ActivityWebViewBinding) {}

    override fun liveDataObservers(binding: ActivityWebViewBinding) {}
}