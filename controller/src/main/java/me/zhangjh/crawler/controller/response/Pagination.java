package me.zhangjh.crawler.controller.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Data
public class Pagination implements Serializable {
    private static final long serialVersionUID = 5196755756560720600L;

    private Integer pageIndex;
    private Integer pageSize = 20;
}
