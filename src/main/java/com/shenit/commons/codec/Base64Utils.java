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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shenit.commons.utils.HttpUtils;

/**
 * @author jiangnan
 *
 */
public class Base64Utils
{
	private static final Logger LOG = LoggerFactory.getLogger(Base64Utils.class);

	/**
	 * @param orderTitle
	 * @return
	 */
	public static String base64EncodeHex(String str) {
		return base64EncodeHex(str, HttpUtils.ENC_UTF8);
	}
	
	/**
	 * To base64 hex
	 * @param data
	 * @param enc
	 * @return
	 */
	public static String base64EncodeHex(byte[] data){
		return base64EncodeHex(data,HttpUtils.ENC_UTF8);
	}
	
	/**
	 * To base64 hex
	 * @param data
	 * @param enc
	 * @return
	 */
	public static String base64EncodeHex(byte[] data,String enc){
		if(data == null) return null;
		enc = enc == null ? HttpUtils.ENC_UTF8 : enc;
		try {
	        return new String(Base64.encodeBase64(data),enc);
        } catch (UnsupportedEncodingException e) {
        	if (LOG.isWarnEnabled()) LOG.warn("[base64EncodeHex] ", e);
        }
		return null;
	}

	/**
	 * 变为base 64编码
	 * 
	 * @param str
	 * @param enc
	 * @return
	 */
	public static String base64EncodeHex(String str, String enc) {
		if (StringUtils.isEmpty(str)) return null;
		enc = enc == null ? HttpUtils.ENC_UTF8 : enc;
		try {
			byte[] encoded = Base64.encodeBase64(str.getBytes(enc));
			return new String(encoded, enc);
		} catch (UnsupportedEncodingException e) {
			LOG.warn("[base64Encode] not supported encoding", e);
		}
		return null;
	}

	/**
	 * Base64解码
	 * 
	 * @param str
	 * @return
	 */
	public static String base64DecodeHex(String str) {
		return base64DecodeHex(str, HttpUtils.ENC_UTF8);
	}

	/**
	 * Base64解码
	 * 
	 * @param str
	 * @param enc
	 * @return
	 */
	public static String base64DecodeHex(String str, String enc) {
		if (StringUtils.isEmpty(str)) return null;
		enc = enc == null ? HttpUtils.ENC_UTF8 : enc;
		try {
			return new String(Base64.decodeBase64(str.getBytes(enc)), enc);
		} catch (UnsupportedEncodingException e) {
			LOG.warn("[base64Decode] not supported encoding", e);
		}
		return null;
	}
}
