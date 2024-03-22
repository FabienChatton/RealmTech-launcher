package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.beans.RemoteReleaseVersion;

import java.util.List;

public interface GetVersionsReleases {
    List<RemoteReleaseVersion> getVersionsReleases() throws Exception;
}
