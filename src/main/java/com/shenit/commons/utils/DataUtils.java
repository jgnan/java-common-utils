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

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数组、集合和Map的工具.
 * 
 * @author jiangnan
 * 
 */
public final class DataUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DataUtils.class);
    public static final BigDecimal DECIMAL_ZERO = new BigDecimal(0);

    /**
     * 把多个参数组装成相同类型的数组
     * 
     * @param vals
     *            参数集合
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] array(T... vals) {
        return vals;
    }
    
    
    /**
     * 判断一个对象类型是否为基础类型
     * @param obj
     * @return
     */
    public static boolean isPrimative(Object obj){
        return obj != null && ValidationUtils.in(obj.getClass(),
                        int.class,Integer.class,
                        short.class,Short.class,
                        long.class,Long.class,
                        char.class,Character.class,
                        boolean.class,Boolean.class,
                        float.class,Float.class,
                        double.class,Double.class,
                        String.class);
    }

    /**
     * 去从方法
     * 
     * @param vals
     * @return
     */
    public static <T> T[] unique(@SuppressWarnings("unchecked") T... vals) {
        if (ValidationUtils.isEmpty(vals)) return vals;
        Set<T> set = CollectionUtils.loadSortedSet(vals);
        return set.toArray(vals);
    }
    
    /**
     * 集合唯一化
     * @param cols
     * @return
     */
    public static <T> Set<T> unique(Collection<T> cols){
        return CollectionUtils.loadSortedSet(cols);
    }

    /**
     * 批量把字符串转为Integer
     * 
     * @param vals
     * @return
     */
    public static Integer[] toInts(String... vals) {
        if(vals == null) return null;
        return toInts(Arrays.asList(vals)).toArray(new Integer[0]);
    }
    
    /**
     * 批量把字符串转为Integer
     * 
     * @param vals
     * @return
     */
    public static Collection<Integer> toInts(Collection<?> vals) {
        return parseNumbers(vals,Integer::parseInt);
    }

    /**
     * 批量把字符串转为Long
     * 
     * @param vals
     * @return
     */
    public static Long[] toLongs(String... vals) {
        if(vals == null) return null;
        return toLongs(Arrays.asList(vals)).toArray(new Long[0]);
    }
    
    /**
     * 批量把字符串转为Long
     * 
     * @param vals
     * @return
     */
    public static Collection<Long> toLongs(Collection<?> vals) {
        return parseNumbers(vals,Long::parseLong);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> parseNumbers(Collection<?> vals,Function<String,T> func){
        if (vals == null || vals.isEmpty()) return null;
        ArrayList<T> results = new ArrayList<T>();
        String str;
        for (Object val : vals) {
            str=  DataUtils.toString(val);
            if (StringUtils.isNumeric(str)) results.add(func.apply(str));
        }
        return (Collection<T>) Arrays.asList(results.toArray());
    }
    
    public static Short toShort(Object obj) {
        return toShort(obj,null);
    }
    
    /**
     * 把非整型对象转换为短整型数字
     * 
     * @param obj
     * @param defaultVal
     * @return
     */
    public static Short toShort(Object obj, Short defaultVal) {
        return parseToNumber(obj,defaultVal, Short.class, BigDecimal::shortValue);
    }
    
    public static Integer toInt(Object obj) {
        return toInt(obj,null);
    }
    
    /**
     * 把非整型对象转换为整型数字
     * 
     * @param obj
     * @param defaultVal
     * @return
     */
    public static Integer toInt(Object obj, Integer defaultVal) {
        return parseToNumber(obj,defaultVal, Integer.class, BigDecimal::intValue);
    }
    
    /**
     * Parse to specific number type
     * @param obj
     * @param defaultVal
     * @param clazz
     * @param func
     * @return
     */
    public static <T> T parseToNumber(Object obj, T defaultVal, Class<T> clazz,Function<BigDecimal,T> func){
        T result = defaultVal;
        if (obj == null) return defaultVal;
        if (obj.getClass().isAssignableFrom(clazz)) return clazz.cast(obj);
        String objStr = obj.toString();
        try{
            result = func.apply(new BigDecimal(objStr));
        }catch(Exception ex){}
        return result;
    }

    /**
     * 把布尔类型转为0和1
     * 
     * @param bool
     * @return
     */
    public static byte toByte(Boolean bool) {
        return bool == null ? 0 : (bool ? (byte) 1 : 0);
    }

    /**
     * 把数字转换为布尔值
     * 
     * @param num
     *            数字
     * @return 非0为true ，其他为false
     */
    public static boolean toBoolean(Number num) {
        return num == null ? false : (num.intValue() != 0);
    }
    /**
     * 带有默认值版本的toBoolean方法
     * @param obj
     * @param defaultVal
     * @return
     */
    public static boolean toBoolean(Object obj,Boolean defaultVal){
        return obj == null ? defaultVal : 
            (obj instanceof Number ? toBoolean((Number)obj) : Boolean.valueOf(obj.toString()));
    }
    
    /**
     * Parse object value to Long type
     * @param obj
     * @return
     */
    public static Long toLong(Object obj) {
        return toLong(obj,null);
    }

    /**
     * @param string
     * @param defaultVal
     * @return
     */
    public static Long toLong(Object obj, Long defaultVal) {
        return parseToNumber(obj,defaultVal,Long.class, BigDecimal::longValue);
    }

    public static String toString(Object obj) {
        return toString(obj, StringUtils.EMPTY);
    }

    /**
     * 读取所有InputStream的值
     * 
     * @param is
     * @return
     */
    public static byte[] readAll(InputStream is) {
        if (is == null) return null;
        byte[] data = null;
        try {
            data = new byte[is.available()];
            is.read(data);
        }
        catch (Exception ex) {
            if (LOG.isWarnEnabled()) LOG.warn("[readAll] Read all bytes from input stream failed", ex);
        }
        return data;
    }

    /**
     * 读取所有InputStream的值
     * 
     * @param is
     * @param enc
     *            编码格式
     * @return
     */
    public static String readAsString(InputStream is, String enc) {
        if (is == null) return null;
        enc = enc == null ? HttpUtils.ENC_UTF8 : enc;
        String data = null;
        try {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            data = new String(bytes, enc);
        }
        catch (Exception ex) {
            if (LOG.isWarnEnabled()) LOG.warn("[readAll] Read all bytes from input stream failed", ex);
        }
        return data;
    }

    /**
     * 读取输入流内容并输出字符串
     * 
     * @return
     */
    public static String readAsString(InputStream is) {
        return readAsString(is, null);
    }

    /**
     * @param next
     * @return
     */
    public static String toString(Object next, String defaultVal) {
        return next == null ? defaultVal :
            (next.getClass().isArray() ? toString((Object[])next,ShenStringUtils.DELIMITER_COMMA) : next.toString());
    }
    
    /**
     * Make an array of objects to a string
     * @param objs
     * @return
     */
    public static String toString(Object[] objs){
        return toString(objs,ShenStringUtils.DELIMITER_COMMA,null);
    }
    
    /**
     * Make an array of objects to a string
     * @param objs
     * @param delimeter
     * @return
     */
    public static String toString(Object[] objs, String delimeter){
        return toString(objs,delimeter,null);
    }
    
    /**
     * make an array of string to a joined string with specific delimeter
     * @param objs
     * @param delimeter
     * @param defaultVal
     * @return
     */
    public static String toString(Object[] objs, String delimeter, String defaultVal){
        return ArrayUtils.isEmpty(objs) ? defaultVal : StringUtils.join(objs,delimeter);
    }

    /**
     * 返回首个非空值
     * 
     * @param vals
     *            值集合
     * @return
     */
    @SafeVarargs
    public static <T> T first(T... vals) {
        int length = vals.length;
        T val;
        for (int i = 0; i < length; i++) {
            val = vals[i];
            if ((val instanceof String) && !StringUtils.isEmpty((String) val)) return val;
            if (val != null) return val;
        }
        return null;
    }

    /**
     * 转换为double
     * @param param
     * @param defaultValue
     * @return
     */
    public static Double toDouble(Object obj, Double defaultValue) {
        Double result = defaultValue;
        if (obj == null) return defaultValue;
        if (obj instanceof Double) return (Double) obj;
        String objStr = obj.toString();
        try{result = new BigDecimal(objStr).doubleValue();}
        catch(Exception ex){}
        return result;
    }
    
    /**
     * Cast成类
     * @param o
     * @param clazz
     * @return
     */
    public static <T> T cast(Object o, Class<? extends T> clazz){
        if(o == null || clazz == null || !clazz.isAssignableFrom(o.getClass())) return null;
        return clazz.cast(o);
    }


    /**
     * Make a object to boolean
     * @param apply
     * @return
     */
    public static boolean toBoolean(Object obj) {
        if(obj == null) return false;
        String str = obj.toString();
        if(obj instanceof Number){
            //special handle for number object, 0 is false, and other s are true
            return !obj.equals(0);
        }else if(StringUtils.isNumeric(str)){
            //no matter how, just compare with 0
            return Long.parseLong(str) != 0;
        }else{
            return new Boolean(obj.toString());
        }
    }
    
    /**
     * 转换成BigDecimal类型
     * @param obj 对象值
     * @param defaultVal 默认值
     * @return
     */
    public static BigDecimal toDecimal(Object obj,BigDecimal defaultVal){
        if(obj == null) return defaultVal;
        try{
            return new BigDecimal(obj.toString());
        }catch(NumberFormatException ex){
            return defaultVal;
        }
        
    }
}
