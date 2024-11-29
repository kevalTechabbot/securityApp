package com.learning.android.wallet.ui.balancecheck

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.learning.android.R
import com.learning.android.databinding.ActivityBalanceCheckBinding
import com.learning.android.wallet.ui.base.BaseActivity
import com.learning.android.utils.AppConstants
import com.learning.android.utils.AppUtils
import com.learning.android.utils.ExtensionUtils.Common.showConfirmationDialog
import com.learning.android.utils.parcelable
import com.learning.android.utils.parcelableArray
import java.text.NumberFormat
import java.util.Locale

class BalanceCheckActivity : BaseActivity<ActivityBalanceCheckBinding>() {
    private var nfcAdapter: NfcAdapter? = null

    override fun getBinding(inflater: LayoutInflater): ActivityBalanceCheckBinding {
        return ActivityBalanceCheckBinding.inflate(inflater)
    }

    override fun onDoneKeyboardClicked(binding: ActivityBalanceCheckBinding) {

    }

    override fun initViews(binding: ActivityBalanceCheckBinding) {
        binding.apply {
            changeBalance()
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onResume() {
        super.onResume()
        checkNfcStatus()
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        nfcAdapter?.let {
            val pendingIntent = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            }
            it.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun setupToolbar(binding: ActivityBalanceCheckBinding) {
        binding.apply {
            includeToolbar.apply {
                ivToolbarBack.isVisible = true
                ivToolbarBack.setOnClickListener {
                    finish()
                }
                tvToolbarTitle.isVisible = true
                tvToolbarTitle.text = getString(R.string.physical_wallet_balance)
            }
        }
    }

    override fun onClickListeners(binding: ActivityBalanceCheckBinding) {
        binding.apply {
            btBalanceCheckNFCDisableSubmit.setOnClickListener {
                openNfcSettings()
            }
            btBalanceCheckNFCNoFoundClose.setOnClickListener {
                finish()
            }
        }
    }

    override fun liveDataObservers(binding: ActivityBalanceCheckBinding) {

    }

    private fun changeBalance(amount: String? = null) {
        if (amount != null) showMessage(
            getString(R.string.nfc_amount_scanned_successfully),
            AppConstants.MessageBackgroundColor.MESSAGE_COLOR_SUCCESS
        )
        binding.tvBalanceCheckAmount.text = try {
            getString(
                R.string.amount_with_symbol,
                if (amount != null) NumberFormat.getNumberInstance(Locale.getDefault())
                    .format(amount.toDouble())
                else "--"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            "--"
        }
    }

    private fun checkNfcStatus() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this@BalanceCheckActivity)
        if (nfcAdapter == null) {
            // NFC is not supported by the device
            nfcStatus(NfcStatus.NFC_NOT_AVAILABLE)
        } else if (nfcAdapter!!.isEnabled) {
            // NFC is enabled
            nfcStatus(NfcStatus.NFC_ENABLE)
        } else if (!nfcAdapter!!.isEnabled) {
            // NFC is disable
            nfcStatus(NfcStatus.NFC_DISABLE)
        }
    }

    private fun nfcStatus(nfcStatus: NfcStatus) {
        binding.apply {
            clBalanceCheckNFC.isVisible = nfcStatus == NfcStatus.NFC_ENABLE
            clBalanceCheckNFCDisableOrNot.isVisible =
                nfcStatus == NfcStatus.NFC_NOT_AVAILABLE || nfcStatus == NfcStatus.NFC_DISABLE
            cvBalanceCheckNFCNoFound.isVisible = nfcStatus == NfcStatus.NFC_NOT_AVAILABLE
            cvBalanceCheckNFCDisable.isVisible = nfcStatus == NfcStatus.NFC_DISABLE
        }
    }

    private fun openNfcSettings() {
        val intent = Intent().apply {
            action = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    // For Android 12 (API level 31) and above
                    Settings.ACTION_NFC_SETTINGS
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    // For Android 10 (API level 29) and Android 11 (API level 30)
                    Settings.ACTION_WIRELESS_SETTINGS
                }

                else -> {
                    // For versions prior to Android 10
                    Settings.ACTION_WIRELESS_SETTINGS
                }
            }
        }
        startActivity(intent)
    }

