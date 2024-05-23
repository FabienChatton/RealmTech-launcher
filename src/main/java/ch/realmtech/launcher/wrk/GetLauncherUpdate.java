package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.beans.LauncherRelease;

import java.util.Optional;

public interface GetLauncherUpdate {
    Optional<LauncherRelease> getLauncherUpdate() throws Exception;
    void forceRefreshCache() throws Exception;
}
