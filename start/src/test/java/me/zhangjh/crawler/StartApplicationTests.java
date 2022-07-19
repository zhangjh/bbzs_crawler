package me.zhangjh.crawler;

import me.zhangjh.crawler.entity.ProductDO;
import me.zhangjh.crawler.service.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class StartApplicationTests {

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void test() {
        ProductDO productDO = new ProductDO();
        productDO.setItemName("test");
        productDO.setSeries("新品");
        productDO.setItemPic("xxx");
        productDO.setItemCode("123450");
        productDO.setBrand("LV");
        productDO.setPrice("100000");
//        productMapper.insert(productDO);
//        productMapper.insertBatch(Collections.singletonList(productDO));
    }

}
