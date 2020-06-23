/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 Testss
 */
package ntt.siriraj.transform.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

/**
 *
 * @author s.trakulmaiphol
 */
@Configuration
public class ElasticConfig {

    @Value("${elastic.username}")
    private String ELASTIC_USERNAME;

    @Value("${elastic.password}")
    private String ELASTIC_PASSWORD;

    @Value("${elastic.url:localhost}")
    private String ELASTIC_URL;

    @Value("${elastic.port:9200}")
    private int ELASTIC_PORT;

    @Value("${elastic.scheme:http}")
    private String ELASTIC_SCHEME;

    @Value("${elastic.certificatePath}")
    private String ELASTIC_CERTIFICATE_PATH;

    @Value("${elastic.skipHostNameValidate:false}")
    private boolean SKIP_HOSTNAME_VALIDATE;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {

        System.out.println("Starting Configuration");
        final CredentialsProvider credentialsProvider
                = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(ELASTIC_USERNAME, ELASTIC_PASSWORD));
        RestHighLevelClient client = null;
        try {
            Path caCertificatePath = caCertificatePath = Paths.get(ResourceUtils.getFile(ELASTIC_CERTIFICATE_PATH).getPath());
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate trustedCa;
            InputStream is = Files.newInputStream(caCertificatePath);
            trustedCa = factory.generateCertificate(is);
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);
            SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null);
            final SSLContext sslContext = sslContextBuilder.build();
            client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(ELASTIC_URL, ELASTIC_PORT, ELASTIC_SCHEME))
                            .setHttpClientConfigCallback(new HttpClientConfigCallback() {
                                @Override
                                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                                    if (SKIP_HOSTNAME_VALIDATE) {
                                        skipHostNameVerification(httpClientBuilder);
                                    }
                                    return httpClientBuilder.setSSLContext(sslContext);
                                }
                            }
                            ));
            System.out.println("===== Init Elastic Client Success =====");
            System.out.println(client.info(RequestOptions.DEFAULT).getNodeName());
            System.out.println(client.info(RequestOptions.DEFAULT).getClusterUuid());
        } catch (IOException ex) {
            Logger.getLogger(ElasticConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(ElasticConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(ElasticConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ElasticConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ElasticConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return client;
    }

    private HttpAsyncClientBuilder skipHostNameVerification(HttpAsyncClientBuilder httpClientBuilder) {
        httpClientBuilder.setSSLHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        });
        return httpClientBuilder;
    }

}
