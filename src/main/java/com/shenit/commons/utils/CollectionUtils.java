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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 集合类工具方法.
 * @author jiangnan
 *
 */
public final class CollectionUtils {
	private static final Logger LOG = LoggerFactory.getLogger(CollectionUtils.class);
    
	/**
	 * Cast stream as list
	 * @param stream
	 * @return
	 */
	public static List<?> asList(Stream<?> stream){
	    return stream == null ? null : Arrays.asList(stream.toArray());
	}
	
	public static <V> V add(Collection<V> col,V val){
	    if(!ValidationUtils.isEmpty(col)) col.add(val);
	    return val;
	}
	/**
	 * 加载列表
	 * @param vals
	 * @return
	 */
	public static <T> List<T> loadList(T[] vals){
        ArrayList<T> list = new ArrayList<>();
        for(T v : vals) if(v != null) list.add(v);
        return list;
    }
	
	/**
	 * 返回有序系列
	 * @param vals
	 * @return
	 */
	public static <T> Set<T> loadSet(T[] vals){
	    HashSet<T> set = new HashSet<>();
	    for(T v : vals) if(v != null) set.add(v);
	    return set;
	}
	
	/**
	 * 返回有序系列
	 * @param vals
	 * @return
	 */
	public static <T> Set<T> loadSortedSet(T[] vals){
        LinkedHashSet<T> set = new LinkedHashSet<>();
        for(T v : vals) if(v != null) set.add(v);
        return set;
    }
	
	/**
     * 返回有序系列
     * @param vals
     * @return
     */
    public static <T> Set<T> loadSortedSet(Collection<T> vals){
        return ValidationUtils.isEmpty(vals) ? new LinkedHashSet<T>() : new LinkedHashSet<T>(vals);
    }
	
	
	/**
	 * 让整个集合对象转换为字符串数组并且进行拼接
	 * @param targets
	 * @param delimiter
	 * @return
	 */
	public static <T> String join(Collection<T> targets, String delimiter) {
		if(ValidationUtils.isEmpty(targets)) return StringUtils.EMPTY;
    	StringBuilder builder = new StringBuilder();
    	for(Iterator<T> iter = targets.iterator();iter.hasNext();){
    		builder.append(ShenStringUtils.str(iter.next()));
    		if(iter.hasNext()) builder.append(delimiter);
    	}
    	return builder.toString();
	}
	
	
	/**
	 * 让整个字符串数组进行拼接
	 * @param targets
	 * @param string
	 * @return
	 */
    public static <T> String join(T[] targets, String delimiter) {
    	if(ValidationUtils.isEmpty(targets)) return StringUtils.EMPTY;
    	StringBuilder builder = new StringBuilder();
    	for(T target : targets){
    		builder.append(target).append(delimiter);
    	}
    	//移除最后一个
    	if(builder.indexOf(delimiter) > 0) builder.deleteCharAt(builder.lastIndexOf(delimiter));
	    return builder.toString();
    }
    
    /**
     * Safe get method to prevent index out of bounds
     * @param vals
     * @param idx
     * @return
     */
    public static <T> T get(T[] vals,int idx){
        if(idx < 0) idx += vals.length;
        if(ValidationUtils.isEmpty(vals) || vals.length < idx || idx < 0) return null;
        return idx >= vals.length ? null : vals[idx];
    }
    
    /**
     * Safe get method to prevent index out of bounds
     * @param vals
     * @param idx
     * @return
     */
    public static <T> T get(Collection<T> vals,int idx){
        if(ValidationUtils.isEmpty(vals) || Math.abs(idx) >= vals.size()) return null;
        if(idx < 0) idx += vals.size();
        List<T> list = new ArrayList<>(Collections.unmodifiableCollection(vals));
        return list.get(idx);
    }
    
