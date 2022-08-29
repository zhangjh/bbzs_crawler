package me.zhangjh.crawler;

import me.zhangjh.crawler.service.FeedDOMapper;
import me.zhangjh.crawler.entity.FeedDO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author zhangjh
 * @date 2022/8/9
 */
@Component
public class FeedCrawler {
    
    @Autowired
    private FeedDOMapper feedDOMapper;
    
    @PostConstruct
    public void init() {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            int num = 3579;
            while (true) {
                System.out.println(num);
                HttpGet httpGet = new HttpGet("https://feeddd.org/feeds?p=" + num);
                CloseableHttpResponse response = client.execute(httpGet);
                if(response.getStatusLine().getStatusCode() == 200) {
                    String body = EntityUtils.toString(response.getEntity());
                    Document document = Jsoup.parse(body);
                    Elements elements = document.select("#__layout > div > main div.w-full");
                    for (Element element : elements) {
                        Document content = Jsoup.parse(element.html());
                        Elements href = content.select(".items-center > a");
                        if(href.size() > 1) {
                            continue;
                        }
                        String name = href.text();
                        String feedLink = href.attr("href");
                        FeedDO feedDO = new FeedDO();
                        feedDO.setName(name);
                        feedDO.setFeed("https://feeddd.org" + feedLink);
                        feedDOMapper.insert(feedDO);
                    }
                } else {
                    break;
                }
                num++;
            }

        } catch (IOException e) {
        }

    }
    
}
