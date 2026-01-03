package keyboardcom.example

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Contextの拡張関数としてDataStoreのインスタンスを生成（シングルトン）
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "word_list_settings")

/**
 * 単語リストのデータ永続化（読み書き）を担当するクラス（リポジトリパターン）。
 * Jetpack DataStoreを使用して、単語データをデバイス内に保存する。
 * これにより、ActivityやServiceはデータの保存場所を意識せず、このクラス経由でデータ操作に専念できる。
 */
class WordRepository(private val context: Context) {

    // DataStoreにデータを保存するためのキーを定義
    private val wordsKey = stringSetPreferencesKey("word_set")

    /**
     * 保存されているすべての単語を、Flowとして提供する。
     * Flowを使うことで、データが変更されるたびに自動的に新しいリストがUIに通知される。
     * データはアルファベット順にソートして提供する。
     */
    val words: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[wordsKey]?.sorted() ?: emptyList()
        }

    /**
     * 新しい単語をDataStoreに追加する。
     * suspend関数であり、コルーチン内から呼び出す必要がある。
     */
    suspend fun addWord(word: String) {
        // .edit{} スコープ内で安全にデータの書き込みを行う
        context.dataStore.edit { settings ->
            val currentWords = settings[wordsKey] ?: emptySet()
            settings[wordsKey] = currentWords + word
        }
    }

    /**
     * 指定された単語をDataStoreから削除する。
     * suspend関数であり、コルーチン内から呼び出す必要がある。
     */
    suspend fun deleteWord(word: String) {
        context.dataStore.edit { settings ->
            val currentWords = settings[wordsKey] ?: return@edit
            settings[wordsKey] = currentWords - word
        }
    }
}
