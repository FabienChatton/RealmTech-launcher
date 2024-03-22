package ch.realmtech.launcher.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteReleaseAsset implements Serializable {
    public String name;
    @JsonProperty("browser_download_url")
    public String downloadUrl;
}
