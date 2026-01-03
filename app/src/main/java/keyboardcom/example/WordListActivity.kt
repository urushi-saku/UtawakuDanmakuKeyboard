package keyboardcom.example

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import keyboardcom.example.databinding.ActivityWordListBinding
import kotlinx.coroutines.launch

class WordListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordListBinding
    private lateinit var adapter: WordListAdapter
    private val wordRepository by lazy { WordRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonTarget = intent.getStringExtra("BUTTON_TARGET")

        // --- ここからが新しい処理 ---
        // ランチャーから起動された場合（管理モード）はツールバーをセットする
        if (buttonTarget == null) {
            setSupportActionBar(binding.toolbar)
            title = "単語の管理"
        }
        // --- ここまで ---

        adapter = WordListAdapter(
            onWordClick = { word ->
                // キーボードから起動された場合（選択モード）のみ単語を設定して閉じる
                if (buttonTarget != null) {
                    val prefs = getSharedPreferences(DanmakuInputMethodService.PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit().putString(buttonTarget, word).apply()

                    val buttonName = if (buttonTarget == DanmakuInputMethodService.KEY_LEFT_WORD) "左" else "右"
                    Toast.makeText(this, "「$word」を${buttonName}ボタンに設定しました", Toast.LENGTH_SHORT).show()

                    finish()
                }
            },
            onWordLongClick = { word ->
                showDeleteConfirmDialog(word)
            }
        )

        binding.wordRecyclerView.adapter = adapter
        binding.wordRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            wordRepository.words.collect { words ->
                adapter.submitList(words)
            }
        }

        binding.fabAddWord.setOnClickListener {
            showAddWordDialog()
        }
    }

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
