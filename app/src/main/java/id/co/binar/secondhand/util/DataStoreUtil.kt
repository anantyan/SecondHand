package id.co.binar.secondhand.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject

const val DATASTORE_SETTINGS: String = "SETTINGS"
val TOKEN_ID = stringPreferencesKey("USER_TOKEN")
val USR_ID = intPreferencesKey("USER_ID")
val Context.dataStore by preferencesDataStore(DATASTORE_SETTINGS)

class DataStoreManager @Inject constructor(
    private val context: Context
) {
    fun setTokenId(value: String) = CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.setValue(TOKEN_ID, value)
    }

    fun setUsrId(value: Int) = CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.setValue(USR_ID, value)
    }

    fun getTokenId() = runBlocking { context.dataStore.getValue(TOKEN_ID, "").first() }
    fun getUsrId() = runBlocking { context.dataStore.getValue(USR_ID, -1).first() }
    fun clear() = runBlocking { context.dataStore.clear() }
}

fun <T> DataStore<Preferences>.getValue(
    key: Preferences.Key<T>,
    defaultValue: T
): Flow<T> = this.data
    .catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[key] ?: defaultValue
    }

suspend fun <T> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    this.edit { preferences ->
        preferences[key] = value
    }
}

suspend fun <T> DataStore<Preferences>.removeValue(key: Preferences.Key<T>) {
    this.edit { preferences ->
        preferences.remove(key)
    }
}

suspend fun DataStore<Preferences>.clear() {
    this.edit { preferences ->
        preferences.clear()
    }
}