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
package com.shenit.commons.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.CaseFormat;

/**
 * 字符串工具集
 * @author jiangnan
 *
 */
public final class ShenStringUtils {
	public static final String SPACE = " ";
	public static final String DELIMITER_COMMA = ",";
	public static final String DELIMITER_DOT = ".";
	public static final String DELIMITER_DASH = "-";
	public static final String DELIMITER_UNDERSCORE = "_";
	public static final String LIKE_PATTERN = "%";
	public static final String NULL_STR = "null";
	public static final String COLON = ":";
	public static final String SEMICOLON = ";";
	public static final String LINE_SEPERATOR = "\r\n";
	
	/**
	 * 把字符串中所有符合样式的字串替换replacement的内容.
	 * @param source 原字串
	 * @param replacement 替换字串
	 * @param patterns 替换样式
	 * @return
	 */
	public static String replaceAll(String source, String replacement, String... patterns){
		for(String pattern : patterns){
			source = source.replaceAll(pattern, replacement);
		}
		return source;
	}
	
	/**
	 * To camel format
	 * @param str
	 * @return
	 */
	public static String camel(String str){
	    if(str.indexOf(DELIMITER_UNDERSCORE) < 0) return str;
	    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,str(str));
	}
	
	/**
	 * To underscore format
	 * @param str
	 * @return
	 */
	public static String underscore(String str){
	    if(str.indexOf(DELIMITER_UNDERSCORE) > 0) return str;
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,str(str));
    }
	
	/**
	 * 把对象强制转换为字符串
	 * @param val
	 * @return
	 */
	public static String str(Object val){
	    return val == null ? StringUtils.EMPTY : val.toString();
	}
	
	/**
	 * 判断字符串是否一个IP
	 * @param str
	 * @return
	 */
	public static boolean isIp(String str){
		if(str.indexOf(DELIMITER_DOT) < 0) return false;
		String[] secs = str.split(DELIMITER_DOT);
		//we don't support IPv6 yet!!
		if(secs.length != 4) return false;
		boolean isIp = true;
		int ipSec;
		for(String sec : secs){
			if(StringUtils.isNumeric(sec)){
				ipSec = Integer.parseInt(sec);
				isIp = !ValidationUtils.in(ipSec, 0,255);
			}
			if(!isIp) break;
		}
		return isIp;
	}
	
	/**
	 * 获取UTF8编码字节码
	 * @param source
	 * @return
	 */
	public static byte[] bytes(String source){
		return bytes(source, null);
	}
	
	/**
	 * 获取字节数
	 * @param source
	 * @param enc
	 * @return
	 */
	public static byte[] bytes(String source, String enc){
		if(source == null ) return null;
		try {
	        return source.getBytes(DataUtils.first(enc,HttpUtils.ENC_UTF8));
        } catch (UnsupportedEncodingException e) {
        }
		return null;
	}

	/**
	 * @param appNavIndex
	 * @param string
	 * @return
	 */
    public static String wrap(String string, String wrapping) {
    	if(string == null) return null;
	    return StringUtils.isEmpty(string) ? (wrapping+wrapping) : (wrapping + string+ wrapping);
    }
    
    /**
     * 获取指定字符串中特定字符的位置
     * @param str
     * @param search
     * @return
     */
    public static int indexOf(String str, String search) {
        return str == null ? -1 : str.indexOf(search);
    }

    /**
     * 判断指定字符是否在特定字符串中
     * @param url 
     * @param queryChar
     * @return
     */
    public static boolean has(String str, String search) {
        return indexOf(str,search) > -1;
    }
    
    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isDigital(String str){
        if(StringUtils.isEmpty(str)) return false;
        boolean result = true;
        try{
            Double.parseDouble(str);
        }catch(Throwable t){
            result = false;
        }
        return result;
    }

    /**
     * Split a string with delimiter
     * @param str
     * @param delimiter
     * @return
     */
    public static String[] split(String str, String delimiter) {
        return StringUtils.isEmpty(str) ? null : str.split(delimiter);
    }
    
    /**
     * Split a string with delimiter
     * @param str
     * @param delimiter
     * @return
     */
    public static List<String> splitAsArray(String str, String delimiter) {
        return Arrays.asList(split(str,delimiter));
    }
    
    /**
     * 解析IP:Port地址字符串，返回ip/port组成的pair列表。 <br/>
     * <strong>不检查</strong>输入的参数是否合法
     * 
     * @param addresStr 地址间用‘;’分开，eg.: localhost:1234;localhost:4321
     * @return Pair.left:ip Pair.right:port
     */
    public static List<Pair<String, Integer>> parseNetAddresses(String addresStr) {
        List<Pair<String, Integer>> pairs = new ArrayList<>();
        
        String[] addres = split(addresStr, SEMICOLON);
        if (addres == null) return pairs;
        
        // 不检查，出错了就各种抛异常吧。
        for (String addr : addres) {
            String[] pair = split(addr, COLON);
            String ip = pair[0];
            int port = Integer.parseInt(pair[1]);
            pairs.add(Pair.of(ip, port));
        }
        
        return pairs;
    }
}
