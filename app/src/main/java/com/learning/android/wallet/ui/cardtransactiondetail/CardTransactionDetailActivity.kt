package com.learning.android.wallet.ui.cardtransactiondetail

import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.learning.android.R
import com.learning.android.databinding.ActivityCardTransactionDetailBinding
import com.learning.android.wallet.model.cardandtransaction.CardAndTransactionResponse
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.utils.AppUtils.convertCardNumberToStar
import com.learning.android.utils.AppUtils.convertServerDateTimeToApp
import com.learning.android.utils.AppUtils.copyToClipboard
import java.text.NumberFormat
import java.util.Locale

class CardTransactionDetailActivity : BaseActivity<ActivityCardTransactionDetailBinding>() {

    private val transactionDetails: CardAndTransactionResponse.TransactionsTransaction? by lazy {
        intent.getSerializableExtr(
            "selectedData", CardAndTransactionResponse.TransactionsTransaction::class.java
        )
    }

    override fun getBinding(inflater: LayoutInflater): ActivityCardTransactionDetailBinding {
        return ActivityCardTransactionDetailBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityCardTransactionDetailBinding) {
        
    }

    override fun setupToolbar(binding: ActivityCardTransactionDetailBinding) {
        binding.apply {
            includeToolbar.apply {
                ivToolbarBack.isVisible = true
                ivToolbarBack.setOnClickListener {
                    finish()
                }
                tvToolbarTitle.isVisible = true
                tvToolbarTitle.text = getString(R.string.transaction_detail)
            }
        }
    }

    override fun liveDataObservers(binding: ActivityCardTransactionDetailBinding) {

    }

    override fun onClickListeners(binding: ActivityCardTransactionDetailBinding) {
        binding.apply {
            ivCardTransactionDetailsCopy.setOnClickListener {
                tvCardTransactionDetailTransactionId.text.toString()
                    .copyToClipboard(this@CardTransactionDetailActivity)

            }
        }
    }

    override fun initViews(binding: ActivityCardTransactionDetailBinding) {
        binding.apply {
            when (transactionDetails?.status) {
                3 -> {
                    tvCardTransactionDetailAmountTitle.text = getString(R.string.amount_deducted)
                    ivCardTransactionDetailStatus.setImageResource(R.drawable.ic_transaction_out)
                    tvCardTransactionDetailAmountTitle.setTextColor(
                        ContextCompat.getColor(
                            this@CardTransactionDetailActivity, R.color.red_500
                        )
                    )
                    tvCardTransactionDetailAmount.setTextColor(
                        ContextCompat.getColor(
                            this@CardTransactionDetailActivity, R.color.red_500
                        )
                    )
                    tvCardTransactionDetailBankNameTitle.text = getString(R.string.paid_to)
                }

                4 -> {
                    tvCardTransactionDetailAmountTitle.text = getString(R.string.amount_added)
                    tvCardTransactionDetailAmountTitle.setTextColor(
                        ContextCompat.getColor(
                            this@CardTransactionDetailActivity, R.color.green_400
                        )
                    )
                    tvCardTransactionDetailAmount.setTextColor(
                        ContextCompat.getColor(
                            this@CardTransactionDetailActivity, R.color.green_400
                        )
                    )
                    tvCardTransactionDetailBankNameTitle.text = getString(R.string.receiveFrom)

                    ivCardTransactionDetailStatus.setImageResource(R.drawable.ic_transaction_in)
                }

                else -> {
                    tvCardTransactionDetailAmountTitle.text = "-"
                    ivCardTransactionDetailStatus.isVisible = false
                }
            }
            tvCardTransactionDetailDateTime.text =
                transactionDetails?.transactionDate.convertServerDateTimeToApp()
            tvCardTransactionDetailAmount.text = getString(
                R.string.amount_with_symbol,
                NumberFormat.getNumberInstance(Locale.getDefault())
                    .format(transactionDetails?.amount.toString().replace("-", "").toDouble())
            )
            tvCardTransactionDetailBankName.text = transactionDetails?.paidOrReceived ?: "-"
            tvCardTransactionDetailCardNo.text =
                transactionDetails?.cardNumber?.convertCardNumberToStar() ?: "-"
            tvCardTransactionDetailTransactionId.text = transactionDetails?.transactionId ?: "-"
        }
    }
}