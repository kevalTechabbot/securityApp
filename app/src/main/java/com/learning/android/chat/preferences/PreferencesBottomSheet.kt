//package com.learning.android.chat.preferences
//
//import android.Manifest
//import android.R
//import android.content.Context
//import android.view.View
//
//class PreferencesBottomSheet : BottomSheetDialogFragment() {
//    private var binding: BottomSheetPreferencesBinding? = null
//    var showDialog: Boolean = false
//
//    private fun forceTappableTile(force: Boolean) {
//        binding.tappableTileSwitch.setChecked(force)
//        binding.tappableTileSwitch.setEnabled(!force)
//        binding.tappableTileTitle.setAlpha(if (force) 0.4f else 1f)
//        binding.tappableTileDescription.setAlpha(if (force) 0.4f else 1f)
//        binding.tappableTileLayout.setEnabled(!force)
//    }
//
//    private fun checkIfPermissionIsDenied(): Boolean {
//        return requireContext().checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = BottomSheetPreferencesBinding.inflate(inflater, container, false)
//        val preferences: SharedPreferences =
//            requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)
//
//        binding.tappableTileLayout.setOnClickListener { self -> binding.tappableTileSwitch.toggle() }
//
//        binding.tappableTileSwitch.setOnCheckedChangeListener { self, state ->
//            if (checkIfPermissionIsDenied() && state) {
//                self.setChecked(false)
//                AdbDialog.show(requireContext())
//            } else {
//                if (state && showDialog) {
//                    MaterialAlertDialogBuilder(requireContext())
//                        .setTitle(R.string.power_save_tile_warning_title)
//                        .setMessage(R.string.power_save_system_warning_description)
//                        .setPositiveButton(
//                            R.string.ok,
//                            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
//                        .show()
//                }
//                preferences
//                    .edit()
//                    .putBoolean("tappableTileEnabled", state)
//                    .apply()
//
//                setTileStatePreferenceEnabled(!state)
//            }
//        }
//
//        binding.emulatePowerSaveTilePreferenceLayout.setOnClickListener { self -> binding.emulatePowerSaveTilePreferenceSwitch.toggle() }
//
//        binding.emulatePowerSaveTilePreferenceSwitch.setOnCheckedChangeListener { self, state ->
//            if (checkIfPermissionIsDenied() && state) {
//                self.setChecked(false)
//                AdbDialog.show(requireContext())
//            } else {
//                forceTappableTile(state)
//                preferences.edit().putBoolean("emulatePowerSaveTile", state).apply()
//                setTileStatePreferenceEnabled(!state)
//
//                binding.tileTextLayout.setEnabled(!state)
//                binding.tileTextTitle.setAlpha(if (state) 0.4f else 1f)
//                binding.tileTextDescription.setAlpha(if (state) 0.4f else 1f)
//                binding.tileTextDescription.setText(getString(if (state) R.string.bottom_sheet_preferences_tile_state_disabled_reason else R.string.bottom_sheet_preferences_tile_text_description))
//
//                binding.infoInTitlePreferenceLayout.setEnabled(!state)
//                binding.infoInTitlePreferenceTitle.setAlpha(if (state) 0.4f else 1f)
//                binding.infoInTitlePreferenceDescription.setAlpha(if (state) 0.4f else 1f)
//                binding.infoInTitlePreferenceDescription.setText(getString(if (state) R.string.bottom_sheet_preferences_tile_state_disabled_reason else R.string.info_in_title_option_description))
//                binding.infoInTitleSwitch.setChecked(false)
//                binding.infoInTitleSwitch.setEnabled(!state)
//
//                binding.dynamicTileIconLayout.setEnabled(!state)
//                binding.dynamicTileIconTitle.setAlpha(if (state) 0.4f else 1f)
//                binding.dynamicTileIconDescription.setAlpha(if (state) 0.4f else 1f)
//                binding.dynamicTileIconDescription.setText(getString(if (state) R.string.bottom_sheet_preferences_tile_state_disabled_reason else R.string.bottom_sheet_preferences_dynamic_tile_icon_description))
//                binding.dynamicTileIconSwitch.setChecked(false)
//                binding.dynamicTileIconSwitch.setEnabled(!state)
//            }
//        }
//
//        binding.infoInTitlePreferenceLayout.setOnClickListener { self -> binding.infoInTitleSwitch.toggle() }
//        binding.infoInTitleSwitch.setOnCheckedChangeListener { self, state ->
//            preferences.edit().putBoolean("infoInTitle", state).apply()
//        }
//
//        binding.dynamicTileIconLayout.setOnClickListener { self -> binding.dynamicTileIconSwitch.toggle() }
//        binding.dynamicTileIconSwitch.setOnCheckedChangeListener { self, state ->
//            preferences.edit().putBoolean("dynamic_tile_icon", state).apply()
//        }
//
//        if (preferences.getBoolean("emulatePowerSaveTile", false)) {
//            binding.emulatePowerSaveTilePreferenceSwitch.setChecked(true)
//            forceTappableTile(true)
//        } else {
//            binding.tappableTileSwitch.setChecked(
//                preferences.getBoolean(
//                    "tappableTileEnabled",
//                    false
//                )
//            )
//        }
//
//        binding.dynamicTileIconSwitch.setChecked(preferences.getBoolean("dynamic_tile_icon", true))
//
//        showDialog = true
//
//        binding.infoInTitleSwitch.setChecked(preferences.getBoolean("infoInTitle", false))
//
//        binding.tileStateLayout.setOnClickListener { v -> TileStatePickerDialog.show(requireContext()) { this.updateTileStateDescription() } }
//        updateTileStateDescription()
//
//        binding.tileTextLayout.setOnClickListener { v ->
//            TileTextDisambiguationDialog.show(
//                requireContext()
//            )
//        }
//
//        return binding.getRoot()
//    }
//
//    private fun updateTileStateDescription() {
//        val preferences: SharedPreferences =
//            requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)
//        if (preferences.getBoolean(
//                "emulatePowerSaveTile",
//                false
//            ) || preferences.getBoolean("tappableTileEnabled", false)
//        ) {
//            binding.tileStateDescription.setText(R.string.bottom_sheet_preferences_tile_state_disabled_reason)
//            return
//        }
//
//        when (preferences.getInt("tileState", 0)) {
//            0 -> binding.tileStateDescription.setText(R.string.tile_state_always_on)
//            1 -> binding.tileStateDescription.setText(R.string.tile_state_on_when_charging)
//            2 -> binding.tileStateDescription.setText(R.string.tile_state_always_off)
//        }
//    }
//
//    private fun setTileStatePreferenceEnabled(enabled: Boolean) {
//        binding.tileStateTitle.setAlpha(if (enabled) 1f else 0.4f)
//        binding.tileStateDescription.setAlpha(if (enabled) 1f else 0.4f)
//        binding.tileStateLayout.setEnabled(enabled)
//        updateTileStateDescription()
//    }
//
//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        requireActivity().finish()
//    }
//}
