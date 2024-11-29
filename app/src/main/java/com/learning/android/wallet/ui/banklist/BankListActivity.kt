package com.learning.android.wallet.ui.banklist

import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.learning.android.R
import com.learning.android.databinding.ActivityBankListBinding
import com.learning.android.wallet.model.banklist.BankListResponse
import com.learning.android.wallet.network.Status
import com.learning.android.wallet.repository.banklist.BankListRepository
import com.learning.android.wallet.ui.banklist.adapter.BankListAdapter
import com.learning.android.wallet.ui.banklist.viewmodel.BankListViewModel
import com.learning.android.wallet.ui.banklist.viewmodel.BankListViewModelFactory
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.utils.AppUtils.openAppByPackageId
import com.learning.android.utils.ExtensionUtils.Common.showConfirmationDialog
import com.learning.android.utils.ExtensionUtils.EditTextUtils.onTextChanged

class BankListActivity : BaseActivity<ActivityBankListBinding>() {
    private var bankListResponse: BankListResponse? = null
    private var isCheckBiometric = true
    private var bankListResponseList: ArrayList<BankListResponse> = arrayListOf()
    private lateinit var viewModel: BankListViewModel
    private lateinit var repository: BankListRepository

    override fun getBinding(inflater: LayoutInflater): ActivityBankListBinding {
        return ActivityBankListBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityBankListBinding) {

    }

    override fun initViews(binding: ActivityBankListBinding) {
        repository = BankListRepository(this)
        viewModel = ViewModelProvider(
            this, BankListViewModelFactory(repository)
        )[BankListViewModel::class.java]
        bankListResponseList = arrayListOf()
        getBankNamesApi()

        initBiometric {
            if (it == null) {
                isCheckBiometric = false
            } else {
                if (it) openAppByPackageId(this@BankListActivity, bankListResponse?.packageName)
            }
        }
        binding.apply {
            tieBankListSearch.onTextChanged { p0 ->
                val filteredItems = filterItems(bankListResponseList) {
                    (it.bankName ?: "").lowercase().contains(p0) || (it.packageName
                        ?: "").lowercase().contains(p0)
                }

                setUpDataToList(filteredItems)
            }
        }
    }

    private fun setUpDataToList(appList: List<BankListResponse>) {
        binding.apply {
            if (appList.isNotEmpty()) {
                rvBankList.isVisible = true
                tvBankListNoDataFound.isVisible = false
                appList.sortedBy { it.packageName }
                rvBankList.adapter = BankListAdapter(appList) { applicationData, _ ->
                    showConfirmationDialog(getString(R.string.app_navigation),
                        getString(R.string.are_you_sure_you_want_to_leave_this_app_go_to_another_app),
                        {
                            bankListResponse = applicationData
                            if (isCheckBiometric) authenticateWithBiometrics()
                            else openAppByPackageId(
                                this@BankListActivity, bankListResponse?.packageName
                            )
                        })
                }
            } else {
                rvBankList.isVisible = false
                tvBankListNoDataFound.isVisible = true
            }
        }
    }

    override fun setupToolbar(binding: ActivityBankListBinding) {
        binding.apply {
            includeToolbar.apply {
                ivToolbarBack.isVisible = true
                ivToolbarBack.setOnClickListener {
                    finish()
                }
                tvToolbarTitle.isVisible = true
                tvToolbarTitle.text = getString(R.string.digital_cash)
            }
        }
    }

    override fun onClickListeners(binding: ActivityBankListBinding) {
        binding.apply {

        }
    }

    override fun liveDataObservers(binding: ActivityBankListBinding) {
        viewModel.bankListResponse.observe(this) {
            when (it.localStatus) {
                Status.SUCCESS -> {
                    bankListResponseList =
                        isPackageInstalled(it.response as ArrayList<BankListResponse>)
                    setUpDataToList(bankListResponseList)
                }

                else -> {

                }
            }
        }
    }

    private fun getBankNamesApi() {
        viewModel.getBankNamesApi<List<BankListResponse>?>()
    }

    private fun filterItems(
        originalList: List<BankListResponse>, filterCondition: (BankListResponse) -> Boolean
    ): List<BankListResponse> {
        return originalList.filter(filterCondition)
    }
}