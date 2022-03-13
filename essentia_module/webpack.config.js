//webpack.config.js
const path = require('path');

module.exports = {
    mode: "development",
    devtool: "inline-source-map",
    entry: {
        main: "./src/essentiaModule.ts",
    },
    output: {
        path: path.resolve(__dirname, './dist'),
        filename: "audio-worker.js",
        library: {
            name: "TestAudioProcessor",
            type: "umd"
        }
    },
    resolve: {
        extensions: [".ts", ".tsx", ".js"],
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                loader: "ts-loader"
            }
        ]
    }
};
