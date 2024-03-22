package ch.realmtech.launcher.beans;


import java.io.Serializable;
import java.util.Date;

public class RemoteReleaseVersion implements Serializable {
    public RemoteReleaseAsset remoteReleaseAsset;
    public String message;
    public Date publishedAt;
    public boolean isInstalled;

    public RemoteReleaseVersion(RemoteReleaseAsset remoteReleaseAsset, String message, Date publishedAt) {
        this.remoteReleaseAsset = remoteReleaseAsset;
        this.message = message;
        this.publishedAt = publishedAt;

    }

    @Override
    public String toString() {
        return isInstalled
                ? String.format("%s (installed)", remoteReleaseAsset.name)
                : remoteReleaseAsset.name;
    }
}
