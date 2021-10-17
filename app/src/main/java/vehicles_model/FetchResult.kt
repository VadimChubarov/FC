package vehicles_model

class FetchResult<T>(val data: T? = null, val error: Error? = null) {
    fun hasData() = data != null
    fun hasError() = error != null
}

class Error(val message: String)