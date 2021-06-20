package com.chason.base.polling;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * 长轮询客户端
 *
 * @author chason
 */
@Data
@Slf4j
public class PollingClient {

    private HttpClient httpClient;

    // 连接超时时间
    private static final int CONNECTION_TIME_OUT = 4000;

    public PollingClient() {
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(CONNECTION_TIME_OUT))
                .build();
    }

    /**
     * 发起长轮询
     */
    public void takeLongPolling(String serviceUrl, String dataId) {
        HttpRequest longPollingReq = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl + "?dataId=" + dataId))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(longPollingReq, HttpResponse.BodyHandlers.ofString());
            switch (response.statusCode()) {
                // 200 代表服务器配置已经更新
                case HttpServletResponse.SC_OK:
                    String bodyStr = response.body();
                    log.info("Server data has changed dataId:[{}], received conf info:[{}]", dataId, bodyStr);
                    takeLongPolling(serviceUrl, dataId);
                    break;
                // 302 代表配置未更新
                case HttpServletResponse.SC_NOT_MODIFIED:
                    log.info("Server data not changed dataId:[{}]", dataId);
                    takeLongPolling(serviceUrl, dataId);
                    break;
                default:
                    throw new RuntimeException("unExpected HTTP status code");

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("take long polling failure!");
        }
    }

    public static void main(String[] args) {
        Logger logger = (Logger) LoggerFactory.getLogger("org.apache.http");
        final String SERVER_BASE_URL = "http://127.0.0.1:8080/listener";
        PollingClient pollingClient = new PollingClient();
        pollingClient.takeLongPolling(SERVER_BASE_URL, "data1");
    }
}
