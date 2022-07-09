package me.zhangjh.crawler.util;

import com.ruiyun.jvppeteer.core.page.Page;
import com.ruiyun.jvppeteer.options.WaitForSelectorOptions;

import java.util.concurrent.ExecutionException;

/**
 * @author zhangjh
 * @date 2022/7/9
 */
public class PageUtil {

    private static String pageWaitTimeout;

    public static void init(String pageWaitTimeout) {
        PageUtil.pageWaitTimeout = pageWaitTimeout;
    }

    // 封装click方法，先等待待点元素出现，然后再点击，点击后等待一段时间
    public static void click(Page page, String cssSelector) throws InterruptedException, ExecutionException {
        WaitForSelectorOptions options = new WaitForSelectorOptions();
        options.setTimeout(Integer.parseInt(pageWaitTimeout));
        page.waitForSelector(cssSelector, options);
        page.$(cssSelector).click();
        page.waitFor(pageWaitTimeout);
    }
}
