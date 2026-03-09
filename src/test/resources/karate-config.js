function fn() {
    var env = karate.env;
    karate.log('karate.env system property was:', env);
    if (!env) {
        env = 'dev';
    }

    var config = {
        baseUrl: 'http://localhost:' + karate.properties['local.server.port']
    };
    
    karate.log('baseUrl ' + config.baseUrl);

    return config;
}