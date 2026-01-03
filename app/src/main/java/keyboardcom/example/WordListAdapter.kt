package keyboardcom.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * 単語リストをRecyclerViewに表示するためのアダプター。
 * ListAdapterを継承することで、リストの差分計算と更新を効率的に行う（DiffUtil）。
 * これにより、リスト項目が追加・削除された際のアニメーションなどがスムーズになる。
 *
 * @param onWordClick リストの項目がタップされたときに呼び出されるコールバック関数。
 * @param onWordLongClick リストの項目が長押しされたときに呼び出されるコールバック関数。
 */
class WordListAdapter(
    private val onWordClick: (String) -> Unit,
    private val onWordLongClick: (String) -> Unit
) : ListAdapter<String, WordListAdapter.WordViewHolder>(WordDiffCallback()) {

    /**
     * RecyclerViewの各リスト項目のViewを保持するクラス。
     */
    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.word_text_view)
    }

    /**
     * ViewHolderが新しく作成されるときに呼び出される。
     * word_list_item.xmlからレイアウトを読み込んでViewHolderを生成する。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.word_list_item, parent, false)
        return WordViewHolder(view)
    }

    /**
     * ViewHolderにデータがバインド（関連付け）されるときに呼び出される。
     * @param holder 表示するViewHolder
     * @param position リスト内の位置
     */
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = getItem(position) // 現在の位置の単語データを取得
        holder.textView.text = word

        // 短いタップ（クリック）のリスナーを設定
        holder.itemView.setOnClickListener {
            onWordClick(word)
        }

        // 長押しのリスナーを設定
        holder.itemView.setOnLongClickListener {
            onWordLongClick(word)
            true // trueを返し、イベントがここで消費されたことをシステムに伝える
        }
    }
}

/**
 * ListAdapterがリストの差分を計算するために使用するコールバッククラス。
 * 新旧のリストを比較し、どの項目が追加・削除・変更されたかを判断する。
 */
class WordDiffCallback : DiffUtil.ItemCallback<String>() {
    // 同じアイテムかどうかをID（この場合は文字列そのもの）で判定
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    // 同じアイテムの内容が変更されたかどうかを判定
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
