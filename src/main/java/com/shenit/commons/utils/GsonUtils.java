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

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON压缩工具
 * @author jiangnan
 *
 */
public final class GsonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(GsonUtils.class);
    
	private static final Gson GSON;
	private static final Gson EXPOSE_ONLY_GSON;
	static{
		GSON = new GsonBuilder().
		                enableComplexMapKeySerialization().
		                excludeFieldsWithModifiers(Modifier.PRIVATE,Modifier.PROTECTED,Modifier.STATIC)
		                .create();
		EXPOSE_ONLY_GSON = new GsonBuilder().
                        enableComplexMapKeySerialization()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
	}
	/**
     * 把一个东西编译为json格式
     * @param source 要编译的对象
     * @param exposeAnnoFieldsOnly 是否只有Expose 标签的字段会被暴露
     * @return 返回JSON字符串
     */
    public static String toJson(Object source){
        return toJson(source,false);
    }
	/**
	 * 把一个东西编译为json格式
	 * @param source 要编译的对象
	 * @param exposeAnnoFieldsOnly 是否只有Expose 标签的字段会被暴露
	 * @return 返回JSON字符串
	 */
	public static String toJson(Object source,boolean exposeAnnoFieldsOnly){
		if(source == null) return null; 
		return exposeAnnoFieldsOnly ? EXPOSE_ONLY_GSON.toJson(source) : GSON.toJson(source);
	}
	/**
	 * 把字符串转换为指定类型.
	 * @param source json字符串
	 * @param type 指定的转换类型
	 * @return 返回转换后的结果
	 */
	public static <T> T fromJson(String source, Class<T> type){
	    try{
	        return GSON.fromJson(source, type);
	    }catch(Exception ex){
	        LOG.error("[fromJson] could not parse ["+source+"] due to exception",ex);
	    }
	    return null;
	}
	
	/**
	 * 把字符串转换为指定类型.
	 * @param source
	 * @param type
	 * @return
	 */
	public static <T>T fromJson(String source, Type type){
	    try{
            return GSON.fromJson(source, type);
        }catch(Exception ex){
            LOG.error("[fromJson] could not parse ["+source+"] due to exception",ex);
        }
        return null;
	}
}
