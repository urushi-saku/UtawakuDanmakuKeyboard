package keyboardcom.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerViewのためのアダプタークラス。
 * このクラスは、データ（単語のリスト）とUI（RecyclerView）を繋ぐ「橋渡し役」です。
 *
 * 主な役割：
 * 1. リスト全体の項目数をシステムに伝える (getItemCount)。
 * 2. 各リスト項目の見た目（View）を生成する (onCreateViewHolder)。
 * 3. 各リスト項目に、対応するデータを表示する (onBindViewHolder)。
 */
class WordListAdapter(
    private val words: List<String>,
    private val onWordClick: (String) -> Unit
) : RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {

    /**
     * 各リスト項目（一行分）のViewを保持するためのクラス（インナークラス）。
     * ここで、リスト項目レイアウト(word_list_item.xml)内のUI要素（TextViewなど）への
     * 参照を保持します。
     */
    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.word_text_view)
    }

    /**
     * 新しいViewHolder（リスト一行分の見た目を保持するオブジェクト）が
     * 必要になったときに呼び出されます。
     *
     * @param parent 新しいViewが追加される親のViewGroup。
     * @param viewType Viewのタイプ。
     * @return 新しく作成されたWordViewHolderインスタンス。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        // word_list_item.xml レイアウトを inflate（オブジェクト化）して、
        // ViewHolder を作成します。
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.word_list_item, parent, false)
        return WordViewHolder(view)
    }

    /**
     * 指定された位置（position）のリスト項目にデータを表示するために呼び出されます。
     *
     * @param holder データを設定する対象のViewHolder。
     * @param position リスト内のデータ位置。
     */
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        // リストから対応する単語を取得します。
        val word = words[position]
        // ViewHolderが保持しているTextViewに、その単語のテキストを設定します。
        holder.textView.text = word
        // リスト項目全体がクリックされたときの処理を設定します。
        holder.itemView.setOnClickListener {
            onWordClick(word) // コンストラクタで渡されたコールバック関数を実行
        }
    }

    /**
     * リストに表示するデータ総数を返します。
     * RecyclerViewは、この数に基づいてリストのスクロールバーなどを計算します。
     *
     * @return 単語リストのサイズ。
     */
    override fun getItemCount() = words.size
}
