package me.zhangjh.crawler.controller;

import com.alibaba.fastjson.JSON;
import me.zhangjh.crawler.constant.TypeEnum;
import me.zhangjh.crawler.controller.entity.ProductVO;
import me.zhangjh.crawler.controller.request.ProductReq;
import me.zhangjh.crawler.controller.request.UserReq;
import me.zhangjh.crawler.controller.response.PageResponse;
import me.zhangjh.crawler.controller.response.Response;
import me.zhangjh.crawler.entity.ProductDO;
import me.zhangjh.crawler.entity.ProductQueryDO;
import me.zhangjh.crawler.entity.SubscribeDO;
import me.zhangjh.crawler.entity.UserDO;
import me.zhangjh.crawler.service.ProductMapper;
import me.zhangjh.crawler.service.SubscribeMapper;
import me.zhangjh.crawler.service.UserMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Calendar;
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

    @Autowired
    private SubscribeMapper subscribeMapper;

    @Autowired
    private UserMapper userMapper;

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

    @RequestMapping("/addSubscribe")
    @ResponseBody
    public Response<Void> addSubscribe(String wxId) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(wxId), "wxId为空");
            UserDO userDO = userMapper.selectByOuterId(wxId);
            if(userDO == null) {
                return Response.fail("未查询到有效用户，可能没有授权注册");
            }
            Long userDOId = userDO.getId();
            SubscribeDO subscribeDO = new SubscribeDO();
            subscribeDO.setUserId(userDOId);
            // 默认不失效
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 99);
            subscribeDO.setExpiredTime(calendar.getTime());
            subscribeDO.setType(TypeEnum.FOREVER.getType());
            subscribeMapper.insert(subscribeDO);
            return Response.success(null);
        } catch (Exception e) {
            log.error("addSubscribe exception, wxId: {}, e: {}", wxId, e);
            return Response.fail(e.getMessage());
        }
    }

    @PostMapping("/addUser")
    @ResponseBody
    public Response<Void> addUser(UserReq req) {
        try {
            UserDO userDO = new UserDO();
            Assert.isTrue(StringUtils.isNotBlank(req.getOuterId()), "outerId为空");
            userDO.setOuterId(req.getOuterId());
//            userDO.setMobile();
            userDO.setUserInfo(JSON.toJSONString(req));
            userMapper.insert(userDO);
            return Response.success(null);
        } catch (Exception e) {
            log.error("addUser exception, req: {}, e: {}", req, e);
            return Response.fail(e.getMessage());
        }
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public Response<UserDO> getUser(String outerId) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(outerId), "outerId为空");
            UserDO userDO = userMapper.selectByOuterId(outerId);
            return Response.success(userDO);
        } catch (Exception e) {
            log.error("getUser exception, outerId: {}, e: {}", outerId, e);
            return Response.fail(e.getMessage());
        }
    }
}
