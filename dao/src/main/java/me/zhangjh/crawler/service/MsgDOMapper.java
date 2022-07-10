package me.zhangjh.crawler.service;

import me.zhangjh.crawler.entity.MsgDO;

import java.util.List;

public interface MsgDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MsgDO record);

    List<MsgDO> selectByQuery(MsgDO query);

    MsgDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MsgDO record);

    int updateByPrimaryKey(MsgDO record);
}