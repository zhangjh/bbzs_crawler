package me.zhangjh.crawler.entity;

import lombok.Data;

/**
 * @author zhangjh
 * @date 2022/7/13
 */
@Data
public class MsgQueryDO {

    private Long userId;

    private String msgId;

    private Integer readStatus;

    private Integer msgType;
}
