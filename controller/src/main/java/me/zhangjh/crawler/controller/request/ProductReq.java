package me.zhangjh.crawler.controller.request;

import lombok.Data;
import me.zhangjh.crawler.controller.response.Pagination;

import java.io.Serializable;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Data
public class ProductReq extends Pagination implements Serializable {
    private static final long serialVersionUID = -5343978420738233816L;

    private String className;

    private String series;
}
