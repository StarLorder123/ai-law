const path = require('path');
const nodeExternals = require('webpack-node-externals');
const TerserPlugin = require('terser-webpack-plugin');
const WebpackObfuscator = require('webpack-obfuscator');

module.exports = {
    mode: 'production',
    entry: './src/app.ts',
    target: 'node',
    externals: [nodeExternals()], // 排除 Node.js 原生模块和依赖
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: 'ts-loader',
                exclude: /node_modules/,
            },
        ],
    },
    resolve: {
        extensions: ['.ts', '.js'],
    },
    output: {
        filename: 'index.js',
        path: path.resolve(__dirname, 'out'),
    },
    optimization: {
        minimize: true,
        minimizer: [
            new TerserPlugin({
                terserOptions: {
                    compress: {
                        // drop_console: true,
                        // drop_debugger: true,
                    },
                    mangle: true, // 启用变量名混淆
                },
            }),
        ],
    },
    plugins: [
        new WebpackObfuscator(
            {
                rotateStringArray: true, // 启用字符串数组旋转
                stringArray: true,       // 将字符串转换为数组
                stringArrayEncoding: ['base64'], // 对字符串数组进行 Base64 编码
                stringArrayThreshold: 0.75,      // 将 75% 的字符串加入数组
                compact: true,           // 启用代码压缩
                deadCodeInjection: true, // 注入无用代码以增加混淆
                deadCodeInjectionThreshold: 0.4, // 注入无用代码的比例
                controlFlowFlattening: true,    // 启用控制流扁平化
                controlFlowFlatteningThreshold: 0.75, // 控制流扁平化的强度
                debugProtection: true,         // 启用调试保护
                debugProtectionInterval: 5, // 循环检测调试器
            },
            ['excluded_bundle.js'] // 排除不需要混淆的文件
        ),
    ]
};