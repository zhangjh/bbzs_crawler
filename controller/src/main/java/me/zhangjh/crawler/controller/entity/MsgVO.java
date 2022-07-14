package me.zhangjh.crawler.controller.entity;

import lombok.Data;
import me.zhangjh.crawler.constant.MsgTypeEnum;
import me.zhangjh.crawler.entity.MsgDO;

import java.text.SimpleDateFormat;

/**
 * @author zhangjh
 * @date 2022/7/12
 */
@Data
public class MsgVO {
    private Long id;

    private String createTime;

    private Long userId;

    private String type;

    private String content;

    private String readStatus;

    public static MsgVO transferDO2VO(MsgDO msgDO) {
        if(msgDO == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MsgVO msgVO = new MsgVO();
        msgVO.setId(msgDO.getId());
        msgVO.setUserId(msgDO.getUserId());
        msgVO.setCreateTime(dateFormat.format(msgDO.getCreateTime()));
        msgVO.setType(MsgTypeEnum.getDescByType(msgDO.getType()));
        msgVO.setContent(msgDO.getContent());
        msgVO.setReadStatus(msgDO.getReadStatus().toString());
        return msgVO;
    }
}
