package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.beans.LauncherRelease;
import ch.realmtech.launcher.beans.RemoteReleaseVersion;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocalCache implements GetVersionsReleases, GetLauncherUpdate {
    private final String INFO_FILE = "cache-info.dat";
    private final String LAUNCHER_UPDATE_AVAILABLE = "launcherUpdateAvailable.dat";
    private final String RELEASE_VERSIONS = "releasesVersions.dat";
    private final long TIME_CACHE_NEED_REFRESH = 3600000L; // 1 hour
    private final RealmTechData realmTechData;
    private final LauncherUpdate launcherUpdate;
    private final ReleasesWrk releasesWrk;

    private LauncherRelease launcherReleaseCache;
    private List<RemoteReleaseVersion> remoteReleaseVersionsCache;

    public LocalCache(RealmTechData realmTechData, LauncherUpdate launcherUpdate, ReleasesWrk releasesWrk) throws Exception {
        this.realmTechData = realmTechData;
        this.launcherUpdate = launcherUpdate;
        this.releasesWrk = releasesWrk;

        if (checkToRefreshCache()) {
            refreshCache();
        } else {
            setCache();
        }
    }

    public boolean checkToRefreshCache() throws Exception {
        boolean needToRefresh = false;
        long currentTimeMillis = System.currentTimeMillis();
        File cacheInfoFile = getInfoFile();
        if (!cacheInfoFile.exists()) {
            try (OutputStream outputStream = new FileOutputStream(cacheInfoFile)) {
                ByteBuffer byteBuf = ByteBuffer.allocate(Long.BYTES);
                byteBuf.putLong(currentTimeMillis);
                outputStream.write(byteBuf.array());
                outputStream.flush();
            }
            needToRefresh = true;
        } else {
            try (InputStream inputStream = new FileInputStream(cacheInfoFile)) {
                ByteBuffer byteBuf = ByteBuffer.wrap(inputStream.readAllBytes());
                long cachedTime = byteBuf.getLong();
                if (currentTimeMillis - cachedTime > TIME_CACHE_NEED_REFRESH) {
                    needToRefresh = true;
                }
            }
        }
        return needToRefresh;
    }

    public void refreshCache() throws Exception {
        List<RemoteReleaseVersion> versionsReleases = releasesWrk.getVersionsReleases();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(getVersionReleases()))) {
            objectOutputStream.writeObject(new ArrayList<>(versionsReleases));
            objectOutputStream.flush();
        }
        remoteReleaseVersionsCache = versionsReleases;

        Optional<LauncherRelease> launcherReleaseOpt = launcherUpdate.getLauncherUpdate();
        if (launcherReleaseOpt.isPresent()) {
            LauncherRelease launcherRelease = launcherReleaseOpt.get();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(getLauncherAvailableFile()))) {
                objectOutputStream.writeObject(launcherRelease);
                objectOutputStream.flush();
                launcherReleaseCache = launcherRelease;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void setCache() throws Exception {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(getLauncherAvailableFile()))) {
            launcherReleaseCache = (LauncherRelease) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            launcherReleaseCache = null;
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(getVersionReleases()))) {
            remoteReleaseVersionsCache = (List<RemoteReleaseVersion>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            remoteReleaseVersionsCache = new ArrayList<>();
        }
    }

    private File getInfoFile() {
        return realmTechData.getCacheDirectory().toPath().resolve(INFO_FILE).toFile();
    }

    private File getLauncherAvailableFile() {
        return realmTechData.getCacheDirectory().toPath().resolve(LAUNCHER_UPDATE_AVAILABLE).toFile();
    }

    private File getVersionReleases() {
        return realmTechData.getCacheDirectory().toPath().resolve(RELEASE_VERSIONS).toFile();
    }

    @Override
    public List<RemoteReleaseVersion> getVersionsReleases() {
        return remoteReleaseVersionsCache;
    }

    @Override
    public Optional<LauncherRelease> getLauncherUpdate() {
        return Optional.ofNullable(launcherReleaseCache);
    }
}
