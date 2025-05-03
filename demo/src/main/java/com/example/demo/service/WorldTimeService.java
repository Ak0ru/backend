package com.example.demo.service;

import com.example.demo.dto.WorldTimeResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.net.URI;
import java.net.http.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Service
public class WorldTimeService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorldTimeService() {
        this.httpClient = createInsecureHttpClient();
    }

    private HttpClient createInsecureHttpClient() {
        try {
            // 1) Trust all certificates
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new SecureRandom());

            // 2) Build JDK HttpClient with this SSLContext and skip hostname check
            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .sslParameters(new SSLParameters() {{
                        setEndpointIdentificationAlgorithm(null);
                    }})
                    .build();
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось сконфигурировать HttpClient: " + ex.getMessage(), ex);
        }
    }

    public WorldTimeResponseDto getTime(String timezone) {
        String url = "https://worldtimeapi.org/api/timezone/" + timezone;
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new RuntimeException("Ошибка от WorldTimeAPI: HTTP " + resp.statusCode());
            }
            return objectMapper.readValue(resp.body(), WorldTimeResponseDto.class);
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось получить время от WorldTimeAPI: " + ex.getMessage(), ex);
        }
    }
}
