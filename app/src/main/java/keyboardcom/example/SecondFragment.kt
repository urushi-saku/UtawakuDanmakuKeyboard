package keyboardcom.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import keyboardcom.example.databinding.FragmentSecondBinding

/**
 * このFragmentは、設定画面の2ページ目（単語の新規登録や編集画面など）として機能します。
 * FirstFragmentから遷移してきます。
 *
 * 主な役割：
 * 1. 新しい単語を辞書に登録するための入力フォームを提供する。（今後実装）
 * 2. 既存の単語を編集するための入力フォームを提供する。（今後実装）
 */
class SecondFragment : Fragment() {

    // View Binding を使用するための変数です。
    // _bindingは、onCreateViewからonDestroyViewまでの間のみ有効です。
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    /**
     * FragmentのUIが初めて描画されるときに呼び出されます。
     * ここでレイアウトファイル（fragment_second.xml）をViewオブジェクトに変換（inflate）します。
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * onCreateViewの直後に呼び出され、Viewの初期設定を行います。
     * ここでクリックリスナーなどを設定するのが一般的です。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fragment_second.xml内の"button_second"がクリックされたときの処理です。
        // findNavController()を使って、FirstFragmentへ戻る画面遷移を実行します。
        // この遷移ルールは、res/navigation/nav_graph.xmlで定義されています。
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
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