    private fun handleNfcMessages(messages: Array<NdefMessage?>, intent: Intent) {
        AppUtils.logI("nFC", "5")
        var payloadString = ""
        for (message in messages) {
            AppUtils.logI("nFC", "6 -> $message")
            message?.let {
                AppUtils.logI("nFC", "7 -> $message")
                for ((index, record) in it.records.withIndex()) {
                    AppUtils.logI("nFC", "8 -> $record")
                    val typeString = record.type.toString(Charsets.UTF_8)
                    AppUtils.logI("nFC", "9 -> $typeString == $mimeType")
                    if (typeString.lowercase() == mimeType.lowercase()) {
                        payloadString = record.payload.toString(Charsets.UTF_8)
                        AppUtils.logI("nFC", "10 -> $payloadString")
                        break
                    } else if (index == it.records.size - 1) {
                        AppUtils.logI("nFC", "11 ->$index")
                        changeBalance()
                        showConfirmationDialog(getString(R.string.nfc_),
                            getString(R.string.do_you_want_to_generate_random_tag_for_test),
                            onYes = {
                                createTag(intent, true)
                            })
                    }
                }
            }
        }
        if (payloadString != "") try {
            changeBalance(payloadString)
            createTag(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            AppUtils.logI("nFC", "1")
            val rawMessages: Array<out Parcelable>? =
                intent.parcelableArray(NfcAdapter.EXTRA_NDEF_MESSAGES)
            AppUtils.logI("nFC", "2")
            if (rawMessages != null) {
                AppUtils.logI("nFC", "3")
                val messages = Array<NdefMessage?>(rawMessages.size) { null }
                for (i in rawMessages.indices) {
                    messages[i] = rawMessages[i] as NdefMessage
                    AppUtils.logI("nFC", "4 -> ${messages[i]}")
                }
                handleNfcMessages(messages, intent)
            } else {
                AppUtils.logI("nFC", "13")
                changeBalance()
                showMessage(
                    getString(R.string.no_message_found_in_nfc_tag),
                    AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
                )
            }
        } else {
            AppUtils.logI("nFC", "0 -> ${intent.action}")
            changeBalance()
            showConfirmationDialog(getString(R.string.nfc_),
                getString(R.string.do_you_want_to_generate_random_tag_for_test),
                onYes = {
                    createTag(intent, true)
                })
        }
    }

    private fun createTag(intent: Intent, showMessageForFirstInsert: Boolean = false) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action.toString() || NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action.toString()) {
            val tag: Tag? = intent.parcelable(NfcAdapter.EXTRA_TAG)
            if (tag == null) {
                showMessage(
                    getString(R.string.nfc_tag_not_found),
                    AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
                )
                return
            }
            try {
                val ndef = Ndef.get(tag)
                if (ndef != null) {
                    val generateNewBalance =
                        AppUtils.generateRandomDecimal().toString().toByteArray(Charsets.UTF_8)
                    if (ndef.isWritable) {
                        ndef.connect()

                        val customRecord = NdefRecord.createMime(mimeType, generateNewBalance)
                        val messageWithPayload = NdefMessage(arrayOf(customRecord))

                        ndef.writeNdefMessage(messageWithPayload)
                        if (showMessageForFirstInsert) showMessage(
                            getString(R.string.tag_created_successfully),
                            AppConstants.MessageBackgroundColor.MESSAGE_COLOR_SUCCESS
                        )
                    } else {
                        showMessage(
                            getString(R.string.nfc_tag_is_not_writable),
                            AppConstants.MessageBackgroundColor.MESSAGE_COLOR_NOTICE
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showMessage(
                    getString(R.string.nfc_tag_has_been_removed_so_early),
                    AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR
                )
            }
        }
    }

    enum class NfcStatus {
        NFC_NOT_AVAILABLE, NFC_DISABLE, NFC_ENABLE
    }
}