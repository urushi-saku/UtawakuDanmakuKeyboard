package keyboardcom.example

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DanmakuInputMethodService : InputMethodService() {

    companion object {
        const val PREFS_NAME = "DanmakuKeyboardPrefs"
        const val KEY_LEFT_WORD = "left_word"
        const val KEY_RIGHT_WORD = "right_word"
    }

    private lateinit var leftButton: Button
    private lateinit var rightButton: Button

    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        leftButton = keyboardView.findViewById(R.id.button_left)
        rightButton = keyboardView.findViewById(R.id.button_right)

        ViewCompat.setOnApplyWindowInsetsListener(keyboardView) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, bottomInset)
            insets
        }

        // --- ここから呼び出し方が変更されます ---
        leftButton.setOnLongClickListener {
            val intent = Intent()
            // エイリアスをクラス名（文字列）で指定して呼び出します
            intent.setClassName(this, "keyboardcom.example.WordListPickerActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("BUTTON_TARGET", KEY_LEFT_WORD)
            startActivity(intent)
            true
        }

        rightButton.setOnLongClickListener {
            val intent = Intent()
            // エイリアスをクラス名（文字列）で指定して呼び出します
            intent.setClassName(this, "keyboardcom.example.WordListPickerActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("BUTTON_TARGET", KEY_RIGHT_WORD)
            startActivity(intent)
            true
        }
        // --- ここまで ---

        leftButton.setOnClickListener {
            val word = leftButton.text.toString()
            if (word != "+") {
                currentInputConnection?.commitText(word, 1)
            }
        }

        rightButton.setOnClickListener {
            val word = rightButton.text.toString()
            if (word != "+") {
                currentInputConnection?.commitText(word, 1)
            }
        }

        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        leftButton.text = prefs.getString(KEY_LEFT_WORD, "+")
        rightButton.text = prefs.getString(KEY_RIGHT_WORD, "+")
    }
}
