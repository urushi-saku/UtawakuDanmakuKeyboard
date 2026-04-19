package keyboardcom.example

/**
 * キーボードから呼び出される単語選択専用のアクティビティ。
 * WordListActivityを継承し、マニフェストで独立したタスクとして設定することで
 * 終了後に元のアプリ（LINE等）へスムーズに戻れるようにします。
 */
class WordListPickerActivity : WordListActivity()
