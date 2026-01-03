package keyboardcom.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import keyboardcom.example.databinding.ActivityWordListBinding

/**
 * 単語リストを管理するための画面（Activity）です。
 * この画面で、ユーザーは単語の追加、選択、削除を行います。
 */
class WordListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordListBinding

    /**
     * Activityが作成されるときに呼び出されます。
     * ここでレイアウトの初期化や、UIコンポーネントの設定を行います。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // View Bindingを使ってレイアウトファイルをインフレートし、ビューにアクセスできるようにします。
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerViewの初期設定（今は空っぽですが、後でアダプターをセットします）
        binding.wordRecyclerView.layoutManager = LinearLayoutManager(this)

        // 新規単語追加ボタン（FAB）がクリックされたときの処理（今は何もしません）
        binding.fabAddWord.setOnClickListener {
            // TODO: 新規単語を追加するためのダイアログなどを表示する処理を実装します。
        }
    }
}
