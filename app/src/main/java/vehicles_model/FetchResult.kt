package vehicles_model

sealed interface FetchResult<T> {
    class FetchData<T>(val data: T? = null): FetchResult<T>
    class FetchError<T>(val message: String): FetchResult<T>
    class FetchPending<T>(val pending: Boolean): FetchResult<T>
}