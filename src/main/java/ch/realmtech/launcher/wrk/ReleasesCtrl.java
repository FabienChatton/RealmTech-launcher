package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.beans.RemoteReleaseVersion;

import java.util.function.IntConsumer;

public interface ReleasesCtrl {
    void downloadVersionRelease(String downloadUrl, String downloadVersionName, Runnable onSuccess, IntConsumer onFail) throws Exception;

    void deleteVersionRelease(RemoteReleaseVersion releaseVersion);
}
