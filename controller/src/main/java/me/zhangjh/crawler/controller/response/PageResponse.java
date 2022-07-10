package me.zhangjh.crawler.controller.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Data
public class PageResponse<T> extends Response<T> implements Serializable {
    private static final long serialVersionUID = 3260218845383086298L;

    private boolean success;
    private String errorMsg;
    private List<T> dataList;
    private int total;

}
