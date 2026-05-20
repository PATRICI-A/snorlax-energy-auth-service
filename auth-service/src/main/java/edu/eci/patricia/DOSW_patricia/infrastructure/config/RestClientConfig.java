package edu.eci.patricia.DOSW_patricia.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.cert.X509Certificate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) {}
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAll, null);

        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }
}