    /**
     * get collection size
     * @param cols
     * @return
     */
    public static int size(Collection<?> cols){
        return ValidationUtils.isEmpty(cols) ? 0 : cols.size(); 
    }
    
    
    /**
     * Get last element of a collection
     * @param cols
     * @return
     */
    public static <T> T last(Collection<T> cols){
        if(cols == null) return null;
        return cols.stream().parallel().skip(cols.size()-1).findFirst().get();
    }
    
    /**
     * Get last n element of a collection
     * @param cols
     * @param n
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> last(Collection<T> cols,int n){
        if(cols == null) return null;
        if(cols.size() <= n ) return cols; 
        return (Collection<T>) Arrays.asList(cols.stream().skip(cols.size()-n).toArray());
    }

    /**
     * Take the first n elements
     * @param cols
     * @param n
     * @return
     */
    public static <T> Collection<T> take(Collection<T> cols, int n, int direction){
        if(cols == null || n < 0) return null;
        ArrayList<T> result = new ArrayList<T>();
        ArrayList<T> colsArr = new ArrayList<T>(cols);
        int size = cols.size();
        for(int i=0; i < cols.size(); i++){
            if((i< n && direction >= 0) || (i>=  (size - n) && direction <0)) 
                result.add(colsArr.get(i));
        }
        return result;
    }
    
    /**
     * Drop n elements by direction
     * @param cols
     * @param n
     * @param direction Negative direction means drop from tail; Others means drop from head
     * @return Collection after drop
     */
    public static <T> Collection<T> drop(Collection<T> cols, int n, int direction){
        if(cols == null || n < 0) return null;
        ArrayList<T> result = new ArrayList<T>();
        ArrayList<T> colsArr = new ArrayList<T>(cols);
        int size = colsArr.size();
        for(int i=0;i< size;i++){
            if((i >= n && direction >= 0) || (i < size - n && direction < 0)) 
                result.add(colsArr.get(i));
        }
        return result;
    }
    
    /**
     * Drop the first n elements
     * @param cols
     * @param n
     * @return
     */
    public static <T> Collection<T> dropFirst(Collection<T> cols, int n){
        return drop(cols,n,0);
    }
    
    /**
     * Drop the last n elements
     * @param cols
     * @param n
     * @return
     */
    public static <T> Collection<T> dropLast(Collection<T> cols, int n){
        return drop(cols,n,-1);
    }
    
    /**
     *  Get the first item.
     * @param cols
     * @return
     */
    public static <T> T first(Collection<T> cols){
        return get(cols,0);
    }

    /**
     * @param streamNames
     * @param i
     * @param defval
     * @return
     */
    public static String get(String[] streamNames, int i, String defval) {
        String result =  get(streamNames, i);
        return result == null ? defval : result;
    }
    
    /**
     * Found out the item that col1 contains but col2 not contains.
     * @param col1 Collection 1
     * @param col2 Collection 2
     * @return The items only in col1 but not col2
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> diff(Collection<T> col1, Collection<T> col2){
        if(ValidationUtils.isEmpty(col1)) return col2;
        else if(ValidationUtils.isEmpty(col2)) return col1;
        try {
            Collection<T> diff = col1.getClass().newInstance();
            col1.stream().forEach((item1) -> {
                if(col2.parallelStream()
                		.noneMatch((item2) -> ValidationUtils.eq(item1, item2))) {
                	diff.add(item1); 
                }
            });
            return diff;
        }
        catch (InstantiationException | IllegalAccessException e) {
            LOG.warn("[diff] Could not create instance for {} with empty parameter constructor", col1.getClass());
        }
        return null;
    }

    /**
     * Check whether a value is in a collection
     * @param values
     * @param value
     * @return
     */
    public static <T> boolean contains(Collection<T> values, T value) {
        return !ValidationUtils.isEmpty(values) && values.contains(value);
    }

    /**
     * Check whether a collection is empty
     * @param records
     * @return
     */
    public static boolean isEmpty(Collection<?> records) {
        return records == null || size(records) < 1;
    }
    
}
