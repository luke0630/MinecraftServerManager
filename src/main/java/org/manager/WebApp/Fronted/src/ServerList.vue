<template>
  <h1>サーバーリスト</h1>
  <ul>
    <li v-for="(server, key) in servers" :key="key" class="content">
      <h2 class="server_name">{{ server.displayServerName }} ({{ key }})</h2>
      <div class="description">
        <h2 :class="server.isOnline ? 'online' : 'offline'">
          {{ server.isOnline ? "〇オンライン" : "✖オフライン" }}
        </h2>
        <div v-if="server.isOnline && server.serverData">
          <h3>プレイヤー数: {{ server.serverData.players.length }}</h3>
          <details class="player-list">
            <summary>プレイヤーリスト</summary>
            <ul v-if="server.serverData.players.length > 0">
              <li v-for="(player, index) in server.serverData.players" :key="index">
                {{ player.name }}
              </li>
            </ul>
          </details>
          <h3>バージョン: {{ server.serverData.version }}</h3>
          <h3>ステータス: {{ getServerStatus(server.serverData.status) }}</h3>
          <h3>プラグインの数: {{ server.serverData.plugins.length }}</h3>
          <details class="player-list">
            <summary>プラグインリスト</summary>
            <ul v-if="server.serverData.plugins.length > 0">
              <li v-for="(plugin, index) in server.serverData.plugins" :key="index">
              <details class="plugin">
                <summary>{{ plugin.pluginName }}</summary>
                <li>バージョン: {{ plugin.version }}</li>
                <li>概要: {{ plugin.description }}</li>
                <ul v-if="plugin.authors.length > 1">
                  <details>
                    <summary>製作者</summary>
                    <li v-for="(author, index) in plugin.authors" :key="index">
                      製作者: {{ author }}
                    </li>  
                  </details>
                </ul>
                <ul v-else>
                  <ul v-if="plugin.authors.length > 0">
                    <li v-for="(author, index) in plugin.authors" :key="index">
                      製作者: {{ author }}
                    </li>
                  </ul>
                  <p v-else>
                    製作者: 
                  </p>
                </ul>
              </details>
              </li>
            </ul>
          </details>
        </div>
      </div>
    </li>
  </ul>
</template>

<script>
export default {
  data() {
    return {
      servers: {}, // サーバーデータを保持するオブジェクト
    };
  },
  mounted() {
    // 初回ロード時にデータを取得する
    this.connectToWebSocket();
  },
  methods: {
    connectToWebSocket() {
      fetch('/api/websocket-address')
        .then(response => response.json())
        .then(data => {
          const host = data.host;
          const port = data.port;
          const address = `${host}:${port}`;

          const connection = new WebSocket(`ws://${address}`);

          connection.onmessage = (event) => {
            this.fetchStatus(event.data);
          };

          connection.onerror = (event) => {
            console.error(event.data);
          }
        })
        .catch(error => {
          console.error('Error fetching data:', error);
        });
    },
    fetchStatus(json) {
      const data = JSON.parse(json);
      // this.$store.dispatch("updateServers", data);
      this.servers = data; // サーバーデータを保持
    },
    getServerStatus(status) {
      switch (status) {
        case "STARTING":
          return "起動中";
        case "RUNNING":
          return "稼働中";
        default:
          return "不明";
      }
    },
    goToServerDetail(serverKey) {
      this.$router.push({ name: "ServerDetail", params: { serverKey } });
    }
  },
};
</script>

<style>
  html {
    background-color: #1c1c1c;
  }

  h1 {
    margin-top: 12px;
    text-align: center;
    color: white;
  }

  ul {
    text-align: center;
    margin: 0 auto;
    width: fit-content;
    list-style-type: none;
    padding: 0;
  }

  li {
    
    border-radius: 5px;
  }

  .online {
    color: rgb(9, 199, 9);
  }

  .offline {
    color: rgb(147, 147, 147);
  }

  .server_name {
    border-radius: 12px 12px 0px 0px;
    padding: 10px 30px 0px 30px;
    margin-top: 22px;
    border-bottom: 4px solid white;
    color: white;
  }

  .description {
    height: fit-content;
    border-top: none;
    color: white;
  }

  .player-list summary {
    cursor: pointer;
  }

  .plugin {
    border: 2px solid white;
    padding: 0px 12px 0px 12px;
  }
</style>
