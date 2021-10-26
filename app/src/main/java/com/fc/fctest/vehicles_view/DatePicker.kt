package com.fc.fctest.vehicles_view

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.widget.DatePicker
import java.util.*

class DatePicker (private val context: Context) : DatePickerDialog.OnDateSetListener {
    private var callback : ((Date) -> Unit)? = null

    fun showPickDate(date: Date = Date(), callback: (Date) -> Unit) {
        if(this.callback != null)
            return

        this.callback = callback
        createDateDialog(date).show()
    }

    private fun createDateDialog(date: Date): Dialog {
        val calendar = Calendar.getInstance().apply {
            time = date
        }
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