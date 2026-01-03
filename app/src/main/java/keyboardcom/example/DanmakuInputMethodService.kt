package keyboardcom.example

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * アプリのメイン機能であるカスタムキーボードを実装するクラス。
 * InputMethodServiceを継承して作成する。
 */
class DanmakuInputMethodService : InputMethodService() {

    // SharedPreferencesやIntentで使う定数
    companion object {
        const val PREFS_NAME = "DanmakuKeyboardPrefs"
        const val KEY_LEFT_WORD = "left_word"
        const val KEY_RIGHT_WORD = "right_word"
    }

    private lateinit var leftButton: Button
    private lateinit var rightButton: Button

    /**
     * キーボードのUIを作成するメソッド。
     * システムがキーボードを表示するときに呼び出される。
     */
    override fun onCreateInputView(): View {
        // keyboard_view.xmlからレイアウトを読み込む
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        leftButton = keyboardView.findViewById(R.id.button_left)
        rightButton = keyboardView.findViewById(R.id.button_right)

        // OSのナビゲーションバーなどにキーボードが隠れないように、下部にパディングを設定する
        ViewCompat.setOnApplyWindowInsetsListener(keyboardView) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, bottomInset)
            insets
        }

        // 左ボタンが長押しされたときの処理
        leftButton.setOnLongClickListener {
            // 単語選択画面(WordListPickerActivity)を起動する
            val intent = Intent()
            intent.setClassName(this, "keyboardcom.example.WordListPickerActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // IMEサービスからActivityを起動するには必須
            intent.putExtra("BUTTON_TARGET", KEY_LEFT_WORD) // 左ボタンのための選択だと伝える
            startActivity(intent)
            true // trueを返して、通常のクリックイベントが発動しないようにする
        }

        // 右ボタンが長押しされたときの処理（左ボタンと同じ）
        rightButton.setOnLongClickListener {
            val intent = Intent()
            intent.setClassName(this, "keyboardcom.example.WordListPickerActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("BUTTON_TARGET", KEY_RIGHT_WORD)
            startActivity(intent)
            true
        }

        // 左ボタンがクリックされたときの処理
        leftButton.setOnClickListener {
            handleButtonClick(leftButton)
        }

        // 右ボタンがクリックされたときの処理
        rightButton.setOnClickListener {
            handleButtonClick(rightButton)
        }

        return keyboardView
    }

    /**
     * ボタンクリック時の共通処理（単語入力と、賢い送信機能）
     */
    private fun handleButtonClick(button: Button) {
        val word = button.text.toString()
        if (word == "+") return // ボタンに単語が設定されていない場合は何もしない

        // 1. 単語を入力
        currentInputConnection?.commitText(word, 1)

        // 2. 入力先のアプリが要求するアクション（送信、検索など）、またはEnterキーの押下を実行
        val editorInfo = currentInputEditorInfo
        if (editorInfo != null) {
            // まず、エディタが指定した明示的なアクション（送信、検索など）を確認する
            val actionId = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
            if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED && actionId != EditorInfo.IME_ACTION_NONE) {
                // 明示的なアクションがあれば、それを実行する（最優先）
                currentInputConnection?.performEditorAction(actionId)
            } 
            // 明示的なアクションがなく、かつEnterキーが無効化されていない場合
            else if ((editorInfo.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0) {
                // Enterキーの押下イベント（ダウン→アップ）を直接送信する
                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
            }
        }
    }

    /**
     * キーボードが表示されるたびに呼び出されるメソッド。
     * ボタンに設定されている単語をSharedPreferencesから読み込んで表示を更新する。
     */
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        leftButton.text = prefs.getString(KEY_LEFT_WORD, "+")
        rightButton.text = prefs.getString(KEY_RIGHT_WORD, "+")
    }
}
