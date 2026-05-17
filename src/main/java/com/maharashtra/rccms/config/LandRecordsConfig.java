package com.maharashtra.rccms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class LandRecordsConfig {

    @Bean
    public RestTemplate restTemplate(
            @Value("${rccms.land-records.allow-insecure-ssl:false}") boolean allowInsecureSsl
    ) {
        if (allowInsecureSsl) {
            enableInsecureSslForDev();
        }
        return new RestTemplate();
    }

    /**
     * Development fallback for systems that fail PKIX validation against upstream.
     * Keep disabled in production.
     */
    private static void enableInsecureSslForDev() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            // no-op
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            // no-op
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to enable insecure SSL fallback for land records.", ex);
        }
    }
}

