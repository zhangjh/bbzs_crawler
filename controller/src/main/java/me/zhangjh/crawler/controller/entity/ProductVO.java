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

    private String itemName;

    private String price;

    private String itemPic;

    private String itemCode;

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
        productVO.setItemName(productDO.getItemName());
        productVO.setItemCode(productDO.getItemCode());
        productVO.setPrice(productDO.getPrice());
        productVO.setItemPic(productDO.getItemPic());
        productVO.setItemSize(productDO.getItemSize());
        return productVO;
    }
}
