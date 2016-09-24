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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 各种常用的格式化方法.
 * @author jiangnan
 *
 */
public final class Formatter {
	private static final Logger LOG = LoggerFactory.getLogger(Formatter.class);
	

	public static final DateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DATE1_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DATE2_FORMATTER = new SimpleDateFormat("yyyy/MM/dd");
	
	/**
	 * 格式化价格
	 * @param num 价格
	 * @param scale 小数点后保留位数
	 * @return
	 */
	public static String formatPrice(double num){
		return new BigDecimal(num).setScale(2,RoundingMode.CEILING).toPlainString();
	}
	
	/**
	 * Parse string to date using full datetime
	 * @param notifyDateStr
	 * @return
	 */
	public static Date parseDate(String notifyDateStr) {
	    return parseDate(notifyDateStr,null);
	}
	
	/**
	 * @param notifyDateStr
	 * @return
	 */
	public static Date parseDate(String notifyDateStr,DateFormat formatter) {
		if(StringUtils.isEmpty(notifyDateStr)) return null;
		try {
			return (formatter == null ? DATETIME_FORMATTER : formatter).parse(notifyDateStr);
		} catch (ParseException e) {
			LOG.warn("[parseDate] illegal date string: {}",notifyDateStr);
		}
		return null;
	}
	
	
}
