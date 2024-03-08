package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.beans.RemoteRelease;
import ch.realmtech.launcher.beans.RemoteReleaseVersion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntConsumer;
import java.util.zip.GZIPInputStream;

public class ReleasesWrk {
    private final static String RELEASE_URI = "https://api.github.com/repos/FabienChatton/RealmTech/releases";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private RealmTechData realmTechData;

    public ReleasesWrk() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        this.mapper = new ObjectMapper();
    }

    public List<RemoteReleaseVersion> getVersionsReleases() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(RELEASE_URI))
                .GET()
                .build();

        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

        InputStream responseStream = response.headers().firstValue("Content-Encoding").orElse("").equals("gzip")
                ? new GZIPInputStream(response.body())
                : response.body();
        List<RemoteRelease> releases = mapper.readValue(responseStream, new TypeReference<>() {});
        return releases.stream()
                .map((remoteRelease) -> remoteRelease.assets.stream().map((asset) -> new RemoteReleaseVersion(asset, remoteRelease.message, remoteRelease.publishedAt)).toList())
                .flatMap((assets) -> assets.stream().filter((asset) -> asset.remoteReleaseAsset.name.endsWith(".jar")).findFirst().stream())
                .toList();
    }

    public void downloadVersionRelease(String downloadUrl, String downloadVersionName, Runnable onSuccess, IntConsumer onFail) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(downloadUrl))
                .GET()
                .build();

        File donwloadFile = Files.createFile(Path.of(realmTechData.getVersionDirectory().toPath().toString(), downloadVersionName)).toFile();
        CompletableFuture<HttpResponse<Path>> futureResponse;
        try {
            futureResponse = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofFile(donwloadFile.toPath()));
        } catch (Exception e) {
            donwloadFile.delete();
            throw e;
        }

        futureResponse.thenAccept((response) -> {
            if (response.statusCode() != 200) {
                donwloadFile.delete();
                onFail.accept(response.statusCode());
            } else {
                onSuccess.run();
            }
        });
    }

    public Optional<RemoteReleaseVersion> getLastedRemoteVersion() {
        try {
            return getVersionsReleases().stream()
                    .min((o1, o2) -> o2.publishedAt.compareTo(o1.publishedAt));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> hasNewRemoteVersion() {
        Optional<String> lastedLocalVersion = realmTechData.getLastedLocalVersion();
        if (lastedLocalVersion.isEmpty()) {
            return Optional.of("Il n'y a pas de version installer. Aller dans l'onglet \"Version\" pour en installer une.");
        }

        Optional<RemoteReleaseVersion> lastedRemoteVersion = getLastedRemoteVersion();
        if (lastedRemoteVersion.isEmpty()) {
            return Optional.empty();
        }

        RemoteReleaseVersion remoteReleaseVersion = lastedRemoteVersion.get();
        int compare = remoteReleaseVersion.remoteReleaseAsset.name.compareTo(lastedLocalVersion.get());
        if (compare > 0) {
            return Optional.of("Il y a une nouvelle version disponible. Dernière version: " + remoteReleaseVersion.remoteReleaseAsset.name);
        } else {
            return Optional.empty();
        }
    }

    public void deleteVersionRelease(RemoteReleaseVersion releaseVersion) {
        realmTechData.getVersionFile(releaseVersion.remoteReleaseAsset.name).ifPresent(File::delete);
    }

    public void setRealmTechData(RealmTechData realmTechData) {
        this.realmTechData = realmTechData;
    }
}
