var path = require('path');

config.devServer = ({
    open: {
        // The name of the browser is different on different operating systems
        app: {
            name: "google-chrome"
        },
    },
    // These headers are included here to be able to use SharedArrayBuffer: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/SharedArrayBuffer
    headers: {
        'Cross-Origin-Opener-Policy': 'same-origin',
        'Cross-Origin-Embedder-Policy': 'require-corp'
    },
    static: [
        path.join(__dirname, '../../../../webpitch2/build/processedResources/js/main')
    ]
    // TODO Is there a better way to specify the path?
    // static: path.join(__dirname, '../../../../synthesizerbrowser/build/processedResources/js/main')
});
