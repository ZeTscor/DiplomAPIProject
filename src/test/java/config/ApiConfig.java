package config;

import org.aeonbits.owner.Config;

    @Config.Sources({
            "classpath:api.properties"
    })
    public interface ApiConfig extends Config {
        @Key("baseUrlAuth")
        @DefaultValue("https://accounts.spotify.com/api")
        String baseUrlAuth();
        @Key("clientId")
        String clientId();
        @Key("clientSecret")
        String clientSecret();
        @Key("baseUrl")
        String baseUrl();
        @Key("basePath")
        String basePath();
    }

