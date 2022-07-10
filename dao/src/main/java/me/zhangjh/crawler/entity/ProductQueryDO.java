package me.zhangjh.crawler.entity;

import lombok.Data;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Data
public class ProductQueryDO extends ProductDO {

    private String orderBy;

    // 从1开始
    private Integer pageIndex;

    // (pageIndex - 1) * pageSize
    private Integer offset;

    private Integer pageSize = 20;
}
