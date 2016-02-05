package com.shenit.commons.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map工具类集合.
 * @author jiangnan
 *
 */
public final class MapUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MapUtils.class);
    
    private static final String PARAM_NAME_PREFIX1="--";
    private static final String PARAM_NAME_PREFIX2="-";
    private static final String PARAM_NAME_PATTERN="--?";
    private static final String PARAM_ASSIGN_CHAR="=";
    
    /**
     * Filter maps' elements using the specific keys with orders
     * @param map Map object
     * @param filters Filters
     * @return
     */
    public static <K,V> Map<K,V> orderedFilter(Map<K,V> map, @SuppressWarnings("unchecked") K... filters){
        if(ValidationUtils.isEmpty(map)) return map;
        Map<K,V> result = new LinkedHashMap<>();
        for(K key : filters) result.put(key,map.get(key));
        return result;
    }
    /**
     * Check whether map contains a specific key
     * @param map
     * @param key
     * @return
     */
    public static <K,V> boolean contains(Map<K,V> map, K key){
        return key != null && map.get(key) != null;
    }
    
    /**
     * Remove keys within a specific map
     * @param map
     * @param keys
     * @return
     */
    public static <K,V> Map<K,V> remove(Map<K,V> map,@SuppressWarnings("unchecked")K... keys){
        if(ArrayUtils.isEmpty(keys)) return map;
        for(K key : keys) map.remove(key);
        return map;
    }

    /**
     * 把一个Map对象按键进行排序
     * @param map 数据图
     * @return 返回key排序后的Map
     */
    public static <K extends Comparable<? super K>,V> Map<K,V> sort(Map<K,V> map){
        if(map == null) return null;
        List<K> keyList = new ArrayList<K>(map.keySet());
        Collections.shuffle(keyList);
        Collections.sort(keyList);
        Map<K,V> result = new LinkedHashMap<K,V>();
        for(K key : keyList){
            result.put(key,map.get(key));
        }
        return result;
    }
    /**
     * 把参数字符串数组转换成Map对象
     * @param args
     * @return
     */
    public static Map<String,String> argsMap(String[] args){
        if(ValidationUtils.isEmpty(args)){
            return null;
        }
        Map<String,String> maps = new HashMap<String,String>();
        String paramName=null;
        String[] pair;
        for(String arg : args){
            if(!arg.startsWith(PARAM_NAME_PREFIX1) && !arg.startsWith(PARAM_NAME_PREFIX2)){
                maps.put(paramName, arg);
                continue;
            }
            paramName = arg.replaceFirst(PARAM_NAME_PATTERN, StringUtils.EMPTY);
            if(arg.indexOf(PARAM_ASSIGN_CHAR) < 0){
                maps.put(paramName, null);
                continue;
            }
            pair = arg.split(PARAM_ASSIGN_CHAR);
            maps.put(pair[0].replaceFirst(PARAM_NAME_PATTERN, StringUtils.EMPTY),pair[1]);
            paramName = null;
        }
        return maps;
    }
    

    
    /**
     * 
     * @param map
     * @param key
     * @param defaultVal
     * @return
     */
    public static <K> Object get(Map<K,?> map,K key, Object defaultVal,Function<Object,?> func){
        Object value =  contains(map,key) ? map.get(key) :   defaultVal;
        if(func != null) value = func.apply(value);
        return value;
    }
    

    /**
     * Get value according to key from a map object
     * @param map
     * @param key
     * @param defaultVal If no value found, return this value instead
     * @return Value store by key in the map, or defaultVal if not any
     */
    public static <K> Object get(Map<K,?> map,K key, Object defaultVal){
        return get(map,key,defaultVal,null);
    }
    
    /**
     * Divide a array into 2.
     * @param src Source array.
     * @return
     */
    public static <T> Map<T,T> unzip(T[] src){
        if(ValidationUtils.isEmpty(src)) return null;
        LinkedHashMap<T,T> result = new LinkedHashMap<T,T>();
        for(int i=0;i<src.length;i+=2){
            result.put(CollectionUtils.get(src,i), CollectionUtils.get(src,i+1));
        }
        return result;
    }
    
    /**
     * 把Map的指定值转换为整型
     * @param map
     * @param key
     * @param defaultVal
     * @return
     */
    public static <K,V> int getInt(Map<K,V> map,K key,int defaultVal){
        return contains(map,key) ? DataUtils.toInt(get(map,key), defaultVal) : defaultVal;
    }
    
    /**
     * Get long value from a map
     */
    public static <K,V> long getLong(Map<K,V> map,K key,long defaultVal){
        return contains(map,key) ? DataUtils.toLong(get(map,key), defaultVal) : defaultVal;
    }
    
    
    /**
     * 把一个Map对象的内容志杰串接起来.
     * @param map 数据图
     * @param pairJoiner 键值连接器，为空则使用空字串连接
     * @param entryJoiner 每个键值对之间的连接器，为空则使用空字串连接
     * @return 返回连接后的字符串，如果map为空则返回空字串
     */
    public static <K,V> String join(Map<K,V> map, String pairJoiner, String entryJoiner){
        if(map == null) return StringUtils.EMPTY;
        pairJoiner = StringUtils.defaultString(pairJoiner,StringUtils.EMPTY);
        entryJoiner = StringUtils.defaultString(entryJoiner, StringUtils.EMPTY);
        StringBuilder builder = new StringBuilder();
        String key;
        for(Iterator<K> keyIterator = map.keySet().iterator(); keyIterator.hasNext();){
            key = DataUtils.toString(keyIterator.next(),StringUtils.EMPTY);
            builder.append(key).append(pairJoiner).append(DataUtils.toString(map.get(key)));
            if(keyIterator.hasNext()) builder.append(entryJoiner);
        }
        return builder.toString();
    }
    
    /**
     * 以第一个参数为ID，第二个参数为值生成一个一一对应的Map
     * @param ids ID列表
     * @param vals 值列表
     * @param keepOrder 保护顺序
     * @param repeatWhenNoValue 如果值不足够是是否循环使用
     * @return
     */
    public static <K,V> Map<K, V> zip(K[] ids, V[] vals,boolean keepOrder, boolean loopValue,V defaultVal) {
        if(ValidationUtils.isEmpty(ids)){
            if (LOG.isInfoEnabled()) LOG.info("[map] No ids given");
            return null;
        }
        Map<K,V> map = keepOrder ? new LinkedHashMap<K,V>() : new HashMap<K,V>();
        int valLength = vals.length;
        for(int i=0;i<ids.length;i++){
            if(valLength > i){
                map.put(ids[i], vals[i]);
                continue;
            }
            map.put(ids[i],loopValue ? vals[i%valLength] : defaultVal);
        }
        return map;
    }
    
    /**
     * get collection size
     * @param cols
     * @return
     */
    public static int size(Map<?,?> cols){
        return ValidationUtils.isEmpty(cols) ? 0 : cols.size(); 
    }

    /**
     * Get value according to key from a map object
     * @param map
     * @param key
     * @param func Transformer to transform value before return.
     * @return
     */
    public static <K> Object get(Map<K,?> map,K key,Function<Object,?> func){
        return get(map,key,null,func);
    }
    /**
     * Get value according to key from a map object
     * @param map
     * @param key
     * @return
     */
    public static <K,V> Object get(Map<K,V> map,K key){
        return get(map,key,null);
    }
    

    /**
     * 以第一个参数为ID，第二个参数为值生成一个一一对应的Map.
     * @param ids ID列表
     * @param vals 值列表
     * @return
     */
    public static <K,V> Map<K, V> zip(K[] ids, V[] vals) {
        return zip(ids,vals,null);
    }
    
    /**
     * 以第一个参数为ID，第二个参数为值生成一个一一对应的Map.
     * @param ids ID列表
     * @param vals 值列表
     * @param defaultVal 默认值，如果vals小于ids的长度时补足
     * @return
     */
    public static <K,V> Map<K, V> zip(K[] ids, V[] vals, V defaultVal) {
        return zip(ids,vals,false,false, defaultVal);
    }
    /**
     * 检查一个hash是否为空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
