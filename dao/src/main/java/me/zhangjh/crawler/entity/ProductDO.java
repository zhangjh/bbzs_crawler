package me.zhangjh.crawler.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangjh
 */
@Data
public class ProductDO {
    private Long id;

    private Date createTime;

    private Date updateTime;

    private Boolean isDeleted;

    private String brand;

    private String className;

    private String series;

    private String itemName;

    private String price;

    private String itemPic;

    private String itemUrl;

    private String itemCode;

    private String itemSize;

    private Integer notified;

    private Integer hasStock;

    private String feature;
}