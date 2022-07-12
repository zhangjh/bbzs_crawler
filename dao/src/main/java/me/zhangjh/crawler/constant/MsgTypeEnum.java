package me.zhangjh.crawler.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author zhangjh
 * @date 2022/7/12
 */
@Getter
public enum MsgTypeEnum {

    SYS_MSG(0, "系统消息"),
    NEW_PRODUCT(1, "上新消息"),
    UNKNOWN(-1, "未知消息"),
    ;

    private Integer type;
    private String desc;

    MsgTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static String getDescByType(Integer type) {
        Optional<MsgTypeEnum> typeEnum = Arrays.stream(MsgTypeEnum.values()).filter(item -> item.type.equals(type)).findFirst();
        return typeEnum.orElse(UNKNOWN).desc;
    }
}
