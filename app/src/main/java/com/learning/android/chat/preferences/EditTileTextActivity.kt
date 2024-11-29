//package com.learning.android.chat.preferences
//
//import android.content.Context
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.WindowCompat
//import com.google.android.material.color.DynamicColors
//import java.util.Objects
//
//class EditTileTextActivity : AppCompatActivity() {
//    private var hasTextBeenChanged = false
//
//    protected override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        DynamicColors.applyToActivityIfAvailable(this)
//        val binding: ActivityEditTileTextBinding =
//            ActivityEditTileTextBinding.inflate(getLayoutInflater())
//        setContentView(binding.getRoot())
//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
//                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
//                OnBackInvokedCallback { this.warnForUnsavedChanges() })
//        }
//
//        val isEditingChargingText: Boolean =
//            getIntent().getBooleanExtra("isEditingChargingText", false)
//        val preference_key = if (isEditingChargingText) "charging_text" else "discharging_text"
//
//        binding.infoText.setText(
//            getString(
//                R.string.activity_edit_tile_text_info_text,
//                getString(if (isEditingChargingText) R.string.activity_edit_tile_text_info_text_default_charging else R.string.activity_edit_tile_text_info_text_default_discharging)
//            )
//        )
//
//        binding.editTileTextEditText.setText(
//            getSharedPreferences(
//                "preferences",
//                Context.MODE_PRIVATE
//            ).getString(preference_key, "")
//        )
//
//        binding.editTileTextEditText.addTextChangedListener(object : TextWatcher {
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                hasTextBeenChanged = true
//            }
//
//            override fun beforeTextChanged(s: CharSequence, a: Int, b: Int, c: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable) {
//            }
//        })
//
//        binding.topBar.setOnMenuItemClickListener { item ->
//            if (item.getItemId() === R.id.save_menu_item) {
//                getSharedPreferences("preferences", Context.MODE_PRIVATE).edit().putString(
//                    preference_key,
//                    Objects.requireNonNull(binding.editTileTextEditText.getText()).toString()
//                        .trim { it <= ' ' }).apply()
//                hasTextBeenChanged = false
//                Toast.makeText(
//                    this,
//                    R.string.activity_edit_tile_text_save_success,
//                    Toast.LENGTH_LONG
//                ).show()
//                finish()
//                return@setOnMenuItemClickListener true
//            }
//            false
//        }
//
//        binding.topBar.setNavigationOnClickListener { v -> warnForUnsavedChanges() }
//    }
//
//    override fun onBackPressed() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
//            warnForUnsavedChanges()
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//    private fun warnForUnsavedChanges() {
//        if (hasTextBeenChanged) {
//            MaterialAlertDialogBuilder(this)
//                .setTitle(R.string.activity_edit_text_discard_dialog_title)
//                .setMessage(R.string.activity_edit_text_discard_dialog_description)
//                .setNegativeButton(R.string.activity_edit_text_discard_dialog_negative_button) { dialog, which -> finish() }
//                .setPositiveButton(R.string.activity_edit_text_discard_dialog_positive_button) { dialog, which -> dialog.dismiss() }
//                .show()
//        } else {
//            finish()
//        }
//    }
//}
