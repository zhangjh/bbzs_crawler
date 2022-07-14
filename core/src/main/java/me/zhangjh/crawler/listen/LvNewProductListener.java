package me.zhangjh.crawler.listen;

import com.alibaba.fastjson.JSONObject;
import com.ruiyun.jvppeteer.core.page.Page;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.crawler.LvNewSeriesProductsCrawler;
import me.zhangjh.crawler.entity.ProductDO;
import me.zhangjh.crawler.service.ProductMapper;
import me.zhangjh.crawler.service.SubscribeNotify;
import org.apache.http.util.Asserts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static me.zhangjh.crawler.common.Constant.LV_URL_PRE;
import static me.zhangjh.crawler.common.Constant.LvSelector.*;
import static me.zhangjh.crawler.util.PageUtil.click;


/**
 * @author zhangjh
 * @date 2022/7/6
 * 新品监控
 */
@Component
@Slf4j(topic = "report")
public class LvNewProductListener extends LvNewSeriesProductsCrawler {

    @Value("${page.wait.timeout}")
    private String scrollWaitTimeout;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private SubscribeNotify subscribeNotify;

    @Override
    public Object bizHandle(List<ProductDO> productDOS) {
        if(productDOS.isEmpty()) {
            return false;
        }
        productMapper.insertBatch(productDOS);
        return this.notify(productDOS);
    }

    @Override
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
            ProductDO record = productMapper.selectByCode(productDO.getItemCode());
            if(record == null) {
                // 该品为新品
                records.add(productDO);
            } else {
                log.info("category: {}暂无新品", classname + "_" + series);
                break;
            }
        }
        return records;
    }

    @Override
    public void crawlerOnePage(Page page, String url, String classname) throws InterruptedException,
            ExecutionException {
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

        Document document = Jsoup.parse(page.content());
        List<ProductDO> newProducts;
        ProductDO lastNewProduct;
        while (true) {
            // 1. 没有新品直接退出
            newProducts = this.parseProduct(page, document, classname);
            int size = newProducts.size();
            if(size == 0) {
                break;
            }
            // 记录当前最后一个新品
            lastNewProduct = newProducts.get(size - 1);
            page.evaluate("() => {window.scrollBy(0, window.screen.height);}");
            page.waitFor(scrollWaitTimeout);
            document = Jsoup.parse(page.content());
            newProducts = this.parseProduct(page, document, classname);
            // 2. 翻页后如果新品还是之前最后一个新品，则表明已经没有新品了，直接退出
            size = newProducts.size();
            if(newProducts.get(size - 1).getItemCode().equals(lastNewProduct.getItemCode())) {
                break;
            }
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
        this.bizHandle(newProducts);
    }

    private Object notify(List<ProductDO> newProducts) {
        log.info("新品通知: {}", JSONObject.toJSONString(newProducts));
        subscribeNotify.send(newProducts);
        return null;
    }

    @XxlJob("lvMonitorJobHandler")
    public void lvMonitorJobHandler() {
        log.info("me.zhangjh.crawler.listen.LvNewProductListener.lvMonitorJobHandler start");
        this.start();
        log.info("me.zhangjh.crawler.listen.LvNewProductListener.lvMonitorJobHandler finish");
    }
}
