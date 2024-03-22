package ch.realmtech.launcher.wrk;

import ch.realmtech.launcher.beans.RateLimite;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.zip.GZIPInputStream;

public class GithubRateLimit {
    private final static String RATE_LIMITE_URI = "https://api.github.com/rate_limit";

    private static int remaining;

    static {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        ObjectMapper mapper = new ObjectMapper();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(RATE_LIMITE_URI))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

            InputStream responseStream = response.headers().firstValue("Content-Encoding").orElse("").equals("gzip")
                    ? new GZIPInputStream(response.body())
                    : response.body();

            RateLimite rateLimite = mapper.readValue(responseStream, new TypeReference<>() {});

            remaining = rateLimite.resources.core.remaining;
        } catch (Exception e) {
            remaining = 0;
        }
    }

    public static boolean hasRemaining() {
        return remaining > 0;
    }

    public static void consumeRequest() {
        remaining--;
    }
}
