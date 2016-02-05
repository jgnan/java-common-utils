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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
package com.shenit.commons.codec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shenit.commons.utils.HttpUtils;
import com.shenit.commons.utils.ShenStringUtils;

/**
 * @author jiangnan
 *
 */
public class DesUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(CodecUtils.class);
	public static final String CODEC_DES = "DES";

	

	/**
	 * 解密字符串
	 * @param codec
	 * @param srcHex
	 * @param key
	 * @return
	 */
	public static String decryptHex(String srcHex, byte[] key) {
		String result = null;
		try {
			result = new String(decrypt(Hex.decodeHex(srcHex.toCharArray()), key));
		} catch (DecoderException e) {
			LOG.warn("could not decode hex string -> {}", srcHex);
		}
		return result;
	}

	/**
	 * 解密字符串
	 * @param codec
	 * @param srcHex
	 * @param key
	 * @return
	 */
	public static String decryptHex(String srcHex, String key) {
        return decryptHex(ShenStringUtils.bytes(srcHex), ShenStringUtils.bytes(key));
	}

	/**
	 * 解密字符串
	 * @param src
	 * @param key
	 * @return
	 */
	public static String decryptHex(byte[] src, byte[] key) {
		return new String(decryptHex(src, key));
	}
	
	/**
	 * 生成加密字符串
	 * @param codec
	 * @param src
	 * @param key
	 * @return
	 */
	public static String encryptHex(byte[] src, KeySpec key) {
		return new String(Hex.encodeHex(encrypt(src, key)));
	}

	/**
	 * 生成加密字符串
	 * @param codec
	 * @param src
	 * @param key
	 * @return
	 */
	public static String encryptHex(byte[] src, byte[] key) {
		return new String(Hex.encodeHex(encrypt(src, key)));
	}


	/**
	 * 生成加密字符串
	 * @param codec
	 * @param src
	 * @param key
	 * @param secure
	 * @return
	 */
	public static String encryptHex(String codec, String src, String key) {
		try {
	        return new String(
	        		Hex.encodeHex(encrypt(src.getBytes(HttpUtils.ENC_UTF8), key.getBytes(HttpUtils.ENC_UTF8))));
        } catch (UnsupportedEncodingException e) {
        	if (LOG.isWarnEnabled()) LOG.warn("[encryptHex] Could not encrypt to hex due to exception", e);
        }
		return null;
	}

	/**
	 * 解密方法
	 * 
	 * @param rawKeyData
	 * @param encryptedData
	 */
	public static byte[] decrypt(byte[] encryptedData, byte[] rawKey) {
		return crypt(encryptedData, rawKey, Cipher.DECRYPT_MODE);
	}
	
	/**
	 * 解密方法
	 * 
	 * @param keySpec
	 * @param encryptedData
	 */
	public static byte[] decrypt(byte[] encryptedData,KeySpec keySpec) {
		return crypt(encryptedData, keySpec, Cipher.DECRYPT_MODE);
	}
	
	/**
	 * 加密方法
	 * @param rawData
	 * @param rawKey
	 * @return
	 */
	public static byte[] encrypt(byte[] rawData,KeySpec key) {
		return crypt(rawData, key, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 加密方法
	 * 
	 * @param rawKeyData
	 * @param slat
	 * @param secure
	 *            是否采用SecureRandom生成安全的秘钥
	 * @return
	 */
	public static byte[] encrypt(byte[] rawData, byte[] rawKey) {
		return crypt(rawData, rawKey, Cipher.ENCRYPT_MODE);
	}
	

	/**
	 * @param rawData
	 * @param rawKey
	 * @param mode
	 */
	private static byte[] crypt(byte[] rawData, byte[] rawKey, int mode) {
		try {
	        return crypt(rawData,new DESKeySpec(rawKey),mode);
        } catch (InvalidKeyException e) {
        	if (LOG.isWarnEnabled()) LOG.warn("[crypt] could not generate key spec for des",e);
        }
		return null;
	}
	
	/**
	 * 加解密方法
	 * @param rawData
	 * @param rawKey
	 * @param mode
	 */
	private static byte[] crypt(byte[] rawData, KeySpec keySpec, int mode) {
		// 从原始密匙数据创建一个DESKeySpec对象
		byte[] result = null;
		try {
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CODEC_DES);
			Key key = keyFactory.generateSecret(keySpec);
			if(key == null){
				if (LOG.isWarnEnabled()) LOG.warn("[crypt] No key generated!");
				return null;
			}
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance(CODEC_DES);
			// DES算法要求有一个可信任的随机数源
			cipher.init(mode, key, new SecureRandom());
			// 现在，获取数据并加密
			// 正式执行加密操作
			result = cipher.doFinal(rawData);
		} catch (Exception ex) {
			LOG.warn("[crypt] crypt with exceptions", ex);
		}
		return result;
	}
}
