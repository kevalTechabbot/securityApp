//package com.learning.android.chat.preferences
//
//import android.R
//import android.content.Context
//
//object TileTextDisambiguationDialog {
//    fun show(context: Context) {
//        val choice = intArrayOf(0)
//
//        val title: TextView = TextView(context)
//        // You Can Customise your Title here
//        title.setText(R.string.dialog_tile_text_disambiguation_title)
//        title.setGravity(Gravity.CENTER)
//        title.setPadding(60, 60, 60, 10)
//        title.setTextSize(24f)
//
//        MaterialAlertDialogBuilder(context)
//            .setSingleChoiceItems(
//                arrayOf<String>(
//                    context.getString(R.string.bottom_sheet_tile_text_disambiguation_charging_text_title),
//                    context.getString(R.string.bottom_sheet_tile_text_disambiguation_discharging_text_title)
//                ),
//                0,
//                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
//                    choice[0] = which
//                })
//            .setNegativeButton(
//                R.string.cancel,
//                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
//            .setPositiveButton(
//                R.string.ok,
//                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
//                    context.startActivity(
//                        Intent(
//                            context,
//                            EditTileTextActivity::class.java
//                        ).putExtra("isEditingChargingText", choice[0] == 0)
//                    )
//                    dialog.dismiss()
//                })
//            .setCustomTitle(title)
//            .show()
//    }
//}
