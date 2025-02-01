MinecraftServerManager
====

マインクラフトサーバーを監視する管理ソフトウェアです

# 特徴
**以下の情報をwebから確認できます。**
> * サーバーのオフライン/オンライン
> * プレイヤーリスト
> * バージョン、サーバーのステータス(起動中、稼働中)
> * プラグインリスト(バージョン、概要、製作者、メインクラス)

**PlaceholderAPIに対応(StatusReporterを使用)**

サーバー内から状態を確認できます。
# インストール
## 1.jarファイルをダウンロード 

Releasesから以下の最新バージョンをダウンロード  
``MinecraftServerManager-x.x.x.jar``  
<https://github.com/luke0630/MinecraftServerManager/releases>
## 2.起動
以下のコマンドを実行し、ダウンロードしたjarを起動します(x.x.xは書き換えてください)
````
$ java -jar MinecraftServerManager-x.x.x.jar
````
ディレクトリに`config.yml`が生成されたらサーバーを停止します。(ctrl+c)
## 3.config.ymlの設定
config.yml内の設定のserverListに以下のように設定します。  
``` yaml
# マイクラサーバーのIP:サーバーのポート番号:名前:表示名
serverList:
  - localhost:25565:Main:メインサーバー
  - localhost:25565:Lobby:ロビーサーバー
```

### ポート番号を設定
初期値は8844ですが、もし既に使用されていたらそれを変更してください。
``` yaml
port: 8844
```

保存したら再度起動してください。

## 4.StatusReporterの導入
以下のプラグインを対象のマインクラフトサーバーの`plugins`フォルダに導入されてください。  
<https://github.com/luke0630/StatusReporter/releases>

起動してサーバーの`plugins`フォルダ内の`StatusReporter`フォルダにアクセスし、  
中にあるconfig.ymlを開きます。
```yaml
address: localhost:8844
```
初期値は`localhost:8844`です。  
addressにはMinecraftStatusManagerのアドレスを入力してください。  
変更をした場合サーバーを再起動してください。

## 5.管理画面へアクセスする
以下のURLでサイトにアクセスしてください。  
```
http://MinecraftServerManagerのIP:ポート番号
```  
ポート番号には、MinecraftServerManagerのconfig.ymlで設定した`port`を使用してください。  
アクセスして、サーバーの状態が表示されたら完了です。
# ライセンス
[MIT licensed](./LICENSE).
