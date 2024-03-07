package ch.realmtech.launcher.beans;


public class RemoteReleaseVersion {
    public RemoteReleaseAsset remoteReleaseAsset;
    public String message;

    public String publishedAt;

    public RemoteReleaseVersion(RemoteReleaseAsset remoteReleaseAsset, String message, String publishedAt) {
        this.remoteReleaseAsset = remoteReleaseAsset;
        this.message = message;
        this.publishedAt = publishedAt;

    }

    @Override
    public String toString() {
        return remoteReleaseAsset.name;
    }
}
