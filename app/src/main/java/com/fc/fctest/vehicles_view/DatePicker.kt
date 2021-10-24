package com.fc.fctest.vehicles_view

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.widget.DatePicker
import java.util.*

class DatePicker (private val context: Context) : DatePickerDialog.OnDateSetListener {
    private var selection : Date? = null
    private var callback : ((Date) -> Unit)? = null

    fun showPickDate(callback: (Date) -> Unit) {
        if(this.callback != null)
            return

        this.callback = callback
        selection = null
        createDateDialog().show()
    }

    private fun createDateDialog(): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(context, this, year, month, day)
        dialog.setOnDismissListener{ onDismiss() }

        return dialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        callback?.let { it(calendar.time) }
    }

    private fun onDismiss() {
        callback = null
    }
}