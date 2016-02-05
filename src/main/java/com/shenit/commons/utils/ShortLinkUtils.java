/***********************************************************************************************************************
 * 
 * Copyright (C) 2013, 2014 by huya (http://www.huya.com)
 * http://www.huya.com/
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
package com.shenit.commons.utils;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import com.google.common.hash.Hashing;

/**
 * Utilities to genrate short link
 * 
 * @author jiangnan
 * 
 */
public final class ShortLinkUtils {
	private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{HttpUtils.HTTP, HttpUtils.HTTPS});
	
	/**
	 * Shorten url.
	 * @param url URL that not encoded
	 * @return
	 */
	public static String shorten(String url){
		return shorten(url,null,null);
	}
	
	/**
	 * Shorten url.
	 * @param url URL that not encoded
	 * @param salt salt to generate url
	 * @return
	 */
	public static String shorten(String url,String salt){
		return shorten(url,null,salt);
	}
	
	public static String toUrl(String url){
		final String fixedUrl = (URL_VALIDATOR.isValid(url) ? url : HttpUtils.URL_HTTP_PREFIX + url);
		return URL_VALIDATOR.isValid(fixedUrl) ? fixedUrl : null;
	}
	
	
	/**
	 * Shorten url
	 * @param url URL that not encoded
	 * @param query
	 * @return
	 */
	public static String shorten(String url,String query,Object salt) {
		if(StringUtils.isEmpty(url)) return null;
		String joinStr = url.indexOf(HttpUtils.QUERY_CHAR) > 0 ? HttpUtils.QUERY_CHAR : HttpUtils.AMP;
		final String queryParams = (query != null) ? joinStr + query :StringUtils.EMPTY;
		String saltStr = DataUtils.toString(salt,StringUtils.EMPTY);
		url = toUrl((url + queryParams));
		return StringUtils.isEmpty(url) ? null : 
			Hashing.murmur3_32().hashString(url +HttpUtils.HASH_CHAR + saltStr, StandardCharsets.UTF_8).toString();
	}
}