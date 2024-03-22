package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.RealmTechLauncher;
import ch.realmtech.launcher.beans.LauncherRelease;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class LauncherUpdate {
    private final static String LAUNCHER_RELEASE_URI = "https://api.github.com/repos/FabienChatton/RealmTech-launcher/releases/latest";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    public LauncherUpdate() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        this.mapper = new ObjectMapper();
    }

    public Optional<LauncherRelease> hasLauncherUpdateAvailable() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(LAUNCHER_RELEASE_URI))
                .GET()
                .build();

        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

        InputStream responseStream = response.headers().firstValue("Content-Encoding").orElse("").equals("gzip")
                ? new GZIPInputStream(response.body())
                : response.body();

        LauncherRelease launcherRelease = mapper.readValue(responseStream, new TypeReference<>() {});

        if (launcherRelease.version.compareTo(RealmTechLauncher.LAUNCHER_VERSION) > 0) {
            return Optional.of(launcherRelease);
        } else {
            return Optional.empty();
        }
    }

    public void update() throws Exception {
        String os = (System.getProperty("os.name")).toUpperCase();
        Process updateInstaller;
        if (os.contains("WIN")) {
            updateInstaller = Runtime.getRuntime().exec("cmd /c start cmd.exe /c powershell Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://github.com/FabienChatton/RealmTech-launcher/releases/download/installateur/realmtech-install.ps1'))");
        } else {
            updateInstaller = Runtime.getRuntime().exec("curl -s \"https://raw.githubusercontent.com/FabienChatton/RealmTech-launcher/master/realmtech-install.sh\" | bash");
        }
        int i = updateInstaller.waitFor();
        if (i != 0) {
            throw new RuntimeException("L'installeur a échouer la mise à jour. Code de sortie: " + i);
        }
        System.exit(0);
    }
}
