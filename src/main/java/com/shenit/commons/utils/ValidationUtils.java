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

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 校验相关的类.
 * 
 * @author jiangnan
 * 
 */
public final class ValidationUtils {
    public static final String[] FALSE_NAMES = new String[]{Boolean.FALSE.toString(),"fail","failed","no"};
    
    /**
     * 判断某值是否在指定某几个值内
     * 
     * @param val1
     *            值1
     * @param range
     *            范围值
     * @return
     */
    public static boolean in(Object val1, Object... range) {
        if (val1 == null || range == null || range.length < 1) return false;
        for (Object aVal : range) {
            if (val1.equals(aVal)) return true;
        }
        return false;
    }

    /**
     * 判断某值是否在不在指定某几个值内
     * 
     * @param val1
     *            值1
     * @param range
     *            范围值
     * @return
     */
    public static boolean notIn(Object val1, Object... range) {
        return !in(val1, range);
    }

    /**
     * 确保值在指定范围内
     * 
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static long ensureInRange(long val, long min, long max) {
        val = Math.max(min, val);
        val = Math.min(max, val);
        return val;
    }

    /**
     * Indicates whether a collection is null or consist of null
     * 
     * @param cols
     *            A collection
     * @return True if the collection input is null, or consist of null. False if one of its item is not null
     */
    public static boolean isEmpty(Collection<?> cols) {
        if (cols == null || cols.isEmpty()) return true;
        for (Object o : cols) {
            if (o != null) return false;
        }
        return true;
    }
    
    /**
     * Indicates whether a collection is null or consist of null
     * 
     * @param cols
     *            A collection
     * @return True if the collection input is null, or consist of null. False if one of its item is not null
     */
    public static boolean isEmpty(Map<?,?> cols) {
        return cols == null || cols.isEmpty();
    }
    
    /**
     * Indicates whether an array are null or consist of null
     * 
     * @param array
     *            An array
     * @return True if the array input is null, or consist of null. False if one of its item is not null
     */
    public static <T> boolean isEmpty(T[] array) {
        if (array == null || array.length == 0) return true;
        for (Object o : array) {
            if (o != null) return false;
        }
        return true;
    }

    
    /**
     * 返回第一个非空的值
     * @param vals
     * @return
     */
    @SafeVarargs
    public static <T> T notNull(T... vals){
    	int size = vals.length;
    	for(int i=0;i< size; i++){
    		if(vals[i] != null) return vals[i];
    	}
    	return null;
    }

    /**
     * 检查是否所有的值和基准值比较是否都满足检查器规则
     * @param validator 检查器规则
     * @param base 基准值
     * @param vals 待检查值
     * @return
     */
    @SafeVarargs
    public static <T> boolean all(Comparator validator, T base, T... vals) {
        for (T val : vals) {
            if (!validator.compare(val, base)) return false;
        }
        return true;
    }
    
    /**
     * 检查是否所有的值是否都满足检查器规则
     * @param validator 检查器规则
     * @param vals 待检查值
     * @return
     */
    @SafeVarargs
    public static <T> boolean all(Validator validator, T... vals) {
        for (T val : vals) {
            if (!validator.validate(val)) return false;
        }
        return true;
    }
    
    /**
     * 检查是否所有值是否为空
     * @param vals
     * @return
     */
    public static <T> boolean allNull(@SuppressWarnings("unchecked") T... vals){
        return all(NULL,vals);
    }
    
    /**
     * 检查是否所有字符串是否都不为空
     * @param validator 检查器规则
     * @param base 基准值
     * @param vals 待检查值
     * @return
     */
    public static boolean anyEmpty(String... vals) {
        return any(NULL_OR_EMPTY,vals);
    }
    
    /**
     * 检查是否有其中一个值为空.
     * @param vals
     * @return
     */
    public static <T> boolean anyNull(@SuppressWarnings("unchecked") T... vals){
        return any(NULL,vals);
    }

