package ch.realmtech.launcher.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RateLimite {

    public Resources resources;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resources {
        public Resource core;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resource {
        public int limite;
        public int remaining;
        public int reset;
        public int used;
    }
}
