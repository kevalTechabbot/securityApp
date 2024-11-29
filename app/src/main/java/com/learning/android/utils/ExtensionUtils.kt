package com.learning.android.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.learning.android.R
import com.learning.android.utils.AppUtils.hideSoftKeyboard
import com.learning.android.utils.AppUtils.logI
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ExtensionUtils {

    object Common {
        fun View.show() {
            visibility = View.VISIBLE
        }

        fun View.gone() {
            visibility = View.GONE
        }

        fun View.hide() {
            visibility = View.INVISIBLE
        }

        fun Context.dpToPx(dp: Int): Int {
            return (dp * resources.displayMetrics.density).toInt()
        }

        fun Context.showConfirmationDialog(
            title: String, message: String, onYes: () -> Unit, onNo: (() -> Unit)? = null
        ) {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle(title).setMessage(message)
                .setPositiveButton(R.string.yes) { _, _ -> onYes() }
            dialogBuilder.setNegativeButton(R.string.no) { _, _ ->
                if (onNo != null) {
                    onNo()
                }
            }
            dialogBuilder.show()
        }
    }

    fun Context.showConfirmationDialogForSingleClick(
        title: String, message: String, onYes: (() -> Unit)? = null
    ) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title).setMessage(message)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (onYes != null)
                    onYes()
            }
        dialogBuilder.show()
    }


    object EditTextUtils {
        fun AppCompatEditText.clear() {
            setText("")
        }

        fun AppCompatEditText.getTextString(): String {
            return text.toString().trim()
        }

        fun AppCompatEditText.isEmpty(): Boolean {
            return getTextString().isEmpty()
        }

        fun AppCompatEditText.isNotEmpty(): Boolean {
            return !isEmpty()
        }

        fun AppCompatEditText.onTextChanged(callback: (String) -> Unit) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    callback(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        fun AppCompatEditText.onDone(callback: () -> Unit) {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    callback()
                    return@setOnEditorActionListener true
                }
                false
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun TextInputEditText.makeClickable(activity: Activity, callback: () -> Unit) {
            inputType = InputType.TYPE_NULL
            keyListener = null
            setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    //your code
                    activity.hideSoftKeyboard()
                    callback()
                }
                false
            }
        }
    }

    object TextViewUtils {
        fun AppCompatTextView.setUnderlineText(text: String) {
            val spannableString = SpannableString(text)
            spannableString.setSpan(UnderlineSpan(), 0, text.length, 0)
            setText(spannableString)
        }

        fun AppCompatTextView.setBoldText(text: String) {
            setTypeface(null, Typeface.BOLD)
            setText(text)
        }

        fun AppCompatTextView.setTextColorRes(context: Context, colorResId: Int) {
            setTextColor(ContextCompat.getColor(context, colorResId))
        }

        fun String.changeFontForSubstring(
            substring: String, typeface: Typeface
        ): SpannableStringBuilder {
            val start = indexOf(substring)
            val end = start + substring.length
            val spannableString = SpannableString(this)
            spannableString.setSpan(
                StyleSpan(typeface.style),
                start,
                end,
                SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE
            )
            return SpannableStringBuilder(spannableString)
        }

        fun AppCompatTextView.openDatePicker(defaultText: String, minimumDate: Boolean = false) {

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, monthOfYear, dayOfMonth ->

                    // Set the selected date to the TextView
                    calendar.set(year, monthOfYear, dayOfMonth)
                    this.text = dateFormat.format(calendar.time).toString()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            if (text != defaultText) {
                val mDate = dateFormat.parse(this.text.toString())
                val mCalendar = Calendar.getInstance()
                mCalendar.time = mDate!!
                val year = mCalendar.get(Calendar.YEAR)
                val month = mCalendar.get(Calendar.MONTH)
                val day = mCalendar.get(Calendar.DAY_OF_MONTH)
                logI("SelectedDate", "$year-$month-$day")
                datePickerDialog.updateDate(year, month, day)
            }
            if (minimumDate)
                datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            // Show the DatePickerDialog
            datePickerDialog.show()
        }
    }

    object RadioButtonUtils {
        fun RadioGroup.getSelectedRadioButton(): RadioButton? {
            val selectedId = checkedRadioButtonId
            if (selectedId != View.NO_ID) {
                return findViewById(selectedId)
            }
            return null
        }
    }

    object DateUtils {

        // Extension function to format a Date object to a string using a given pattern
        fun Date.format(pattern: String): String {
            val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
            return formatter.format(this)
        }

        // Extension function to parse a string to a Date object using a given pattern
        fun String.toDate(pattern: String): Date {
            val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
            return formatter.parse(this)!!
        }

        // Extension function to add or subtract days from a Date object
        operator fun Date.plus(days: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.time = this
            calendar.add(Calendar.DAY_OF_MONTH, days)
            return calendar.time
        }

        // Extension function to calculate the difference between two dates in days
        infix fun Date.daysUntil(otherDate: Date): Int {
            val diff = (otherDate.time - this.time) / (1000 * 60 * 60 * 24)
            return diff.toInt()
        }

        // Extension function to get the day of the week for a Date object
        fun Date.getDayOfWeek(): String {
            val daysOfWeek =
                listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            val calendar = Calendar.getInstance()
            calendar.time = this
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            return daysOfWeek[dayOfWeek - 1]
        }

        // Extension function to get the current time in a given timezone
        fun getTimeInTimeZone(timeZone: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            dateFormat.timeZone = TimeZone.getTimeZone(timeZone)
            return dateFormat.format(Date())
        }

        // Extension function to get the age based on a Date of Birth
        fun Date.getAge(): Int {
            val dob = Calendar.getInstance()
            dob.time = this
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            return age
        }

        // Extension function to check if it is today
        fun Calendar.isToday(): Boolean {
            val today = Calendar.getInstance(); return today.get(Calendar.DAY_OF_YEAR) == get(
                Calendar.DAY_OF_YEAR
            ) && today.get(Calendar.YEAR) == get(Calendar.YEAR)
        }
    }

    object Spinner {
        fun AppCompatSpinner.observePosition(callback: (Int) -> Unit) {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    callback.invoke(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    object IntentUtils {/*fun <T : Parcelable> Intent.putParcelableListExtra(name: String, list: ArrayList<T>?) {
            val bundle = Bundle()
            bundle.putParcelableArrayList(name, list)
            putExtra(name, bundle)
        }*/

        /*fun <T : Parcelable> Intent.getParcelableListExtra(name: String): ArrayList<T>? {
            val bundle = getBundleExtra(name)
            return bundle?.getParcelableArrayList(name)
        }*/

        fun <T : Serializable?> Intent.getSerializableExtr(keyName: String?, clazz: Class<T>): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getSerializableExtra(
                keyName, clazz
            )
            else getSerializableExtra(keyName) as T?
        }
    }
}