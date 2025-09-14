
// export default setupProxy;
const { createProxyMiddleware } = require('http-proxy-middleware');
module.exports = function (app) {
    app.use(
        '/llw',
        createProxyMiddleware({
            target: 'http://localhost:8080', //  API服务器port:8080', // 将此处替换为你的API服务器的URL
            changeOrigin: true,
            pathRewrite: {
                '^/llw': '', // 可选：重写请求路径，如果你希望去掉路径中的/api
            }
        })
    );
};
