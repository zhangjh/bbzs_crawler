package me.zhangjh.crawler.controller.entity;

import lombok.Data;
import me.zhangjh.crawler.entity.ProductDO;

import java.io.Serializable;

/**
 * @author zhangjh
 * @date 2022/7/10
 */
@Data
public class ProductVO implements Serializable {
    private static final long serialVersionUID = 6407223946635178680L;

    private Long id;

    private String brand;

    private String className;

    private String series;

    private String title;

    private String price;

    private String thumb;

    private String itemUrl;

    private String spuId;

    private String itemSize;

    public static ProductVO transferDO2VO(ProductDO productDO) {
        if(productDO == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        productVO.setId(productDO.getId());
        productVO.setBrand(productDO.getBrand());
        productVO.setSeries(productDO.getSeries());
        productVO.setClassName(productDO.getClassName());
        productVO.setTitle(productDO.getItemName());
        productVO.setSpuId(productDO.getItemCode());
        productVO.setPrice(productDO.getPrice());
        productVO.setThumb(productDO.getItemPic());
        productVO.setItemUrl(productDO.getItemUrl());
        productVO.setItemSize(productDO.getItemSize());
        return productVO;
    }
}
