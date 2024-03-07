package ch.realmtech.launcher.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteReleaseAsset {
    public String name;
    @JsonProperty("browser_download_url")
    public String downloadUrl;
}
