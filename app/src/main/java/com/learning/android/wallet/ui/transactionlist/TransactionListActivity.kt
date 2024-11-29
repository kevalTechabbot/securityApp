package com.learning.android.wallet.ui.transactionlist

import android.content.Intent
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.learning.android.R
import com.learning.android.databinding.ActivityTransactionListBinding
import com.learning.android.wallet.model.cardandtransaction.CardAndTransactionResponse
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.repository.virtualcard.VirtualCardRepository
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.wallet.ui.cardtransactiondetail.CardTransactionDetailActivity
import com.learning.android.wallet.model.transactionlist.request.TransactionListRequest
import com.learning.android.wallet.ui.virtualcard.adapter.VirtualCardTransactionAdapter
import com.learning.android.wallet.ui.virtualcard.viewmodel.VirtualCardViewModel
import com.learning.android.wallet.ui.virtualcard.viewmodel.VirtualCardViewModelFactory

class TransactionListActivity : BaseActivity<ActivityTransactionListBinding>() {
    private lateinit var viewModel: VirtualCardViewModel
    private lateinit var repository: VirtualCardRepository

    private val cardId: String? by lazy {
        intent.getStringExtra("cardId")
    }

    override fun getBinding(inflater: LayoutInflater): ActivityTransactionListBinding {
        return ActivityTransactionListBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityTransactionListBinding) {
        
    }

    override fun setupToolbar(binding: ActivityTransactionListBinding) {
        binding.apply {
            includeToolbar.apply {
                ivToolbarBack.isVisible = true
                ivToolbarBack.setOnClickListener {
                    finish()
                }
                tvToolbarTitle.isVisible = true
                tvToolbarTitle.text = getString(R.string.all_transactions)
            }
        }
    }

    override fun liveDataObservers(binding: ActivityTransactionListBinding) {
        viewModel.cardAndTransaction.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    val response =
                        it.response as List<CardAndTransactionResponse.TransactionsTransaction>?

                    if (response != null) virtualTransactionList(response)
                }

                else -> {}
            }
        }
    }

    override fun onClickListeners(binding: ActivityTransactionListBinding) {

    }

    override fun initViews(binding: ActivityTransactionListBinding) {
        repository = VirtualCardRepository(this)
        viewModel = ViewModelProvider(
            this, VirtualCardViewModelFactory(repository)
        )[VirtualCardViewModel::class.java]
        viewModel.getLastNTransactionApi<List<CardAndTransactionResponse.TransactionsTransaction?>>(
            TransactionListRequest(
                cardId,
                "0"
            )
        )
    }

    private fun virtualTransactionList(response: List<CardAndTransactionResponse.TransactionsTransaction>) {
        binding.apply {

            val adapter = VirtualCardTransactionAdapter(response) { selectedData ->
                startActivity(
                    Intent(
                        this@TransactionListActivity, CardTransactionDetailActivity::class.java
                    ).putExtra("selectedData", selectedData).setAction(
                        "your.custom.action"
                    )
                )
            }
            rvTransactionList.adapter = adapter
        }
    }
}