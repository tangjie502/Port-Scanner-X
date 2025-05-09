const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
})
module.exports = {
  devServer: {
    host: '0.0.0.0', // 指定主机名，默认是 localhost
    port: 80, // 指定端口号
    https: false, // 是否启用 HTTPS
    open: true, // 是否在启动时自动打开浏览器
  }
};
