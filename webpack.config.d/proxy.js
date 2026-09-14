// Dev-server proxy: forward /api/* → Spring backend (strips /api prefix)
config.devServer = Object.assign({}, config.devServer, {
    port: 3005,
    proxy: [
        {
            context: ["/api"],
            target: "https://srv1881459.hstgr.cloud",
            changeOrigin: true,
            pathRewrite: { "^/api": "" }
        }
    ]
});
