package me.zhangjh.crawler.util;

import com.ruiyun.jvppeteer.core.page.Page;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutionException;

/**
 * @author zhangjh
 * @date 2022/7/9
 */
public class PageUtil {

    private static String pageWaitTimeout;

    private static String urlPre;

    public static void init(String pageWaitTimeout, String urlPre) {
        PageUtil.pageWaitTimeout = pageWaitTimeout;
        PageUtil.urlPre = urlPre;
    }

    // 封装click方法，先等待待点元素出现，然后再点击，点击后等待一段时间
    public static void click(Page page, String cssSelector) throws InterruptedException, ExecutionException {
//        WaitForSelectorOptions options = new WaitForSelectorOptions();
//        options.setTimeout(Integer.parseInt(pageWaitTimeout));
        page.waitForSelector(cssSelector);
        page.$(cssSelector).click();
        page.waitFor(pageWaitTimeout);
    }

    @SneakyThrows
    public static Document open(Page page, String url, Boolean needPageParse) {
        if(!url.startsWith("http")) {
            url = urlPre + url;
        }
        page.goTo(url);
//        page.waitFor(pageWaitTimeout);
        Document document = null;
        if(needPageParse) {
            document = Jsoup.parse(page.content());
        }
        return document;
    }
}
