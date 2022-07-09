package me.zhangjh.crawler;

import lombok.extern.slf4j.Slf4j;
import me.zhangjh.crawler.common.Constant;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhangjh
 * @date 2022/7/5
 */
@Component
@Slf4j
@Deprecated
public class LvHttpAction extends AbstractHttpAction {

    private static final CloseableHttpClient CLIENT = HttpPool.getClient();

    @Override
    public void doGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        setGetHeader(httpGet);
        try (CloseableHttpResponse response = CLIENT.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                // biz logic
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(content);
            }
        } catch (Exception e) {
            log.error("doGet exception, e:", e);
        }
    }

    @Override
    public void doPost(String url, Map<String, String> map) {

    }

    public void start() {
        doGet("https://www.louisvuitton.cn/zhs-cn/homepage");
    }

    private void setGetHeader(HttpGet httpGet) {
        httpGet.setHeader(HTTP.USER_AGENT, Constant.USER_AGENT);
        httpGet.setHeader("authority", "www.louisvuitton.cn");
        httpGet.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        httpGet.setHeader("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"102\", \"Google Chrome\";v=\"102\"");
        httpGet.setHeader("sec-ch-ua-mobile", "?0");
        httpGet.setHeader("sec-ch-ua-platform", "macOS");
        httpGet.setHeader("sec-fetch-dest", "document");
        httpGet.setHeader("sec-fetch-mode", "navigate");
        httpGet.setHeader("sec-fetch-site", "none");
        httpGet.setHeader("sec-fetch-user", "?1");
        httpGet.setHeader("upgrade-insecure-requests", "1");

    }
}
