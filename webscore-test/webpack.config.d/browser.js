var path = require('path');

config.devServer = ({
    open: {
        // The name of the browser is different on different operating systems
        app: {
            name: "google-chrome"
        },
    },
    static: [
        path.join(__dirname, '../../../../webscore-test/build/processedResources/js/main')
    ]
});