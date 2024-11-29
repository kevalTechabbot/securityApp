//package com.learning.android.chat.preferences
//
//import android.R
//import android.content.Context
//
//object TileStatePickerDialog {
//    fun show(context: Context, completionHandler: Runnable) {
//        val choice = intArrayOf(0)
//        val preferences: SharedPreferences =
//            context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
//
//        val title: TextView = TextView(context)
//        // You Can Customise your Title here
//        title.setText(R.string.dialog_tile_state_picker_title)
//        title.setGravity(Gravity.CENTER)
//        title.setPadding(60, 60, 60, 10)
//        title.setTextSize(24f)
//
//        MaterialAlertDialogBuilder(context)
//            .setSingleChoiceItems(
//                arrayOf<String>(
//                    context.getString(R.string.dialog_tile_state_picker_always_on),
//                    context.getString(R.string.dialog_tile_state_picker_on_when_charging),
//                    context.getString(R.string.dialog_tile_state_picker_always_off)
//                ),
//                preferences.getInt("tileState", 0),
//                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
//                    choice[0] = which
//                })
//            .setNegativeButton(
//                R.string.cancel,
//                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
//            .setPositiveButton(
//                R.string.ok,
//                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
//                    preferences.edit().putInt("tileState", choice[0]).apply()
//                    completionHandler.run()
//                    dialog.dismiss()
//                })
//            .setCustomTitle(title)
//            .show()
//    }
//}
