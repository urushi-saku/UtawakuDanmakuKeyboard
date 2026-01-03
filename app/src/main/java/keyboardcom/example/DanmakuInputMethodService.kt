package keyboardcom.example

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * このクラスは、カスタムキーボードの心臓部です。
 * InputMethodServiceを継承することで、Androidシステム全体で
 * 使用できる入力メソッド（IME）として機能します。
 */
class DanmakuInputMethodService : InputMethodService() {

    /**
     * キーボードのUIが作成されるときに呼び出される、最も重要なメソッドの一つです。
     * ここで返されるViewが、キーボードとして画面の下部に表示されます。
     */
    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)

        ViewCompat.setOnApplyWindowInsetsListener(keyboardView) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, bottomInset)
            insets
        }

        val leftButton = keyboardView.findViewById<Button>(R.id.button_left)
        val rightButton = keyboardView.findViewById<Button>(R.id.button_right)

        leftButton.setOnClickListener {
            handleWordSelection(leftButton.text.toString())
        }

        rightButton.setOnClickListener {
            handleWordSelection(rightButton.text.toString())
        }

        return keyboardView
    }

    /**
     * 選択された単語（ボタンのテキスト）を入力欄に送信する処理です。
     * @param word 送信する文字列。
     */
    private fun handleWordSelection(word: String) {
        if (word == "+") {
            // --- ここからが新しい処理 ---
            // WordListActivity（単語リスト画面）を開くためのIntentを作成します。
            val intent = Intent(this, WordListActivity::class.java)

            // ServiceからActivityを起動するには、このフラグが必須です。
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // Activityを起動します。
            startActivity(intent)
            // --- ここまで ---
            return
        }

        currentInputConnection?.commitText(word, 1)
    }
}
