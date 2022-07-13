package me.zhangjh.crawler.service;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.crawler.dto.WxMsgDTO;
import me.zhangjh.crawler.dto.WxMsgField;
import me.zhangjh.crawler.entity.ProductDO;
import me.zhangjh.crawler.entity.SubscribeDO;
import me.zhangjh.crawler.entity.UserDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjh
 * @date 2022/7/12
 */
@Component
@Slf4j
public class SubscribeNotify {

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String secret;

    @Value("${wx.templateId}")
    private String templateId;

    @Value("${wx.programState}")
    private String programState;

    @Autowired
    private SubscribeMapper subscribeMapper;

    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    public void test() {
        System.out.println(this.getWxToken());
        ProductDO productDO = new ProductDO();
        productDO.setItemName("LV包包");
        productDO.setPrice("10000");
        productDO.setCreateTime(new Date());
        this.send(productDO);
    }

    public String getWxToken() {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +
                    appId + "&secret=" + secret);
            CloseableHttpResponse response = client.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == 200) {
                String res = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject jo = JSONObject.parseObject(res);
                return jo.getString("access_token");
            } else {
                throw new RuntimeException("请求出错, res status: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("getWxToken exception, e:", e);
            return null;
        }
    }

    @SneakyThrows
    public Boolean send(ProductDO productDO) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String token = this.getWxToken();
        Assert.isTrue(StringUtils.isNotBlank(token), "获取accessToken失败");
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost =
                    new HttpPost("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + token);
            WxMsgDTO msgDTO = new WxMsgDTO();
            msgDTO.setThing1(new WxMsgField(productDO.getItemName()));
            msgDTO.setThing3(new WxMsgField("上新啦！点击查看消息"));
            msgDTO.setTime5(new WxMsgField(sdf.format(productDO.getCreateTime())));
            msgDTO.setAmount2(new WxMsgField(productDO.getPrice()));

            Map<String, Object> params = new HashMap<>();
            params.put("template_id", templateId);
            params.put("page", "pages/msg/index");
            params.put("data", msgDTO);
            params.put("miniprogram_state", programState);

            SubscribeDO subscribeDO = new SubscribeDO();
            subscribeDO.setExpiredTime(new Date());
            List<SubscribeDO> subscribes = subscribeMapper.selectByQuery(subscribeDO);
            for (SubscribeDO subscribe : subscribes) {
                Long userId = subscribe.getUserId();
                UserDO userDO = userMapper.selectByPrimaryKey(userId);
                Assert.isTrue(userDO != null, "userId错误，userId:" + userId);
                String outerId = userDO.getOuterId();
                Assert.isTrue(StringUtils.isNotBlank(outerId), "微信openid为空");
                params.put("touser", outerId);
                httpPost.setEntity(new StringEntity(JSONObject.toJSONString(params), ContentType.APPLICATION_JSON));
                CloseableHttpResponse response = client.execute(httpPost);
                if(response.getStatusLine().getStatusCode() == 200) {
                    String res = EntityUtils.toString(response.getEntity());
                    JSONObject jo = JSONObject.parseObject(res);
                    String errcode = jo.getString("errcode");
                    if(StringUtils.isNotBlank(errcode) && !"0".equals(errcode)) {
                        log.error("sendMsg failed, userId: {}, res: {}", userId, res);
                        return false;
                    }
                    return true;
                } else {
                    throw new RuntimeException("请求出错, res status: " + response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            log.error("sendMsg exception, e:", e);
            return false;
        }
        return false;
    }
}