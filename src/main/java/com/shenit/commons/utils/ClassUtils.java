package com.shenit.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class工具类
 * @author folo 2015年4月21日下午2:15:57
 */
public class ClassUtils {
	
	/**
	 * javaBean转换为List<String>
	 * @author Folo 2014年10月20日
	 */
	public static <T> List<String[]> beanToArray(T t){
		Class<?> c = t.getClass();
		Field[] fields = c.getDeclaredFields();
		List<String[]> params = new ArrayList<String[]>();
		for (int i = 0; i < fields.length; i++) {
			String name = fields[i].getName();
			params.add(new String[]{name, getObjValue(t, name)});
		}
		return params;
	}
	
	public static <T> Map<String, String> beanToMap(T t){
	    Class<?> c = t.getClass();
        Field[] fields = c.getDeclaredFields();
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < fields.length; i++) {
            String name = fields[i].getName();
            params.put(name, getObjValue(t, name));
        }
        return params;
	}
	
	public static <T> String getObjValue(T t, String name){
		String getMName = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
		Method method = getMethod(t.getClass().getDeclaredMethods(), getMName);
		String objValue = null;
		try {
			Object obj = method.invoke(t);
			if(obj == null){}
			else if(obj instanceof String || obj instanceof Integer || obj instanceof Float
					|| obj instanceof Double){
				objValue = (String) obj;// 基础类型 直接赋值
			}else{
				objValue = GsonUtils.toJson(obj);// 特殊类型 转换为json
			}
		} catch (Exception e) { e.printStackTrace(); }
		
		return objValue;
	}
	
	public static Method getMethod(Method[] methods, String getMName){
		for (Method method : methods) {
			String mName = method.getName();
			if(mName.equals(getMName)) return method;
		}
		return null;
	}
}
