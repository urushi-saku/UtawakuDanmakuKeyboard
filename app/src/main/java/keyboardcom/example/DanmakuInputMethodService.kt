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
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DanmakuInputMethodService : InputMethodService() {

    companion object {
        const val PREFS_NAME = "DanmakuKeyboardPrefs"
        const val KEY_LEFT_WORD = "left_word"
        const val KEY_RIGHT_WORD = "right_word"
        private const val INTERVAL_AUTO_SEND_MS = 1000L // 自動送信時は1秒
        private const val INTERVAL_INPUT_ONLY_MS = 100L  // 入力のみ時は0.1秒で即装填
    }

    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var autoSendSwitch: SwitchCompat

    private var spammingButton: Button? = null
    private var spammingWord: String? = null
    private var originalButtonBackground: Drawable? = null

    private val spamHandler = Handler(Looper.getMainLooper())
    private var spamRunnable: Runnable? = null

    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        leftButton = keyboardView.findViewById(R.id.button_left)
        rightButton = keyboardView.findViewById(R.id.button_right)
        autoSendSwitch = keyboardView.findViewById(R.id.switch_auto_send)

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
            stopSpamMode()
            startSpamMode(clickedButton, word)
        }
    }

    private fun startSpamMode(button: Button, word: String) {
        spammingButton = button
        spammingWord = word
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.spam_mode_active))

        spamRunnable = object : Runnable {
            override fun run() {
                val isAutoSend = autoSendSwitch.isChecked
                checkAndProcessWord(isAutoSend)
                
                // モードに応じて待機時間を変える
                val interval = if (isAutoSend) INTERVAL_AUTO_SEND_MS else INTERVAL_INPUT_ONLY_MS
                spamHandler.postDelayed(this, interval)
            }
        }
        spamHandler.post(spamRunnable!!)
    }

    private fun stopSpamMode() {
        spamRunnable?.let { spamHandler.removeCallbacks(it) }
        spamRunnable = null

        spammingButton?.background = originalButtonBackground
        spammingButton = null
        spammingWord = null
    }

    private fun checkAndProcessWord(isAutoSend: Boolean) {
        val wordToSend = spammingWord ?: return
        val ic = currentInputConnection ?: return

        val currentText = ic.getExtractedText(ExtractedTextRequest(), 0)?.text?.toString() ?: ""

        // 入力欄が空、または既にその単語が入っている場合
        if (currentText.isEmpty() || currentText == wordToSend) {
            if (isAutoSend) {
                // 自動送信モード：入力して送信
                sendWordWithEnter(wordToSend)
            } else if (currentText.isEmpty()) {
                // 入力のみモード：空のときだけ入力（送信はしない）
                ic.commitText(wordToSend, 1)
            }
        }
    }

    private fun sendWordWithEnter(word: String) {
        val ic = currentInputConnection ?: return
        ic.beginBatchEdit()
        ic.deleteSurroundingText(1000, 1000)
        ic.commitText(word, 1)
        ic.endBatchEdit()

        val editorInfo = currentInputEditorInfo ?: return
        val actionId = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED && actionId != EditorInfo.IME_ACTION_NONE) {
            ic.performEditorAction(actionId)
        } else if ((editorInfo.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0) {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
        }
    }

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
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        leftButton.text = prefs.getString(KEY_LEFT_WORD, "+")
        rightButton.text = prefs.getString(KEY_RIGHT_WORD, "+")
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        stopSpamMode()
    }
}
