import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val serverDatePattern = "yyyy-MM-DD HH:mm:ssZ"


fun getDate(timestamp: String): Date? {
    val format = SimpleDateFormat(serverDatePattern, Locale.ENGLISH)
    return try { format.parse(timestamp) }
    catch (e: ParseException) { null }
}


