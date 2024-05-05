# discord-user-watchdog
discordユーザーのオンラインステータスが変化時に特定のチャンネルにメッセージを送信するbotプログラムです。  
友達の死活監視等にご利用ください。

## コンパイル方法
```
mvn package
```
さすれば、target/ディレクトリ内に`discord-user-watchdog-1.0.jar`が作成されることであろう。

## 使用法
```
使い方: java -jar discord-user-watchdog-1.0.jar <Discordのトークン> <チャンネルID> <観測対象のユーザーID> [--offline] [--online] [--afk] [--help]
```
観測対象のユーザーIDは、`123456789,234567891,345678912`のようにカンマ区切りにすると、複数人を監視できます。
```
オプション:
    --offline  : オフラインになったときに通知するよ！
    --online   : オンラインになったときに通知するよ！
    --afk      : 離席中になったときに通知するよ！！
    --dnd      : 取り込み中になったときに通知するよ！
    --invisible: オンライン状態を隠したときに通知するよ！(動かないっぽい...)
    --all      : 上記すべてのオプションを適用するよ！
    --help   : このヘルプを表示するよ！
```

## BOTアカウント側の設定
以下の写真のように、PRESENCE INTENT、SERVER MEMBERS INTENT、MESSAGE CONTENT INTENTを全て有効にしてください！
![image](https://github.com/Shachiku-chan/discord-user-watchdog/assets/84060191/44dccb04-4bc0-4e86-8c3d-22ec1c767198)