    /**
     * Check whether any values compare to the base value is true.
     * @param comparator Compare logic
     * @param base
     * @param vals
     * @return
     */
    @SafeVarargs
    public static <T> boolean any(Comparator comparator, T base, T... vals) {
        for (T val : vals) {
            if (comparator.compare(val, base)) return true;
        }
        return false;
    }
    
    /**
     * Validate whether any values pass the validation check.
     * @param validator Validation logic
     * @param vals Values to be validated
     * @return
     */
    @SafeVarargs
    public static <T> boolean any(Validator validator, T... vals) {
        for (T val : vals) {
            if (validator.validate(val)) return true;
        }
        return false;
    }

    /**
     * None value of values compare to base is true. 
     * @param comparator Comparator logic to compare base and vals input
     * @param base Base value
     * @param vals Compare targets.
     * @return
     */
    @SafeVarargs
    public static <T> boolean none(Comparator comparator, T base, T... vals) {
        if (vals == null) return false;
        for (T val : vals) {
            if (comparator.compare(val, base)) return false;
        }
        return true;
    }
    
    /**
     * None value of values are validated. 
     * @param validator Validate logic to validate each values
     * @param vals Values to be validated.
     * @return
     */
    @SafeVarargs
    public static <T> boolean none(Validator validator, T... vals) {
        for (T val : vals) {
            if (validator.validate(val)) return false;
        }
        return true;
    }
    
    public static interface Validator{
        boolean validate(Object val);
    }
    
    /**
     * Use for comparation between values
     * 
     * @author jgnan1981@163.com
     * 
     * @param 
     */
    public static interface Comparator {
        boolean compare(Object val, Object base);
    }

    /**
     * Comparator for greater than comparation
     */
    public static final Comparator GT = new Comparator() {

        @SuppressWarnings("unchecked")
        @Override
        public boolean compare(Object val, Object base) {
            if (val == null) return false; // to simplify our logic, always return false when the base value is null
            return base == null ? true : 
                (val instanceof Comparable<?>) ? 
                                ((Comparable<Object>) val).compareTo(base) < 0 : false;
        }

    };

    /**
     * Comparator for less than comparation
     */
    public static final Comparator LT = new Comparator() {

        @SuppressWarnings("unchecked")
        @Override
        public boolean compare(Object val, Object base) {
            if (val == null) return false; // to simplify our logic, always return -1 when the base value is null
            // base value wins return 1
            return base == null ? true : 
                (val instanceof Comparable<?>) ? 
                            ((Comparable<Object>) val).compareTo(base) > 0 : false;
        }
    };

    /**
     * Comparator for equals comparation
     */
    public static final Comparator EQ = new Comparator() {

        @Override
        public boolean compare(Object val, Object base) {
            return val == base || (val != null && base != null && val.equals(base));
        }
    };
    
    /**
     * Comparator for equals ignore case comparation(for string comparation only.
     */
    public static final Comparator EQ_IGNORE_CASE = new Comparator() {

        @Override
        public boolean compare(Object val, Object base) {
            return val == base || (val != null && base != null && val.toString().equalsIgnoreCase(base.toString()));
        }
    };
    
    /**
     * Validator for null validation.
     */
    public static final Validator NULL = new Validator() {

        @Override
        public boolean validate(Object val) {
            return val == null;
        }
    };
    
    public static final Validator NULL_OR_EMPTY = new Validator() {

        @Override
        public boolean validate(Object val) {
            return val == null || StringUtils.isBlank(ShenStringUtils.str(val));
        }
    };

	/**
	 * check value equalization
	 * @param v1
	 * @param v2
	 * @return
	 */
    public static boolean eq(Object v1, Object v2) {
	    return v1 == v2 || (v1 != null && v1.equals(v2));
    }
    
    /**
     * 判断指定值是否在数组中
     * @param target 目标
     * @param vals 值
     * @return
     */
    public static boolean exists(Object target, Object... vals){
        return any(EQ,target,vals);
    }
    
    /**
     * 判断指定值是否在数组中,比较过程忽略大小写
     * @param target 目标
     * @param vals 值
     * @return
     */
    public static boolean existsIgnoreCase(String target, String... vals){
        return any(EQ_IGNORE_CASE,target,vals);
    }
    
