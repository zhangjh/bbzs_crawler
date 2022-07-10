package me.zhangjh.crawler.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangjh
 */
@Data
public class UserDO {
    private Long id;

    private Date createTime;

    private Date updateTime;

    private Boolean isDeleted;

    private String mobile;

    private String outerId;

    private String userInfo;
}