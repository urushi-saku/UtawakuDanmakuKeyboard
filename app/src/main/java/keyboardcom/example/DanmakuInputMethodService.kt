package keyboardcom.example

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * このクラスは、カスタムキーボードの心臓部です。
 * InputMethodServiceを継承することで、Androidシステム全体で
 * 使用できる入力メソッド（IME）として機能します。
 *
 * 主な役割：
 * 1. キーボードのUI（見た目）を作成し、画面に表示する。
 * 2. ユーザーの操作（リスト項目のタップなど）を検知する。
 * 3. 選択された単語を、現在アクティブな入力欄（例：LINEやブラウザのテキストボックス）に送信する。
 */
class DanmakuInputMethodService : InputMethodService() {

    /**
     * キーボードのUIが作成されるときに呼び出される、最も重要なメソッドの一つです。
     * ここで返されるViewが、キーボードとして画面の下部に表示されます。
     *
     * @return キーボードのレイアウトを含むViewオブジェクト。
     */
    override fun onCreateInputView(): View {
        // layoutInflaterを使って、XMLで定義されたレイアウトファイルをViewオブジェクトに変換します。
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)

        // --- ここからが今回追加する処理 ---

        // 1. 表示する単語のリストを準備します。
        //    今回は動作確認のため、いくつかのサンプル単語をハードコードしています。
        //    将来的には、このリストをデータベースなどから動的に読み込むように変更します。
        val wordList = listOf("単語A", "単語B", "単語C", "サンプル", "キーボード", "テスト")

        // 2. レイアウトファイル(keyboard_view.xml)からRecyclerViewのインスタンスを取得します。
        val recyclerView = keyboardView.findViewById<RecyclerView>(R.id.word_list_view)

        // 3. RecyclerViewのレイアウト方法を設定します。
        //    LinearLayoutManagerは、一般的な縦方向のリストを表示する場合に使用します。
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 4. 作成したWordListAdapterをRecyclerViewに設定します。
        //    アダプターに単語リストと、項目がクリックされた際の処理を渡します。
        recyclerView.adapter = WordListAdapter(wordList) { selectedWord ->
            // 5. リストの単語がクリックされたら、この処理が実行されます。
            handleWordSelection(selectedWord)
        }

        return keyboardView
    }

    /**
     * 選択された単語を入力欄に送信する処理です。
     * @param word 送信する文字列。
     */
    private fun handleWordSelection(word: String) {
        // currentInputConnectionは、現在アクティブな入力欄との通信路を表します。
        // これがnullでないことを確認してから処理を行います。
        currentInputConnection?.let {
            // commitText()メソッドで、入力欄にテキストを送信します。
            // 第2引数の '1' は、テキスト入力後にカーソルをテキストの末尾に置くことを意味します。
            it.commitText(word, 1)

            // 必要に応じて、単語を送信した後にキーボードを非表示にすることもできます。
            // requestHideSelf(0)
        }
    }
}
