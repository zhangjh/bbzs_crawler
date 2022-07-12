package me.zhangjh.crawler.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangjh
 * @date 2022/7/12
 */
@Data
public class WxMsgDTO implements Serializable {
    private static final long serialVersionUID = 6264913506546744389L;

    // 商品名称
    private WxMsgField thing1;

    // 上架时间
    private WxMsgField time5;

    // 价格
    private WxMsgField amount2;

    // 备注
    private WxMsgField thing3;
}
