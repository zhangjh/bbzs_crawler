package me.zhangjh.crawler.constant;

import lombok.Getter;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Getter
public enum SubscribeTypeEnum {
    FOREVER(0, "永久订阅"),
    ;

    private Integer type;
    private String desc;

    SubscribeTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
