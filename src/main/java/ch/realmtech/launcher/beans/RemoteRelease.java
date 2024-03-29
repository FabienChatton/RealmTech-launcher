package ch.realmtech.launcher.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteRelease implements Serializable {
    @JsonProperty("body")
    public String message;

    @JsonProperty("published_at")
    public Date publishedAt;

    public List<RemoteReleaseAsset> assets;
}
