package com.learning.android.chat.preferences

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.text.Html
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.learning.android.R

object AdbDialog {
    fun show(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.requires_adb_dialog_title)
            .setMessage(
                Html.fromHtml(
                    context.getString(R.string.requires_adb_dialog_description) + "<br><br><tt>adb shell pm grant " + context.packageName + " " + Manifest.permission.WRITE_SECURE_SETTINGS +
                            "</tt><br><br>" + context.getString(R.string.requires_adb_dialog_description_second_half),
                    Html.FROM_HTML_MODE_COMPACT
                )
            )
            .setPositiveButton(
                R.string.ok
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setNeutralButton(R.string.requires_adb_dialog_copy_button) { _, which ->
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(
                    "",
                    "adb shell pm grant " + context.packageName + " " + Manifest.permission.WRITE_SECURE_SETTINGS
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    context,
                    R.string.requires_adb_dialog_copy_success,
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }
}