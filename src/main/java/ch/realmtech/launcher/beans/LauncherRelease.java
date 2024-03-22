package ch.realmtech.launcher.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LauncherRelease {
    @JsonProperty("tag_name")
    public String version;
    @JsonProperty("published_at")
    public Date publishedAt;
}
