package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.beans.RemoteRelease;
import ch.realmtech.launcher.beans.RemoteReleaseVersion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ReleasesWrk {
    private final static String RELEASE_URI = "https://api.github.com/repos/FabienChatton/RealmTech/releases";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    public ReleasesWrk() {
        this.httpClient = HttpClient.newBuilder().build();
        this.mapper = new ObjectMapper();
    }

    public List<RemoteReleaseVersion> getVersionsReleases() throws Error {
        try {
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
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new Error(e);
        }
    }
}
