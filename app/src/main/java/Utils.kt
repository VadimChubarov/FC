
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val serverDatePattern = "yyyy-MM-DD HH:mm:ssZ"


fun getDate(timestamp: String): Date? {
    val format = SimpleDateFormat(serverDatePattern, Locale.ENGLISH)
    return try { format.parse(timestamp) }
    catch (e: ParseException) { null }
}

fun getDateString(date: Date, format: String): String? {
    return try { SimpleDateFormat(format, Locale.ENGLISH).format(date) }
    catch (e: ParseException) { null }
}

fun setTime(date: Date, hours: Int, minutes: Int, seconds: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, hours)
    calendar.set(Calendar.MINUTE, minutes)
    calendar.set(Calendar.SECOND, seconds)

    return Date().apply {
        time = calendar.time.time
    }
}

fun addDays(date: Date, days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date.clone() as Date
    calendar.add(Calendar.DATE, 1)
    return calendar.time
}


