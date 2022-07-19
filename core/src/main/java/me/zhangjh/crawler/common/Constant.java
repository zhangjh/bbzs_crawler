package me.zhangjh.crawler.common;

/**
 * @author zhangjh
 * @date 2022/7/5
 */
public class Constant {

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 " +
            "(KHTML," +
            " like Gecko) Chrome/102.0.0.0 Safari/537.36";


    public static final String LV_URL_PRE = "https://www.louisvuitton.cn";

    public static final String LV_START_URL = "/zhs-cn/homepage";

    public static final class LvSelector {
        // 同意按钮
        public static final String AGREE_BTN = "#ucm-details > div.ucm-popin.ucm-popin--pushpop.ucm-popin--active > div > form > ul > li:nth-child(3) > button";
        // 新品系列
        public static final String NEW_PRODUCT_SERIES = "#header nav.lv-header__main-nav > ul > li:nth-child(1)";
        // 新品系列点击按钮
        public static final String NEW_PRODUCT_SERIES_BTN = NEW_PRODUCT_SERIES + " > button";
        // 新品男士分类
        public static final String NEW_SERIES_FORMAN = "#nvcat1830006v-button";
        // 新品女士分类
        public static final String NEW_SERIES_FORWOMAN = "#nvcat1830005v-button";
        // 分类tab标签
        public static final String PRODUCT_TABS = ".lv-header__main .lv-header-main-nav > li";
        // 第一级分类
        public static final String FIRST_CLASS = ".lv-header-main-nav-panel__container > ul > li";
        // 第二级分类
        public static final String SECOND_CLASS = ".lv-header-main-nav-child__category > ul > li";
        // 新品分类下系列链接
        public static final String NEW_SERIES_URLS = ".lv-header-main-nav-child__item.-active div .lv-list li > a";
        // 产品卡片
        public static final String PRODUCT_CARD = ".lv-product-card__info";
        // 加载更多的按钮
        public static final String HAS_MORE_BTN = "#__layout div.lv-paginated-list.lv-category__grid > div.lv-paginated-list__button-wrap > button";
        // 更多
        public static final String HAS_MORE = ".lv-infinite-scroll__label";
        // 产品系列标题
        public static final String PRODUCT_SERIES = ".lv-category__name.-new";
        // 产品价格
        public static final String PRODUCT_PRICE = ".lv-product-purchase__price .notranslate";
        // 产品主图
        public static final String PRODUCT_IMAGE = "picture > img";
        // 购买按钮
        public static final String PURCHASE_BTN = ".lv-product-purchase.lv-product__purchase > button";
    }
}
