package me.zhangjh.crawler.service;

import me.zhangjh.crawler.entity.FeedDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FeedDO record);

    int insertSelective(FeedDO record);

    FeedDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FeedDO record);

    int updateByPrimaryKey(FeedDO record);
}