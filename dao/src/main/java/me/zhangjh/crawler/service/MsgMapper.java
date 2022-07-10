package me.zhangjh.crawler.service;

import me.zhangjh.crawler.entity.MsgDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MsgMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MsgDO record);

    List<MsgDO> selectByQuery(MsgDO query);

    MsgDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MsgDO record);

    int updateByPrimaryKey(MsgDO record);
}