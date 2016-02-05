/***********************************************************************************************************************
 * 
 * Copyright (C) 2013, 2014 by huanju (http://www.yy.com)
 * http://www.yy.com/
 * 
 *********************************************************************************************************************** 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 **********************************************************************************************************************/
package com.shenit.commons.codec;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

import javax.crypto.KeyGenerator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shenit.commons.utils.HttpUtils;
import com.shenit.commons.utils.MapUtils;
import com.shenit.commons.utils.ShenStringUtils;

/**
 * DES加解密算法.
 * 
 * @author jiangnan
 * 
 */
public class CodecUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(CodecUtils.class);
    public static final String CODEC_DES = "DES";

    /**
     * 生成随机秘钥
     * 
     * @return
     */
    public static byte[] generateRandomKey(String codec) {
        SecureRandom sr = new SecureRandom();
        // 为我们选择的DES算法生成一个KeyGenerator对象
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(codec);
            kg.init(sr);
            // 生成密匙
            Key secret = kg.generateKey();
            // 获取密匙数据
            return secret.getEncoded();
        }
        catch (NoSuchAlgorithmException e) {
            LOG.warn("[generateKey] not supported algorithm -> " + codec);
        }
        return null;
    }

    /**
     * 生成MD5加密串
     * 
     * @param source
     * @return
     */
    public static String md5Hex(String source) {
        return md5Hex(source, null);
    }

    /**
     * 转换为unicode
     * @param s
     * @return
     */
    public static String unicode(String s) {
        try {
            StringBuffer out = new StringBuffer("");
            byte[] bytes = s.getBytes("unicode");
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);

            }
            return out.toString();
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 生成MD5加密串
     * 
     * @param source
     * @param salt
     * @return
     */
    public static String md5Hex(String source, String salt) {
        return DigestUtils.md5Hex(source + (salt == null ? 
                        StringUtils.EMPTY : (ShenStringUtils.DELIMITER_UNDERSCORE+ salt)));
    }
    
    /**
     * Verify signature
     * @param params Params to be verified
     * @param salt Salt for the params
     * @param sign Signature to be verified
     * @return
     */
    public static boolean verifySignature(Map<?,?> params, String salt, String sign){
        String query = MapUtils.join(params,HttpUtils.EQ, HttpUtils.AMP);
        if (LOG.isDebugEnabled()) LOG.debug("[verifySignature] query -> {}",query);
        query = CodecUtils.md5Hex(query,salt);
        if (LOG.isDebugEnabled()) LOG.debug("[verifySignature] sign -> {}, query -> {}",sign, query);
        return query.equals(sign);
    }
}
