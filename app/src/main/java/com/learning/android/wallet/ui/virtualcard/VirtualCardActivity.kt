package com.learning.android.wallet.ui.virtualcard

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.learning.android.R
import com.learning.android.databinding.ActivityVirtualCardBinding
import com.learning.android.wallet.model.cardandtransaction.CardAndTransactionResponse
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.repository.virtualcard.VirtualCardRepository
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.ui.cardtransactiondetail.CardTransactionDetailActivity
import com.learning.android.wallet.ui.transactionlist.TransactionListActivity
import com.learning.android.wallet.ui.virtualcard.adapter.VirtualCardAdapter
import com.learning.android.wallet.ui.virtualcard.adapter.VirtualCardTransactionAdapter
import com.learning.android.wallet.ui.virtualcard.viewmodel.VirtualCardViewModel
import com.learning.android.wallet.ui.virtualcard.viewmodel.VirtualCardViewModelFactory
import com.learning.android.utils.AppConstants
import com.learning.android.utils.AppUtils.dismissProgress
import com.learning.android.utils.AppUtils.showProgress

class VirtualCardActivity : BaseActivity<ActivityVirtualCardBinding>() {
    private lateinit var viewModel: VirtualCardViewModel
    private lateinit var repository: VirtualCardRepository

    override fun getBinding(inflater: LayoutInflater): ActivityVirtualCardBinding {
        return ActivityVirtualCardBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityVirtualCardBinding) {

    }

    override fun setupToolbar(binding: ActivityVirtualCardBinding) {
        binding.apply {
            includeToolbar.apply {
                ivToolbarBack.isVisible = true
                ivToolbarBack.setOnClickListener {
                    finish()
                }
                tvToolbarTitle.isVisible = true
                tvToolbarTitle.text = getString(R.string.virtual_card)
            }
        }
    }

    override fun liveDataObservers(binding: ActivityVirtualCardBinding) {
        viewModel.cardAndTransaction.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response = it.response as List<CardAndTransactionResponse?>?

                    if (response != null) virtualCardList(response)
                }

                else -> {}
            }
        }
    }

    override fun onClickListeners(binding: ActivityVirtualCardBinding) {
        binding.ivVirtualCardNext.setOnClickListener {
            val nextItem: Int = binding.vpVirtualCard.currentItem + 1
            if (nextItem < (binding.vpVirtualCard.adapter?.itemCount ?: 0)) {
                binding.vpVirtualCard.currentItem = nextItem
            } else {
                showMessage(
                    getString(R.string.no_more_cards_to_show),
                    AppConstants.MessageBackgroundColor.MESSAGE_COLOR_NOTICE
                )
            }
        }
        binding.ivVirtualCardPrevious.setOnClickListener {
            val previousItem: Int = binding.vpVirtualCard.currentItem - 1
            if (previousItem > -1) {
                binding.vpVirtualCard.currentItem = previousItem
            } else {
                showMessage(
                    "No more Cards to show",
                    AppConstants.MessageBackgroundColor.MESSAGE_COLOR_NOTICE
                )
            }
        }
    }

    override fun initViews(binding: ActivityVirtualCardBinding) {
        repository = VirtualCardRepository(this)
        viewModel = ViewModelProvider(
            this, VirtualCardViewModelFactory(repository)
        )[VirtualCardViewModel::class.java]
        viewModel.getCardAndTransactionApi<List<CardAndTransactionResponse>>()
    }

    private fun virtualCardList(data: List<CardAndTransactionResponse?>) {
        binding.apply {

            val adapter = VirtualCardAdapter(data) { cardDetails ->
                startActivity(
                    Intent(
                        this@VirtualCardActivity, TransactionListActivity::class.java
                    ).putExtra("cardId", cardDetails.cardId).setAction(
                        "your.custom.action"
                    )
                )
            }
            vpVirtualCard.adapter = adapter
            vpVirtualCard.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            ivVirtualCardPrevious.isVisible = false
                            ivVirtualCardNext.isVisible = true
                        }

                        (vpVirtualCard.adapter?.itemCount ?: 0) - 1 -> {
                            ivVirtualCardPrevious.isVisible = true
                            ivVirtualCardNext.isVisible = false
                        }

                        else -> {
                            ivVirtualCardPrevious.isVisible = true
                            ivVirtualCardNext.isVisible = true
                        }
                    }
                    showProgress()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val cardItem = data[position]
                        if (cardItem?.transactions != null) transactionList(cardItem.transactions)
                        else transactionList(arrayListOf())
                        dismissProgress()
                    }, 300)
                }

                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {

                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }
    }

    private fun transactionList(transactions: List<CardAndTransactionResponse.TransactionsTransaction?>) {
        binding.apply {

            rvVirtualCardTransactions.adapter =
                VirtualCardTransactionAdapter(transactions) { selectedData ->
                    startActivity(
                        Intent(
                            this@VirtualCardActivity, CardTransactionDetailActivity::class.java
                        ).putExtra("selectedData", selectedData).setAction(
                            "your.custom.action"
                        )
                    )
                }
        }
    }
}