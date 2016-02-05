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

import java.util.Random;


/**
 * 验证码工具类.
 * @author jiangnan
 *
 */
public final class VerifyCodeUtils
{
	private static final char[] NUMERIC_CODES;
	private static final char[] CHAR_CODES;
	private static final char[] WORD_CODES;
	//初始化代码
	static{
		NUMERIC_CODES = new char[10];
		CHAR_CODES = new char[26];
		WORD_CODES = new char[36];
		int aindex = 0,bindex = 0;
		char aChar;
		for(int i=48; i< 58; i++){
			aChar = (char)i;
			NUMERIC_CODES[aindex] = aChar;
			WORD_CODES[aindex] = aChar;
			aindex ++;
		}
		for(int i=97;i < 123; i++){
			aChar = (char)i;
			CHAR_CODES[bindex] = aChar;
			WORD_CODES[aindex + bindex] = aChar;
			bindex ++;
		}
	}
	
	/**
	 * 生成验证码
	 * @param length 长度
	 * @param type 类型
	 * @return
	 */
	public static String generateCode(int length,VerifyCodeType type){
		String code = null;
		switch(type){
		case Numeric:
			code = generateCode(length,NUMERIC_CODES);
			break;
		case Char:
			code = generateCode(length,CHAR_CODES);
			break;
		default:
			code = generateCode(length,WORD_CODES);
			break;
		}
		return code;
	}
	
	/**
	 * 生成验证码
	 * @param length 长度
	 * @param codes 生成字符元集合
	 * @return
	 */
	public static String generateCode(int length, char[] codes){
		StringBuilder builder = new StringBuilder();
		Random rand = new Random(System.currentTimeMillis());
		int codesLength = codes.length;
		for(int i=0;i<length;i++){
			builder.append(codes[rand.nextInt(codesLength)]);
		}
		return builder.toString();
	}
	
	public static enum VerifyCodeType{
		Numeric, Char, Word;
	}
}
