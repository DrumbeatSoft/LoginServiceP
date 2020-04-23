package com.drumbeat.service.login.utils;

import com.blankj.utilcode.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author ZuoHailong
 * @date 2020/4/23
 */
public class SignUtils {

    /**
     * 默认字符集编码。现在推荐使用UTF-8，之前默认是GBK
     */
    private static String DEFAULT_CHARSET = "UTF-8";

    public static final String SIGN_TYPE_RSA = "RSA";

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

    public static String signRsa2(String content, String privateKey) throws Exception {
        try {
            if (StringUtils.isEmpty(content)) {
                throw new Exception("待签名内容不可为空");
            }
            if (StringUtils.isEmpty(privateKey)) {
                throw new Exception("私钥不可为空");
            }
            return doSign(content, DEFAULT_CHARSET, privateKey);
        } catch (Exception e) {

            String errorMessage = "RSA2签名遭遇异常，请检查私钥格式是否正确。" + e.getMessage() +
                    " content=" + content + "，charset=" + DEFAULT_CHARSET + "，privateKeySize=" + privateKey.length();
            throw new Exception(errorMessage, e);
        }
    }

    private static String doSign(String content, String charset, String privateKey) throws Exception {
        PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA, new ByteArrayInputStream(privateKey.getBytes()));

        /*
         * RSA：SIGN_ALGORITHMS
         * RSA2：SIGN_SHA256RSA_ALGORITHMS
         * */
        Signature signature = Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);

        signature.initSign(priKey);

        if (StringUtils.isEmpty(charset)) {
            signature.update(content.getBytes());
        } else {
            signature.update(content.getBytes(charset));
        }

        byte[] signed = signature.sign();

        return new String(Base64.encodeBase64(signed));
    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
                                                    InputStream ins) throws Exception {
        if (ins == null || StringUtils.isEmpty(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        byte[] encodedKey = StreamUtil.readText(ins).getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }


}
