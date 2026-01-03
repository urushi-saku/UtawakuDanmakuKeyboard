package keyboardcom.example

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "word_list_settings")

class WordRepository(private val context: Context) {

    private val wordsKey = stringSetPreferencesKey("word_set")

    val words: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[wordsKey]?.sorted() ?: emptyList()
        }

    suspend fun addWord(word: String) {
        context.dataStore.edit { settings ->
            val currentWords = settings[wordsKey] ?: emptySet()
            settings[wordsKey] = currentWords + word
        }
    }

    /**
     * 指定された単語をDataStoreから削除します。
     */
    suspend fun deleteWord(word: String) {
        context.dataStore.edit { settings ->
            val currentWords = settings[wordsKey] ?: return@edit
            settings[wordsKey] = currentWords - word
        }
    }
}
