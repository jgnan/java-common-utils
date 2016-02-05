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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shenit.commons.utils.HttpUtils;

/**
 * RSA加解密工具
 * 
 * @author jiangnan
 * 
 */
public class RsaUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(CodecUtils.class);
	public static final String CODEC_RSA = "RSA";

	public static final String SHA1_WITH_RSA = "SHA1WithRSA";

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @return 签名值
	 */
	public static String sign(String content, String privateKey) {
		return sign(content, privateKey, SHA1_WITH_RSA, HttpUtils.ENC_UTF8);
	}

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名值
	 */
	public static String sign(String content, String privateKey, String algorithm, String input_charset) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(CODEC_RSA);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(priKey);
			signature.update(content.getBytes(input_charset));
			byte[] signed = signature.sign();
			return Base64Utils.base64EncodeHex(signed);
		} catch (Exception e) {
			if (LOG.isWarnEnabled()) LOG.warn("[sign] could not sign with exception", e);
		}

		return null;
	}

	/**
	 * 检查加密内容
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @return
	 */
	public static boolean verify(String content, String sign, String publicKey) {
		return verify(content, sign, publicKey, SHA1_WITH_RSA, HttpUtils.ENC_UTF8);
	}

	/**
	 * RSA验签名检查
	 * 
	 * @param content
	 *            待签名数据
	 * @param sign
	 *            签名值
	 * @param publicKey
	 *            支付宝公钥
	 * @param inputCharset
	 *            编码格式
	 * @return 布尔值
	 */
	public static boolean verify(String content, String sign, String publicKey, String algorithm, String inputCharset) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(CODEC_RSA);
			byte[] encodedKey = Base64.decodeBase64(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature.getInstance(algorithm);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(inputCharset));

			boolean bverify = signature.verify(Base64.decodeBase64(sign));
			return bverify;

		} catch (Exception e) {
			if (LOG.isWarnEnabled()) LOG.warn("[verify] verify with exception", e);
		}

		return false;
	}
	
	public static String decrypt(String content, String privateKey){
		return decrypt(content,privateKey,HttpUtils.ENC_UTF8);
	}
	
	/**
	 * 解密
	 * 
	 * @param content
	 *            密文
	 * @param privateKey
	 *            商户私钥
	 * @param inputCharset
	 *            编码格式
	 * @return 解密后的字符串
	 */
	public static String decrypt(String content, String privateKey, String inputCharset) {
		InputStream ins = null;
		ByteArrayOutputStream writer = null;
		String result = null;
		try{
		PrivateKey prikey = getPrivateKey(privateKey);

		Cipher cipher = Cipher.getInstance(CODEC_RSA);
		cipher.init(Cipher.DECRYPT_MODE, prikey);

			ins = new ByteArrayInputStream(Base64.decodeBase64(content));
			writer = new ByteArrayOutputStream();
			// rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
			byte[] buf = new byte[128];
			int bufl;

			while ((bufl = ins.read(buf)) != -1) {
				byte[] block = null;

				if (buf.length == bufl) {
					block = buf;
				} else {
					block = new byte[bufl];
					for (int i = 0; i < bufl; i++) {
						block[i] = buf[i];
					}
				}

				writer.write(cipher.doFinal(block));
			}
			result = new String(writer.toByteArray(), inputCharset);
		} catch (Exception ex){
			if (LOG.isWarnEnabled()) LOG.warn("[decrypt] Decrypt failed with exception.",ex);
		}finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(ins);
		}

		return result;
	}

	/**
	 * 得到私钥
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String key) throws Exception {

		byte[] keyBytes;

		keyBytes = Base64.decodeBase64(key);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance(CODEC_RSA);

		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		return privateKey;
	}
}
