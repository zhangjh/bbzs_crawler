package me.zhangjh.crawler.controller;

import me.zhangjh.crawler.controller.entity.ProductVO;
import me.zhangjh.crawler.controller.request.ProductReq;
import me.zhangjh.crawler.controller.response.PageResponse;
import me.zhangjh.crawler.controller.response.Response;
import me.zhangjh.crawler.entity.ProductDO;
import me.zhangjh.crawler.entity.ProductQueryDO;
import me.zhangjh.crawler.service.ProductMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangjh
 * @date 2022/7/7
 */
@RestController
@RequestMapping("/bbzs/wx/")
public class WxController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String TOKEN = "wiredSheep";

    @Autowired
    private ProductMapper productMapper;

    @RequestMapping("/msg")
    @ResponseBody
    public String checkSignature(String signature, String timestamp, String nonce, String echostr) {
        log.info("checkSignature, signature: {}, timestamp: {}, nonce: {}", signature, timestamp, nonce);
        List<String> list = Arrays.asList(TOKEN, timestamp, nonce);
        Collections.sort(list);
        String join = String.join("", list);
        String sha1Str = DigestUtils.sha1Hex(join);
        log.info("sha1Str: {}", sha1Str);
        if(sha1Str.equals(signature)) {
            return echostr;
        } else {
            return "";
        }
    }

    @RequestMapping("/getProduct")
    @ResponseBody
    public Response<List<ProductVO>> getProduct(ProductReq req) {
        try {
            Integer pageIndex = req.getPageIndex();
            Integer pageSize = req.getPageSize();
            ProductQueryDO queryDO = new ProductQueryDO();
            if(pageIndex != null) {
                queryDO.setPageIndex(pageIndex);
                queryDO.setPageSize(pageSize);
                queryDO.setOffset((pageIndex - 1) * pageSize);
            }
            List<ProductDO> productDOS = productMapper.selectByQuery(queryDO);
            List<ProductVO> productVOS = productDOS.stream().map(ProductVO::transferDO2VO).collect(Collectors.toList());
            return Response.success(productVOS);
        } catch (Exception e) {
            log.error("getProduct exception, req: {}, e: {}", req, e);
            return PageResponse.fail(e.getMessage());
        }
    }
}
