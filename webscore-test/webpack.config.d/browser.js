var path = require('path');

config.devServer = ({
    // The name of the browser is different on different operating systems
    open: "google-chrome",
    // TODO Is there a better way to specify the path?
    contentBase: path.join(__dirname, '../../../../webscore-test/build/processedResources/js/main')
});
