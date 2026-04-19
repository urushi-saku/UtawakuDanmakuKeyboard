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

// open を追加して WordListPickerActivity から継承できるようにする
open class WordListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordListBinding
    private lateinit var adapter: WordListAdapter
    private val wordRepository by lazy { WordRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonTarget = intent.getStringExtra("BUTTON_TARGET")

        setSupportActionBar(binding.toolbar)
        if (buttonTarget != null) {
            title = "単語を選択"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            title = "単語の管理"
        }

        adapter = WordListAdapter(
            onWordClick = { word ->
                if (buttonTarget != null) {
                    val prefs = getSharedPreferences(DanmakuInputMethodService.PREFS_NAME, Context.MODE_PRIVATE)
                    val targetKey = if (buttonTarget == "left_word") {
                        DanmakuInputMethodService.KEY_LEFT_WORD
                    } else {
                        DanmakuInputMethodService.KEY_RIGHT_WORD
                    }

                    prefs.edit().putString(targetKey, word).apply()

                    val buttonName = if (buttonTarget == "left_word") "左" else "右"
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
                val isEmpty = words.isEmpty()
                binding.wordRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
                binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }

        binding.fabAddWord.setOnClickListener {
            showAddWordDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (intent.getStringExtra("BUTTON_TARGET") != null) return false
        menuInflater.inflate(R.menu.word_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_help -> {
                showHelpDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showHelpDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("使い方")
            .setMessage("■ 設定方法\nキーボードの「+」を長押しして単語を選んでください。")
            .setPositiveButton("OK", null)
            .show()
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
                    lifecycleScope.launch { wordRepository.addWord(newWord) }
                }
            }
            .show()
    }

    private fun showDeleteConfirmDialog(word: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("単語の削除")
            .setMessage("「$word」を削除しますか？")
            .setNegativeButton("キャンセル", null)
            .setPositiveButton("削除") { _, _ ->
                lifecycleScope.launch { wordRepository.deleteWord(word) }
            }
            .show()
    }
}
