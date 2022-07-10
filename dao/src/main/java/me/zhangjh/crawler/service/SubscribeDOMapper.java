package me.zhangjh.crawler.service;

import me.zhangjh.crawler.entity.SubscribeDO;

import java.util.List;

public interface SubscribeDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SubscribeDO record);

    List<SubscribeDO> selectByQuery(SubscribeDO query);
    SubscribeDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SubscribeDO record);

    int updateByPrimaryKey(SubscribeDO record);
}