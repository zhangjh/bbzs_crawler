package me.zhangjh.crawler.dto;

import lombok.Data;

/**
 * @author zhangjh
 * @date 2022/7/12
 */
@Data
public class WxMsgField {

    private String value;

    public WxMsgField(String value) {
        this.value = value;
    }
}
