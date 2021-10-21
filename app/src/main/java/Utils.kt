import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


const val serverDatePattern = "yyyy-mm-dd hh:mm:ss+zzzz"


fun getDate(timestamp: String): Date? {
    val format = SimpleDateFormat(serverDatePattern, Locale.ENGLISH)
    format.timeZone = TimeZone.getTimeZone("GMT")

    return try { format.parse(timestamp) }
    catch (e: ParseException) { null }
}


