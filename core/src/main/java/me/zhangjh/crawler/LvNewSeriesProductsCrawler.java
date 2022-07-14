package me.zhangjh.crawler;

import com.ruiyun.jvppeteer.core.page.Page;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.crawler.entity.ProductDO;
import me.zhangjh.crawler.service.ProductMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static me.zhangjh.crawler.common.Constant.LV_START_URL;
import static me.zhangjh.crawler.common.Constant.LV_URL_PRE;
import static me.zhangjh.crawler.common.Constant.LvSelector.*;
import static me.zhangjh.crawler.util.PageUtil.click;

/**
 * @author zhangjh
 * @date 2022/7/5
 * 用来自动爬取lv新品系列的爬虫
 */
@Component
@Slf4j(topic = "report")
public class LvNewSeriesProductsCrawler extends Crawler {

    @Value("${page.wait.timeout}")
    private String scrollWaitTimeout;

    @Value("${skip.initial.crawler:true}")
    private boolean skipInitialCrawler;

    @Autowired
    private ProductMapper productMapper;

    @PostConstruct
    public void init() {
        if(!skipInitialCrawler) {
            this.start();
        }
    }

    public void start() {
        this.run(LV_URL_PRE + LV_START_URL);
    }

    @SuppressWarnings("uncheck")
    @Override
    public void action(Page page) throws ExecutionException, InterruptedException {
        Document document = null;
        Map<String, Set<String>> classUrlMap = new HashMap<>();
        // 1. 点击同意
        click(page, AGREE_BTN);
        // 2. 点击新品系列
        click(page, NEW_PRODUCT_SERIES_BTN);
        // 3. 获取所有新品分类的url(新品下有男士、女士两个分类)
        List<String> classSelectors = Arrays.asList(NEW_SERIES_FORMAN, NEW_SERIES_FORWOMAN);
        for (String classSelector : classSelectors) {
            Set<String> urls = new HashSet<>();
            click(page, classSelector);
            document = Jsoup.parse(page.content());
            String classname = document.select(classSelector).select("span").text();
            Asserts.notEmpty(classname, "classname");
            Elements elements = document.select(NEW_PRODUCT_SERIES)
                    .select(NEW_SERIES_URLS);
            for (Element element : elements) {
                urls.add(element.attr("href"));
            }
            classUrlMap.put(classname, urls);
        }
        Asserts.notNull(document, "document");
        for (Map.Entry<String, Set<String>> entry : classUrlMap.entrySet()) {
            String classname = entry.getKey();
            Set<String> urls = entry.getValue();
            for (String url : urls) {
                crawlerOnePage(page, url, classname);
            }
        }
    }

    @Override
    public void crawlerOnePage(Page page, String url, String classname) throws InterruptedException, ExecutionException {
        log.info("start craw page url: {}", url);
        if(!url.startsWith("http")) {
            url = LV_URL_PRE + url;
        }
        page.goTo(url);

        page.waitFor(scrollWaitTimeout);
        if(page.$(PRODUCT_CARD) == null) {
            log.info("url: {}不包含所需要的产品", url);
            return;
        }

        Document document;
        while (true) {
            page.evaluate("() => {window.scrollBy(0, window.screen.height);}");
            page.waitFor(scrollWaitTimeout);
            document = Jsoup.parse(page.content());
            Elements hasMoreBtn = document.select(HAS_MORE_BTN);
            Elements hasMore = document.select(HAS_MORE);
            // 到达页面底部停止(针对lv是页面上既没有更多也没有加载更多)
            if(hasMore.size() == 0 && hasMoreBtn.size() == 0) {
                break;
            }
            if(hasMoreBtn.size() != 0) {
                click(page, HAS_MORE_BTN);
            }
        }
        this.bizHandle(this.parseProduct(page, document, classname));
    }

    /**
     * 解析一页产品
     * */
    public List<ProductDO> parseProduct(Page page, Document document, String classname) {
        log.info("start parseProduct, className: {}", classname);
        Elements elements = document.select(PRODUCT_CARD);
        String series = document.select(PRODUCT_SERIES).text();
        Asserts.notEmpty(series, "series");
        log.info("category parsed total: {}:{}", classname + "_" +  series, elements.size());

        List<ProductDO> records = new ArrayList<>();
        for (Element element : elements) {
            ProductDO productDO = handleElement(element);
            productDO.setSeries(series);
            productDO.setClassName(classname);
            records.add(productDO);
        }
        return records;
    }

    public ProductDO handleElement(Element element) {
        Elements h2 = element.select("h2");
        String id = h2.attr("id");
        Elements link = h2.select("a");
        String href = link.attr("href");
        String name = link.text();
        String price = element.select(PRODUCT_PRICE).text();
        String img = "";
        assert element.parent() != null;
        assert element.parent().parent() != null;
        String srcset = element.parent().parent().select("img").attr("data-srcset");
        if(StringUtils.isNotBlank("srcset")) {
            img = srcset.split(" ")[0];
        }
        Asserts.notEmpty(id, "id");
        Asserts.notEmpty(name, "name");
//        Asserts.notEmpty(price, name + ": price");
        price = price.isEmpty() ? "-" : price;
        Asserts.notEmpty(href, name + ": href");
        ProductDO record = new ProductDO();
        record.setBrand("LV");
        record.setItemCode(id);
        record.setItemName(name);
        record.setPrice(price);
        record.setItemUrl(href);
        record.setItemPic(img);
        return record;
    }

    /**
     * 接收解析过的产品进行业务处理
     * */
    public Object bizHandle(List<ProductDO> productDOS) {
        log.info("start handle bizLogic");
        if(productDOS.isEmpty()) {
            return null;
        }
        return productMapper.insertBatch(productDOS);
    }
}
