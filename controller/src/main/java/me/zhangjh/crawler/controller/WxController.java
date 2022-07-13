package me.zhangjh.crawler.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.zhangjh.crawler.constant.SubscribeTypeEnum;
import me.zhangjh.crawler.constant.UserTypeEnum;
import me.zhangjh.crawler.controller.entity.MsgVO;
import me.zhangjh.crawler.controller.entity.ProductVO;
import me.zhangjh.crawler.controller.request.ProductReq;
import me.zhangjh.crawler.controller.request.UserReq;
import me.zhangjh.crawler.controller.response.PageResponse;
import me.zhangjh.crawler.controller.response.Response;
import me.zhangjh.crawler.entity.*;
import me.zhangjh.crawler.service.MsgMapper;
import me.zhangjh.crawler.service.ProductMapper;
import me.zhangjh.crawler.service.SubscribeMapper;
import me.zhangjh.crawler.service.UserMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
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

    @Autowired
    private MsgMapper msgMapper;

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
            UserDO userDO = userMapper.selectByOuterId(wxId, UserTypeEnum.WEIXIN.getType());
            if(userDO == null) {
                return Response.fail("未查询到有效用户，可能没有授权注册");
            }
            Long userId = userDO.getId();
            SubscribeDO subscribeDO = new SubscribeDO();
            subscribeDO.setUserId(userId);
            List<SubscribeDO> subscribeDOS = subscribeMapper.selectByQuery(subscribeDO);
            if(CollectionUtils.isNotEmpty(subscribeDOS)) {
                log.info("addSubscribe, wxId: {}, userId: {}已经订阅过", wxId, userId);
                return Response.success(null);
            }
            // 默认不失效
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 99);
            subscribeDO.setExpiredTime(calendar.getTime());
            subscribeDO.setType(SubscribeTypeEnum.FOREVER.getType());
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
            Assert.isTrue(UserTypeEnum.checkType(req.getOuterType()), "非法的用户类型");
            userDO.setOuterType(req.getOuterType());
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
            UserDO userDO = userMapper.selectByOuterId(outerId, UserTypeEnum.WEIXIN.getType());
            return Response.success(userDO);
        } catch (Exception e) {
            log.error("getUser exception, outerId: {}, e: {}", outerId, e);
            return Response.fail(e.getMessage());
        }
    }

    @RequestMapping("/getMsg")
    @ResponseBody
    public Response<List<MsgVO>> getMsg(String outerId) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(outerId), "outerId为空");
            Response<UserDO> userRes = this.getUser(outerId);
            Assert.isTrue(userRes.isSuccess() && userRes.getData() != null,
                    "获取用户失败, outerId:" + outerId);
            Long userId = userRes.getData().getId();
            MsgQueryDO queryDO = new MsgQueryDO();
            queryDO.setUserId(userId);
            queryDO.setReadStatus(0);
            List<MsgDO> msgDOS = msgMapper.selectByQuery(queryDO);
            List<MsgVO> msgVOS = msgDOS.stream().map(MsgVO::transferDO2VO).collect(Collectors.toList());
            return Response.success(msgVOS);
        } catch (Exception e) {
            log.error("getMsg exception, outerId: {}, e", outerId, e);
            return Response.fail(e.getMessage());
        }
    }

    @RequestMapping("/getNoticed")
    @ResponseBody
    public Response<Boolean> getNoticed(String outerId) {
        try {
            Response<UserDO> userRes = this.getUser(outerId);
            Assert.isTrue(userRes.isSuccess() && userRes.getData() != null,
                    "获取用户失败, outerId:" + outerId);
            Long userId = userRes.getData().getId();
            SubscribeDO subscribeQuery = new SubscribeDO();
            subscribeQuery.setUserId(userId);
            List<SubscribeDO> subscribeDOS = subscribeMapper.selectByQuery(subscribeQuery);
            Assert.isTrue(subscribeDOS.size() == 1, "订阅用户查询大于1");
            SubscribeDO subscribeDO = subscribeDOS.get(0);
            String feature = subscribeDO.getFeature();
            JSONObject featureMap = JSONObject.parseObject(feature);
            if(featureMap == null) {
                return Response.success(false);
            }
            return Response.success(featureMap.getBoolean("noticed"));
        } catch (Exception e) {
            log.error("getNoticed exception, outerId: {}, e", outerId, e);
            return Response.fail(e.getMessage());
        }
    }

    @RequestMapping("/updateMsgRead")
    @ResponseBody
    public Response<Void> updateMsgRead(String outerId, Long msgId) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(outerId), "outerId为空");
            Assert.isTrue(msgId != null, "msgId为空");

            Response<UserDO> userRes = this.getUser(outerId);
            Assert.isTrue(userRes.isSuccess() && userRes.getData() != null,
                    "获取用户失败, outerId:" + outerId);
            Long userId = userRes.getData().getId();
            SubscribeDO subscribeQuery = new SubscribeDO();
            subscribeQuery.setUserId(userId);
            List<SubscribeDO> subscribeDOS = subscribeMapper.selectByQuery(subscribeQuery);
            Assert.isTrue(subscribeDOS.size() == 1, "订阅用户查询大于1");
            SubscribeDO subscribe = subscribeDOS.get(0);
            String feature = subscribe.getFeature();
            JSONObject featureMap = JSONObject.parseObject(feature);
            // 是否已知晓，已知晓忽略，为空则记录更新
            if(featureMap == null) {
                featureMap = new JSONObject();
            }
            Boolean noticed = featureMap.getBoolean("noticed");
            if(noticed == null || !noticed) {
                featureMap.put("noticed", 1);
                subscribe.setFeature(JSONObject.toJSONString(featureMap));
                subscribeMapper.updateByPrimaryKeySelective(subscribe);
            }

            MsgDO msgDO = new MsgDO();
            msgDO.setId(msgId);
            msgDO.setReadStatus(true);
            msgMapper.updateByPrimaryKeySelective(msgDO);
            return Response.success(null);
        } catch (Exception e) {
            log.error("updateMsgRead exception, msgId: {}, e", msgId, e);
            return Response.fail(e.getMessage());
        }
    }
}
