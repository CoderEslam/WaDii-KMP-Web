// Dev-server proxy: forward /api/* → Spring backend (strips /api prefix)
config.devServer = Object.assign({}, config.devServer, {
    port: 3005,
    proxy: [
        {
            context: ["/api"],
            target: "http://192.168.1.30:8080",
            changeOrigin: true,
            pathRewrite: { "^/api": "" }
        }
    ]
});
