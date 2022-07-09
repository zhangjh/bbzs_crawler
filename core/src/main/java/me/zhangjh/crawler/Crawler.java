package me.zhangjh.crawler;

import com.ruiyun.jvppeteer.core.Puppeteer;
import com.ruiyun.jvppeteer.core.browser.Browser;
import com.ruiyun.jvppeteer.core.browser.BrowserFetcher;
import com.ruiyun.jvppeteer.core.page.Page;
import com.ruiyun.jvppeteer.options.LaunchOptions;
import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder;
import com.ruiyun.jvppeteer.options.ScreenshotOptions;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.crawler.util.PageUtil;
import org.springframework.beans.factory.annotation.Value;
import sun.awt.OSInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static me.zhangjh.crawler.common.Constant.USER_AGENT;

/**
 * @author zhangjh
 * @date 2022/7/6
 */
@Slf4j
public abstract class Crawler {

    @Value("${browser.headless}")
    private boolean headless;

    @Value("${page.wait.timeout}")
    private String scrollWaitTimeout;

    private Browser browser;

    public Page getPage() throws Exception {
        PageUtil.init(scrollWaitTimeout);
        // 第一次自动下载，后续不再下载
        if(!OSInfo.getOSType().equals(OSInfo.OSType.MACOSX)) {
            BrowserFetcher.downloadIfNotExist(null);
        }
        List<String> argList = Arrays.asList("--no-sandbox","--incognito",
                "--user-agent=" + USER_AGENT,
                "--disable-setuid-sandbox","-disable-dev-shm-usage","--disable-blink-features=AutomationControlled",
                "--start-maximized","--user-data-dir=./userData");
        LaunchOptions options = new LaunchOptionsBuilder()
                .withArgs(argList)
//                .withExecutablePath(chromeExecutablePath)
                .withHeadless(headless).build();
        options.setDevtools(false);
        options.setViewport(null);
        options.setIgnoreDefaultArgs(Arrays.asList("--disable-extensions", "--enable-automation"));
        browser = Puppeteer.launch(options);
        Page page = browser.pages().get(0);
        hideHeadless(page);
        return page;
    }

    private void hideHeadless(Page page) {
        page.setUserAgent(USER_AGENT);

        page.evaluateOnNewDocument("() => {\n" +
                "    const newProto = navigator.__proto__;\n" +
                "    delete newProto.webdriver;\n" +
                "    navigator.__proto__ = newProto;");
        page.evaluateOnNewDocument("() => {\n" +
                "    window.chrome = {};\n" +
                "    window.chrome.app = {\n" +
                "        InstallState: 'hehe',\n" +
                "        RunningState: 'haha',\n" +
                "        getDetails: 'xixi',\n" +
                "        getIsInstalled: 'ohno',\n" +
                "    };\n" +
                "    window.chrome.csi = function () {};\n" +
                "    window.chrome.loadTimes = function () {};\n" +
                "    window.chrome.runtime = function () {};\n");
        page.evaluateOnNewDocument("() => {\n" +
                "    Object.defineProperty(navigator, 'plugins', {\n" +
                "        get: () => [\n" +
                "        {\n" +
                "            0: {\n" +
                "            type: 'application/x-google-chrome-pdf',\n" +
                "            suffixes: 'pdf',\n" +
                "            description: 'Portable Document Format',\n" +
                "            enabledPlugin: Plugin,\n" +
                "            },\n" +
                "            description: 'Portable Document Format',\n" +
                "            filename: 'internal-pdf-viewer',\n" +
                "            length: 1,\n" +
                "            name: 'Chrome PDF Plugin',\n" +
                "        },\n" +
                "        {\n" +
                "            0: {\n" +
                "            type: 'application/pdf',\n" +
                "            suffixes: 'pdf',\n" +
                "            description: '',\n" +
                "            enabledPlugin: Plugin,\n" +
                "            },\n" +
                "            description: '',\n" +
                "            filename: 'mhjfbmdgcfjbbpaeojofohoefgiehjai',\n" +
                "            length: 1,\n" +
                "            name: 'Chrome PDF Viewer',\n" +
                "        },\n" +
                "        {\n" +
                "            0: {\n" +
                "            type: 'application/x-nacl',\n" +
                "            suffixes: '',\n" +
                "            description: 'Native Client Executable',\n" +
                "            enabledPlugin: Plugin,\n" +
                "            },\n" +
                "            1: {\n" +
                "            type: 'application/x-pnacl',\n" +
                "            suffixes: '',\n" +
                "            description: 'Portable Native Client Executable',\n" +
                "            enabledPlugin: Plugin,\n" +
                "            },\n" +
                "            description: '',\n" +
                "            filename: 'internal-nacl-plugin',\n" +
                "            length: 2,\n" +
                "            name: 'Native Client',\n" +
                "        },\n" +
                "        ],\n" +
                "    });\n");
        page.evaluateOnNewDocument("() => {\n" +
                "    Object.defineProperty(navigator, 'languages', {\n" +
                "        get: () => ['zh-CN', 'zh', 'en'],\n" +
                "    });");
        page.evaluateOnNewDocument("() => {\n" +
                "    const originalQuery = window.navigator.permissions.query; //notification伪装\n" +
                "    window.navigator.permissions.query = (parameters) =>\n" +
                "        parameters.name === 'notifications'\n" +
                "        ? Promise.resolve({ state: Notification.permission })\n" +
                "        : originalQuery(parameters);\n" +
                "}\n");
        page.evaluateOnNewDocument("() => {\n" +
                "    const getParameter = WebGLRenderingContext.getParameter;\n" +
                "    WebGLRenderingContext.prototype.getParameter = function (parameter) {\n" +
                "        // UNMASKED_VENDOR_WEBGL\n" +
                "        if (parameter === 37445) {\n" +
                "            return 'Intel Inc.';\n" +
                "        }\n" +
                "        // UNMASKED_RENDERER_WEBGL\n" +
                "        if (parameter === 37446) {\n" +
                "            return 'Intel(R) Iris(TM) Graphics 6100';\n" +
                "        }\n" +
                "        return getParameter(parameter);\n" +
                "    };\n");
    }

    /** 总体流程编排 */
    public void run(String startUrl) {
        Page page = null;
        try {
            page = getPage();
            page.goTo(startUrl);
            action(page);
        } catch (Exception e) {
            log.error("me.zhangjh.crawler.Crawler.run exception: ", e);
            if(page != null) {
                ScreenshotOptions options = new ScreenshotOptions();
                options.setType("png");
                options.setPath("./crash/" + System.currentTimeMillis() + ".png");
                try {
                    page.screenshot(options);
                } catch (IOException ignored) {
                }
            }
        } finally {
            if(browser != null) {
                browser.close();
            }
        }
    }

    /** 具体工作流程 */
    public abstract void action(Page page) throws ExecutionException, InterruptedException;

    /** 针对一页的具体处理 */
    public abstract void crawlerOnePage(Page page, String url, String classname) throws InterruptedException, ExecutionException;
}
