package me.zhangjh.crawler.controller.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Data
public class UserReq implements Serializable {
    private static final long serialVersionUID = -1166877232698010879L;

    private String outerId;

    private Integer outerType;

    private String avatarUrl;

    private String nickName;

    private Integer gender;

    private String country;

    private String province;

    private String city;
}
