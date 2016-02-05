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

/**
 * 手机号工具类.
 * @author jiangnan
 *
 */
public final class MobileUtils
{
	private static final String CODE_TELCOM = "telCom";
	private static final String CODE_CHINANET = "chinaNet";
	private static final String CODE_UNIONCOM = "unionCom";
	//移动的号码样式
	private static final String CHINANET_PATTERN = "(?:134[0-8]|(?:(?:13[5-9]|15[017-9]|18[278])\\d))\\d{7}";
	private static final String UNIONCOM_PATTERN = "(?:13[0-2]|15[256]|18[56])\\d{8}";
	private static final String TELCOM_PATTERN = "(?:1349|(?:133|153|180|189)\\d)\\d{8}";
	
	/**
	 * 获取手机类型
	 * @param mobile
	 * @return
	 */
	public static MobileType getMobileType(String mobile){
		MobileType type = null;
		if(isTelComMobile(mobile)) type = MobileType.TelCom;
		else if(isUnionComMobile(mobile)) type = MobileType.UnionCom;
		else if(isChinaTelMobile(mobile)) type = MobileType.ChinaNet;
		return type;
		
	}
	
	/**
	 * 判断是否电信号码.
	 * @param mobile 手机号码
	 * @return
	 */
	public static boolean isTelComMobile(String mobile){
		return mobile != null && mobile.matches(TELCOM_PATTERN);
	}
	
	/**
	 * 判断是否联通号码.
	 * @param mobile 手机号码
	 * @return
	 */
	public static boolean isUnionComMobile(String mobile){
		return mobile != null && mobile.matches(UNIONCOM_PATTERN);
	}
	
	/**
	 * 判断是否移动号码.
	 * @param mobile 手机号码
	 * @return
	 */
	public static boolean isChinaTelMobile(String mobile){
		return mobile != null && mobile.matches(CHINANET_PATTERN);
	}
	
	/**
	 * 手机类型.
	 * @author jiangnan
	 *
	 */
	public static enum MobileType{
		TelCom(CODE_TELCOM,"中国电信"),ChinaNet(CODE_CHINANET,"中国移动"),UnionCom(CODE_UNIONCOM,"中国联通");
		public String code;
		public String name;
		MobileType(String code, String name){
			this.code = code;
			this.name = name;
		}
		public static MobileType getType(String code){
			MobileType type = null;
			switch(code){
			case CODE_TELCOM: type = TelCom; break;
			case CODE_CHINANET: type = ChinaNet; break;
			case CODE_UNIONCOM: type = UnionCom; break;
			}
			return type;
		}
	}
}
