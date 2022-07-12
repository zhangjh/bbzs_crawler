package me.zhangjh.crawler.constant;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author zhangjh
 * @date 2022/7/12
 */
@Getter
public enum UserTypeEnum {
    WEIXIN(1, "微信"),
    ;
    private Integer type;
    private String desc;

    UserTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean checkType(Integer type) {
        return Arrays.stream(UserTypeEnum.values()).anyMatch(item -> item.getType().equals(type));
    }
}
