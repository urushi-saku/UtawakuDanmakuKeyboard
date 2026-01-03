package keyboardcom.example

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DanmakuInputMethodService : InputMethodService() {

    companion object {
        const val PREFS_NAME = "DanmakuKeyboardPrefs"
        const val KEY_LEFT_WORD = "left_word"
        const val KEY_RIGHT_WORD = "right_word"
        private const val SPAM_INTERVAL_MS = 300L // 連打間隔（0.3秒）
    }

    private lateinit var leftButton: Button
    private lateinit var rightButton: Button

    private var spammingButton: Button? = null
    private var spammingWord: String? = null
    private var originalButtonBackground: Drawable? = null

    // --- タイマー方式のための新しい変数 ---
    private val spamHandler = Handler(Looper.getMainLooper())
    private var spamRunnable: Runnable? = null
    // --- ここまで ---

    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        leftButton = keyboardView.findViewById(R.id.button_left)
        rightButton = keyboardView.findViewById(R.id.button_right)

        originalButtonBackground = leftButton.background

        ViewCompat.setOnApplyWindowInsetsListener(keyboardView) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, bottomInset)
            insets
        }

        leftButton.setOnLongClickListener { handleLongClick(KEY_LEFT_WORD); true }
        rightButton.setOnLongClickListener { handleLongClick(KEY_RIGHT_WORD); true }

        leftButton.setOnClickListener { handleButtonClick(leftButton) }
        rightButton.setOnClickListener { handleButtonClick(rightButton) }

        return keyboardView
    }

    private fun handleButtonClick(clickedButton: Button) {
        val word = clickedButton.text.toString()
        if (word == "+") return

        if (spammingButton == clickedButton) {
            stopSpamMode()
        } else {
            stopSpamMode() // 念のため、他のモードを停止してから
            startSpamMode(clickedButton, word) // 新しいモードを開始
        }
    }

    /**
     * 連打モードを開始し、タイマーを起動する
     */
    private fun startSpamMode(button: Button, word: String) {
        spammingButton = button
        spammingWord = word
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.spam_mode_active))

        // --- タイマー処理を開始 ---
        spamRunnable = Runnable {
            checkAndSendSpamWord() // 送信チェック処理を呼び出し
            // 指定した時間後にもう一度このRunnableを実行するように予約する
            spamHandler.postDelayed(spamRunnable!!, SPAM_INTERVAL_MS)
        }
        // すぐに最初の実行を開始
        spamHandler.post(spamRunnable!!)
    }

    /**
     * 連打モードを停止し、タイマーを破棄する
     */
    private fun stopSpamMode() {
        // --- タイマー処理を停止・破棄 --- 
        spamRunnable?.let { spamHandler.removeCallbacks(it) }
        spamRunnable = null

        spammingButton?.background = originalButtonBackground
        spammingButton = null
        spammingWord = null
    }

    /**
     * 【新しいロジック】入力欄の状態をチェックして、送信を判断する
     */
    private fun checkAndSendSpamWord() {
        val wordToSend = spammingWord ?: return
        val ic = currentInputConnection ?: return

        // 入力欄の現在のテキストをすべて取得する
        val currentText = ic.getExtractedText(ExtractedTextRequest(), 0)?.text?.toString() ?: ""

        // 入力欄が「空」または「これから送る単語そのもの」の場合のみ、送信処理を実行
        if (currentText.isEmpty() || currentText == wordToSend) {
            sendSpamWord(wordToSend)
        }
    }

    /**
     * 【新しいロジック】実際に単語を送信する処理
     */
    private fun sendSpamWord(word: String) {
        val ic = currentInputConnection ?: return

        ic.beginBatchEdit() // 複数の編集処理を一つにまとめる
        // 1. まず入力欄を完全にクリアする（カーソル前後を大きく削除）
        ic.deleteSurroundingText(1000, 1000)
        // 2. 新しい単語を送信する
        ic.commitText(word, 1)
        ic.endBatchEdit() // 編集を確定

        // 3. Enterキーを押す処理（変更なし）
        val editorInfo = currentInputEditorInfo ?: return
        val actionId = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED && actionId != EditorInfo.IME_ACTION_NONE) {
            ic.performEditorAction(actionId)
        } else if ((editorInfo.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0) {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
        }
    }

    // onUpdateSelection はもう不要になったので削除しました

    private fun handleLongClick(buttonTarget: String) {
        stopSpamMode()
        val intent = Intent()
        intent.setClassName(this, "keyboardcom.example.WordListPickerActivity")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("BUTTON_TARGET", buttonTarget)
        startActivity(intent)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (!restarting) {
            stopSpamMode()
        }
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        leftButton.text = prefs.getString(KEY_LEFT_WORD, "+")
        rightButton.text = prefs.getString(KEY_RIGHT_WORD, "+")
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        stopSpamMode()
    }
}
