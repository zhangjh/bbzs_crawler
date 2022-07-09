package me.zhangjh.crawler;

import java.util.Map;

/**
 * @author zhangjh
 * @date 2022/7/5
 */
public abstract class AbstractHttpAction {

    /**
     * 抽象doGet方法，由具体工厂实现
     * @param url
     * */
    abstract public void doGet(String url);

    /**
     * 抽象doPost方法，由具体工厂实现
     * @param url
     * @param map
     * */
    abstract public void doPost(String url, Map<String, String> map);
}
