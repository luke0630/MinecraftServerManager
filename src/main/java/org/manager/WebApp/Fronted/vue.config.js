const { defineConfig } = require('@vue/cli-service')
const path = require('path');
module.exports = defineConfig({
  transpileDependencies: true
})

module.exports = {
  // ビルドの出力先を指定
  outputDir: path.resolve(__dirname, '../../../../../resources/static'), 
};
