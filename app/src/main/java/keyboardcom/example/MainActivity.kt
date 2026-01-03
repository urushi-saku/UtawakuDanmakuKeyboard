package keyboardcom.example

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import keyboardcom.example.databinding.ActivityMainBinding

/**
 * このActivityは、アプリの「設定画面」として機能します。
 * ランチャーアイコンから起動されるメインの画面です。
 *
 * 主な役割：
 * 1. ユーザーがキーボードの辞書（単語リスト）を編集する画面を提供する。
 * 2. キーボードのその他の設定項目（将来的に追加）を提供する。
 * 3. アプリのUIの骨格（ツールバーやナビゲーションなど）を管理する。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding を使ってレイアウトファイル(activity_main.xml)と連携します。
        // これにより、findViewByIdを使わずにUI要素に安全にアクセスできます。
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ツールバーをこのActivityのアクションバーとして設定します。
        setSupportActionBar(binding.toolbar)

        // Navigation Component を使って、フラグメント間の画面遷移を管理します。
        // nav_host_fragment_content_main は、activity_main.xml内のNavHostFragmentのIDです。
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // フローティングアクションボタン（FAB）がクリックされたときの処理です。
        // 現状はサンプルコードのスナックバーが表示されます。
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
    }

    /**
     * アクションバーのメニューを作成します。
     * res/menu/menu_main.xml ファイルがここで読み込まれます。
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * アクションバーのメニュー項目がクリックされたときの処理を記述します。
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * アクションバーの「上へ」ボタン（←）が押されたときの処理です。
     * Navigation Component が画面遷移を制御します。
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
