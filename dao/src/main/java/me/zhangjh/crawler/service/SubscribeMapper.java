package me.zhangjh.crawler.service;

import me.zhangjh.crawler.entity.SubscribeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubscribeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SubscribeDO record);

    List<SubscribeDO> selectByQuery(SubscribeDO query);

    SubscribeDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SubscribeDO record);

    int updateByPrimaryKey(SubscribeDO record);
}