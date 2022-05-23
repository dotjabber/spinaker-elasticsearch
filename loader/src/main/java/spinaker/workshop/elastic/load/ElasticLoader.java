package spinaker.workshop.elastic.load;

import java.io.Closeable;
import java.io.IOException;

import java.util.logging.Logger;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import nl.altindag.ssl.SSLFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import spinaker.workshop.elastic.common.ElasticDocument;

public class ElasticLoader implements Closeable {
    private static final Logger LOG = Logger.getGlobal();

    private final ElasticsearchClient client;
    private final ElasticsearchTransport transport;
    private final String indexName;
    private int loadCount;

    public ElasticLoader(String address, int port, String indexName, String userName, String password) {

        SSLFactory sslFactory = SSLFactory.builder()
                .withUnsafeTrustMaterial()
                .withUnsafeHostnameVerifier()
                .build();

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password));

        RestClientBuilder builder = RestClient.builder(new HttpHost(address, port, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    httpClientBuilder.setSSLContext(sslFactory.getSslContext());
                    httpClientBuilder.setSSLHostnameVerifier(sslFactory.getHostnameVerifier());
                    return httpClientBuilder;
                });

        RestClient restClient = builder.build();
        transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        client = new ElasticsearchClient(transport);
        this.indexName = indexName;
    }

    public void store(ElasticDocument document, String pipeline) throws IOException {
        if("null".equals(pipeline)) {
            client.index(IndexRequest.of(e -> e.index(indexName).id(String.valueOf(++loadCount)).document(document)));

        } else {
            client.index(IndexRequest.of(e -> e.index(indexName).pipeline(pipeline).id(String.valueOf(++loadCount)).document(document)));
        }

        if (++loadCount % 1000 == 0) {
            LOG.info(String.format("Loaded documents: %d", loadCount));
        }
    }

    @Override
    public void close() throws IOException {
        transport.close();
    }
}
