package me.zhangjh.crawler.constant;

import lombok.Getter;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Getter
public enum TypeEnum {
    FOREVER(0, "永久订阅"),
    ;

    private Integer type;
    private String desc;

    TypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
