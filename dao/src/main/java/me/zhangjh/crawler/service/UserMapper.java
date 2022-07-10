package me.zhangjh.crawler.service;

import me.zhangjh.crawler.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO record);

    UserDO selectByPrimaryKey(Long id);

    UserDO selectByOuterId(String outerId);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);
}