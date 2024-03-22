package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.beans.RemoteReleaseVersion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class RealmTechData {
    private final String rootPath;
    private final static String ROOT_DATA = "RealmTechData";
    private final static String VERSIONS = "versions";
    private final static String CACHE = "launcher/.cache";

    public RealmTechData(RootPathClass rootPathClass) throws IOException {
        this.rootPath = rootPathClass.rootPath;

        createFileStructure();
    }

    private void createFileStructure() throws IOException {
        File rootPathDirectory = getRootPathDirectory();
        if (!rootPathDirectory.exists()) {
            Files.createDirectory(rootPathDirectory.toPath());
        }

        File rootDataDirectory = getRootDataDirectory();
        if (!rootDataDirectory.exists()) {
            Files.createDirectory(rootDataDirectory.toPath());
        }

        File versionDirectory = getVersionDirectory();
        if (!versionDirectory.exists()) {
            Files.createDirectory(versionDirectory.toPath());
        }

        File cacheDirectory = getCacheDirectory();
        if (!cacheDirectory.exists()) {
            Files.createDirectory(cacheDirectory.toPath());
        }
    }

    public File getRootPathDirectory() {
        return Path.of(rootPath).toFile();
    }

    public File getRootDataDirectory() {
        return Path.of(rootPath, ROOT_DATA).toFile();
    }
    public File getCacheDirectory() {
        return Path.of(rootPath, ROOT_DATA, CACHE).toFile();
    }

    public File getVersionDirectory() {
        File rootDataDirectory = getRootDataDirectory();
        return Path.of(rootDataDirectory.toPath().toString(), VERSIONS).toFile();
    }

    public List<String> listVersion() {
        String[] list = getVersionDirectory().list((dir, name) -> isRealmTechVersion(name));
        if (list == null) {
            return List.of();
        } else {
            return Stream.of(list).sorted(Comparator.reverseOrder()).toList();
        }
    }

    public Optional<File> getVersionFile(String selectedVersionName) {
        String[] list = getVersionDirectory().list((dir, name) -> name.equals(selectedVersionName));
        if (list == null) {
            return Optional.empty();
        }
        if (list.length != 1) {
            return Optional.empty();
        }
        String versionName = list[0];
        File versionFile = Path.of(getVersionDirectory().toPath().toString(), versionName).toFile();
        return Optional.of(versionFile);
    }

    public static boolean isRealmTechVersion(String fileName) {
        return fileName.matches("^RealmTech-\\d\\.\\d\\.\\d\\.jar$");
    }

    public boolean isReleasePresentOnLocal(RemoteReleaseVersion releaseVersion) {
        return getVersionFile(releaseVersion.remoteReleaseAsset.name).isPresent();
    }

    public Optional<String> getLastedLocalVersion() {
        return listVersion().stream().max(String::compareTo);
    }

    public static class RootPathClass {
        private final String rootPath;
        private RootPathClass(String rootPath) {
            this.rootPath = rootPath;
        }

        public static RootPathClass defaultRootPath() {
            String os = (System.getProperty("os.name")).toUpperCase();
            String rootPath;
            if (os.contains("WIN")) {
                rootPath = System.getenv("AppData");
            } else {
                // linux
                rootPath = System.getProperty("user.home") + "/.local";
            }
            return new RootPathClass(rootPath);
        }

        public static RootPathClass customRootPath(String customRootPath) {
            return new RootPathClass(customRootPath);
        }
    }
}
