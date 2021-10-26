package vehicles_model

sealed interface FetchResult<T> {
    class FetchData<T>(val data: T?): FetchResult<T>
    class FetchError<T>(val message: String): FetchResult<T> { fun isCanceled() = message.contains("Canceled") }
    class FetchPending<T>(val pending: Boolean): FetchResult<T>
}