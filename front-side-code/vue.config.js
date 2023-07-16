module.exports = {
    publicPath: "./",
    assetsDir: "static",
    outputDir: 'dist',
    pages: {
        index: {
            entry: 'src/main.js',       //入口文件
            title: "Q&A"
        }
    },
    pwa: {
        iconPaths: {
            favicon32: 'favicon.ico',
            favicon16: 'favicon.ico',
            appleTouchIcon: 'favicon.ico',
            maskIcon: 'favicon.ico',
            msTileImage: 'favicon.ico'
        }
    }
}
