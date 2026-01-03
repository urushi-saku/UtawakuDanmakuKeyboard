package keyboardcom.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class WordListAdapter(
    private val onWordClick: (String) -> Unit,
    private val onWordLongClick: (String) -> Unit // 長押しイベント用のコールバックを追加
) : ListAdapter<String, WordListAdapter.WordViewHolder>(WordDiffCallback()) {

    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.word_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.word_list_item, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = getItem(position)
        holder.textView.text = word

        // 短いタップの処理
        holder.itemView.setOnClickListener {
            onWordClick(word)
        }

        // 長押しの処理を追加
        holder.itemView.setOnLongClickListener {
            onWordLongClick(word)
            true // イベントを消費したことを示す
        }
    }
}

class WordDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
