package me.zhangjh.crawler.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangjh
 * @date 2022/7/7
 */
@RestController
@RequestMapping("/bbzs/wx/")
public class WxController {

    private static final String TOKEN = "8FqhwyzIjsmNx2HAfusQrei3p7gSe7XeBEetzPSlCcV";

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };

    @RequestMapping("/msg")
    @ResponseBody
    public boolean checkSignature(String signature, String timestamp, String nonce) throws NoSuchAlgorithmException {
        List<String> list = Arrays.asList(TOKEN, timestamp, nonce);
        Collections.sort(list);
        String join = String.join("", list);
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        sha1.update(join.getBytes(StandardCharsets.UTF_8));
        byte[] digest = sha1.digest();
        int j = digest.length;
        char[] buf = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = digest[i];
            buf[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
            buf[k++] = HEX_DIGITS[byte0 & 0xf];
        }
        String sha1Str = new String(buf);
        return sha1Str.equals(signature);
    }

}
