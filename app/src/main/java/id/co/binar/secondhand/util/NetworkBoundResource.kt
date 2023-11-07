package id.co.binar.secondhand.util

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: suspend () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
): Flow<Resource<ResultType>> = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (throwable: Throwable) {
            query().map { Resource.Error(throwable, it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}

inline fun <ResultType> networkResource(
    crossinline api: suspend () -> ResultType,
    crossinline response: suspend (ResultType) -> ResultType,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
): Flow<Resource<ResultType>> = flow {
    emit(Resource.Loading())
    try {
        val result = response(api())
        if (shouldFetch(result)) {
            emit(Resource.Success(result))
        } else {
            throw Exception()
        }
    } catch (ex: Exception) {
        emit(Resource.Error(ex))
    }
}