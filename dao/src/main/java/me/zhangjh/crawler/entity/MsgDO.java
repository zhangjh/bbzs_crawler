package me.zhangjh.crawler.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangjh
 */
@Data
public class MsgDO {
    private Long id;

    private Date createTime;

    private Date updateTime;

    private Boolean isDeleted;

    private Long userId;

    private Integer type;

    private String content;

    private Boolean readStatus;

    private String feature;
}