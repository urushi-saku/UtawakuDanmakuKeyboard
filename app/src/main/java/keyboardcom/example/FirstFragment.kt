package keyboardcom.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import keyboardcom.example.databinding.FragmentFirstBinding

/**
 * このFragmentは、設定画面の最初のページ（単語リスト画面など）として機能します。
 * Navigation Component により、MainActivity内のコンテナに表示されます。
 *
 * 主な役割：
 * 1. 登録されている単語のリストを表示する。（今後実装）
 * 2. 単語の追加や編集画面へ遷移するボタンを配置する。
 */
class FirstFragment : Fragment() {

    // View Binding を使用するための変数です。
    // _bindingは、onCreateViewからonDestroyViewまでの間のみ有効です。
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    /**
     * FragmentのUIが初めて描画されるときに呼び出されます。
     * ここでレイアウトファイル（fragment_first.xml）をViewオブジェクトに変換（inflate）します。
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * onCreateViewの直後に呼び出され、Viewの初期設定を行います。
     * ここでクリックリスナーなどを設定するのが一般的です。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fragment_first.xml内の"button_first"がクリックされたときの処理です。
        // findNavController()を使って、SecondFragmentへの画面遷移を実行します。
        // この遷移ルールは、res/navigation/nav_graph.xmlで定義されています。
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    /**
     * FragmentのViewが破棄されるときに呼び出されます。
     * View Bindingのインスタンスを解放し、メモリリークを防ぎます。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