    /**
     * 检查密码强度是否足够
     * @param pwd 密码
     * @param simple 是否采用简单规则
     * @return
     */
    public static boolean isPasswordStrong(String pwd, boolean simple){
    	if(StringUtils.isEmpty(pwd)) return false;
    	char last = 0,now = 0;
    	int dupCount = 0;
    	boolean duplicateWords = false, hasLowerCase = false,hasUpperCase = false,
    			hasSpecCase = false, hasNumber = false;
    	for(int i=0 ; i<pwd.length(); i++){
    		now = pwd.charAt(i);
    		if(last != now){
    			last = now;
    			duplicateWords = false;
    			dupCount = 0;
    		} else{
    			if(dupCount > 2) {
    				duplicateWords = true;
    				if(!simple) break;
    			}else{
    				dupCount ++;
    			}
    		}
    		//是否有数字
    		if(!hasNumber && now >= 48 && now < 58) hasNumber = true;
    		//是否有大写字幕
    		else if(!hasUpperCase && now >= 56 && now < 82) hasUpperCase = true;
    		//是否有小写字幕
    		else if(!hasLowerCase && now >= 97 && now < 123) hasLowerCase = true;
    		//是否有特殊字符
    		else if(!simple && !hasSpecCase) hasSpecCase = true;
    	}
    	if(simple){
    		return pwd.length() >= 6 && (hasLowerCase || hasUpperCase) && hasNumber;
    	}else{
    		return pwd.length() >= 8 && hasLowerCase && hasUpperCase && hasNumber && !duplicateWords;
    	}
    	
    }

    /**
     * 判断给定的对象是否为真
     * @param obj 对象
     * @return
     */
    public static boolean isTrue(Object obj) {
        if(obj == null) return false;
        boolean result = false;
        if(obj instanceof String){
            String str = obj.toString();
            if(StringUtils.isEmpty(str)) return false;
            result =ShenStringUtils.isDigital(str)? 
                            Double.parseDouble(str) != 0.0d : 
                            !existsIgnoreCase(str,FALSE_NAMES);
        }else if(obj instanceof Number){
            result = ((Number)obj).doubleValue() != 0.0d;
        }else if(obj instanceof Boolean){
            result = eq(obj,Boolean.TRUE);
        }
        return result;
    }

    /**
     * Check whether value within range
     * @param target
     * @param from
     * @param to
     * @return
     */
    public static boolean between(long target, long... ranges) {
        if(ranges.length == 0) return true;
        boolean check = target >= ranges[0];
        if(ranges.length > 1) check =  check && target <= ranges[1];
        return check;
    }
    
    
   private static String patternStrEmoji = "[\ud83d\ude00-\uD83D\uDE4F|\u2700-\u27BF|\uD83D\uDE80-\uD83D\uDEC0|\u2460-\u24FF|\uD83C\uDD70-\uD83c\uDE51|\u0080-\u00FF]"
                    + "|\u0000-\u007F|\u20D0-\u20FF|\u2100-\u214F|\u2190-\u21FF|\u2300-\u23FF|\u25A0-\u25FF|\u2600-\u26FF|\u2900-\u297F"
                    + "|\2B00-\u2BFF|\u3000-\u303F|\u3200-\u32FF|\uD83C\uDC04-\uD83D\uDDFF|\uD83D\uDE00-\uD83D\uDE36|\uD83D\uDE81-\uD83D\uDEC5"
                    + "|\uD83C\uDF0D-\uD83D\uDD67";
   
   private static Pattern emoji = Pattern.compile(patternStrEmoji, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
   
   /**
    * validate emoji character
    * @param str
    * @return
    */
   public static Boolean hasEmojiCharacter(String str) {
	   
	   Matcher emojiMatcher = emoji.matcher(str);
       return emojiMatcher.find();
   }

   /**
    * Check whether value contains flag
    * @param value Value to be checked 
    * @param flag Flag to check with
    * @return
    */
    public static boolean hasFlag(int value, int flag) {
        return (value & flag) == flag;
    }
   
}
