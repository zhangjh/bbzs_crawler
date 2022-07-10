package me.zhangjh.crawler.service;

import me.zhangjh.crawler.entity.ProductDO;
import me.zhangjh.crawler.entity.ProductQueryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhangjh
 */
@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProductDO record);

    int insertBatch(List<ProductDO> records);

    ProductDO selectByPrimaryKey(Long id);

    ProductDO selectByCode(String code);

    List<ProductDO> selectByQuery(ProductQueryDO productQueryDO);

    int updateByPrimaryKeySelective(ProductDO record);

    int updateByPrimaryKey(ProductDO record);
}