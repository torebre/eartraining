var path = require('path');

config.devServer = ({
    open: {
        // The name of the browser is different on different operating systems
        app: {
            name: "google-chrome"
        },
    },
    static: [
        '/home/student/workspace/EarTraining/synthesizerbrowser/build/processedResources/js/main'
    ]
    // TODO Is there a better way to specify the path?
    // static: path.join(__dirname, '../../../../synthesizerbrowser/build/processedResources/js/main')
});
