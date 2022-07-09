package me.zhangjh.crawler;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author zhangjh
 * @date 2022/7/4
 */
public class HttpPool {
    private static final PoolingHttpClientConnectionManager manager;

    private static final RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .build();

    static {
        manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(200);
        manager.setDefaultMaxPerRoute(20);
    }

    private static final class ClientHolder {
        static final CloseableHttpClient CLIENT = HttpClients.custom()
                .setDefaultRequestConfig(DEFAULT_REQUEST_CONFIG)
                .setConnectionManager(manager)
                .build();
    }

    public static CloseableHttpClient getClient() {
        return ClientHolder.CLIENT;
    }
}
