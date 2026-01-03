package keyboardcom.example

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import keyboardcom.example.databinding.ActivityWordListBinding
import kotlinx.coroutines.launch

/**
 * 単語リストの表示と管理を行うActivity。
 * このActivityは2つのモードで動作する。
 *
 * 1. 単語管理モード (通常起動):
 *    - アプリアイコンから直接起動された場合。
 *    - ツールバー、追加、削除、ヘルプ機能がすべて有効になる。
 *
 * 2. 単語選択モード (ピッカーとして起動):
 *    - キーボードから呼び出された場合 (`WordListPickerActivity`経由)。
 *    - ダイアログ風に表示され、単語を1つ選択してキーボードに返すことだけを目的とする。
 */
class WordListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordListBinding
    private lateinit var adapter: WordListAdapter
    private val wordRepository by lazy { WordRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // どのボタン（左 or 右）から呼び出されたかを示すIntent Extraを取得
        val buttonTarget = intent.getStringExtra("BUTTON_TARGET")

        // buttonTargetがnullの場合、通常の「単語管理モード」として動作
        if (buttonTarget == null) {
            setSupportActionBar(binding.toolbar)
            title = "単語の管理"
        }

        // RecyclerViewのアダプターを初期化
        adapter = WordListAdapter(
            // 単語がクリックされたときの処理
            onWordClick = { word ->
                // 「単語選択モード」の場合のみ、クリック処理を実行
                if (buttonTarget != null) {
                    // 選択された単語をSharedPreferencesに保存
                    val prefs = getSharedPreferences(DanmakuInputMethodService.PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit().putString(buttonTarget, word).apply()

                    // ユーザーにフィードバックを表示
                    val buttonName = if (buttonTarget == DanmakuInputMethodService.KEY_LEFT_WORD) "左" else "右"
                    Toast.makeText(this, "「$word」を${buttonName}ボタンに設定しました", Toast.LENGTH_SHORT).show()

                    // 選択画面を閉じる
                    finish()
                }
            },
            // 単語が長押しされたときの処理（「単語管理モード」でのみ意味を持つ）
            onWordLongClick = { word ->
                showDeleteConfirmDialog(word)
            }
        )

        binding.wordRecyclerView.adapter = adapter
        binding.wordRecyclerView.layoutManager = LinearLayoutManager(this)

        // DataStoreから単語リストの変更を監視し、UIに反映する
        lifecycleScope.launch {
            wordRepository.words.collect { words ->
                adapter.submitList(words) // リストの差分更新

                // リストが空かどうかで、RecyclerViewと「空です」メッセージの表示を切り替える
                val isEmpty = words.isEmpty()
                binding.wordRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
                binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }

        // フローティングアクションボタン（単語追加）のクリックリスナー
        binding.fabAddWord.setOnClickListener {
            showAddWordDialog()
        }
    }

    /**
     * ツールバーにヘルプメニューを表示する。
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // 「単語選択モード」ではメニューを表示しない
        if (intent.getStringExtra("BUTTON_TARGET") != null) {
            return false
        }
        menuInflater.inflate(R.menu.word_list_menu, menu)
        return true
    }

    /**
     * ツールバーのメニュー項目がタップされたときの処理。
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_help -> {
                showHelpDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 使い方を説明するヘルプダイアログを表示する。
     */
    private fun showHelpDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("使い方")
            .setMessage(
                "■ 単語をボタンに設定する方法\n"
                        + "1. キーボードの「+」ボタンを長押しします。\n"
                        + "2. この単語リストが表示されたら、設定したい単語をタップします。\n\n"
                        + "■ 新しい単語を追加する方法\n"
                        + "この画面の右下にある「+」ボタンから、新しい単語を追加できます。\n\n"
                        + "■ 単語を削除する方法\n"
                        + "この画面で、削除したい単語を長押ししてください。"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * 新しい単語を追加するためのダイアログを表示する。
     */
    private fun showAddWordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_word, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_text_word)

        MaterialAlertDialogBuilder(this)
            .setTitle("新しい単語を追加")
            .setView(dialogView)
            .setNegativeButton("キャンセル", null)
            .setPositiveButton("追加") { _, _ ->
                val newWord = editText.text.toString()
                if (newWord.isNotBlank()) {
                    lifecycleScope.launch {
                        wordRepository.addWord(newWord)
                    }
                }
            }
            .show()
    }

    /**
     * 単語を削除する前に確認ダイアログを表示する。
     */
    private fun showDeleteConfirmDialog(word: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("単語の削除")
            .setMessage("「$word」をリストから削除しますか？")
            .setNegativeButton("キャンセル", null)
            .setPositiveButton("削除") { _, _ ->
                lifecycleScope.launch {
                    wordRepository.deleteWord(word)
                }
            }
            .show()
    }
}
