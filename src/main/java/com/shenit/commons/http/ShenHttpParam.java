package com.shenit.commons.http;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shenit.commons.utils.ArrayUtils;
import com.shenit.commons.utils.DataUtils;
import com.shenit.commons.utils.GsonUtils;
import com.shenit.commons.utils.HttpUtils;
import com.shenit.commons.utils.ShenStringUtils;

/**
 * Http 参数 封装类.
 * @author jiangnan
 *
 */
public class ShenHttpParam extends TreeMap<String,List<Object>>{
    private static final long serialVersionUID = 8036452208953720373L;
    private static final Logger LOG = LoggerFactory.getLogger(ShenHttpParam.class);
    
    public ShenHttpParam(Object... keysAndVals){
        addAll(keysAndVals);
    }
    
    public ShenHttpParam(Map<String,Object> objs){
        for(String key : objs.keySet()){
            add(key, objs.get(key));
        }
    }
    
    public ShenHttpParam(HttpServletRequest req){
        if(req == null) return;
        for(Enumeration<String> names = req.getParameterNames();names.hasMoreElements();){
            String paramName = names.nextElement();
            add(paramName,req.getParameter(paramName));
        }
    }
    
    public ShenHttpParam(ShenHttpParam params){
    	if(params != null) putAll(params);
    }
    
    /**
     * Add all keys and vals to header
     * @param keysAndVals
     * @return
     */
    public ShenHttpParam addAll(Object... keysAndVals){
        if(ArrayUtils.isEmpty(keysAndVals)) return this;
        String key, val;
        List<Object> vals;
        for(int i=0;i<keysAndVals.length;i+=2){
            key =DataUtils.toString(ArrayUtils.get(keysAndVals,i));
            val = DataUtils.toString(ArrayUtils.get(keysAndVals,i+1,StringUtils.EMPTY));
            if(!containsKey(key)) put(key,new ArrayList<>());
            vals = get(key);
            vals.add(val);
        }
        return this;
    }
    
    /**
     * Add values to the specific key
     * @param key
     * @param vals
     * @return
     */
    public ShenHttpParam add(String key, Object... vals){
        if(StringUtils.isEmpty(key)){
            LOG.warn("[add] empty key");
            return this;
        }
        if(!containsKey(key)) put(key,new ArrayList<>());
        if(ArrayUtils.isEmpty(vals)){
            if(LOG.isInfoEnabled()) LOG.info("[add] no value to put, just add the key.");
            return this;
        }
        List<Object> valList = get(key);
        for(Object val : vals) valList.add(val);
        return this;
    }
    
    /**
     * Get header value
     * @param key
     * @return
     */
    public String getParam(String key){
        return getParam(key,null);
    }
    
    /**
     * Get all objects to a specific key.
     * @param key
     * @return
     */
    public Object[] getAll(String key){
        List<Object> result =get(key);
        return result == null ? null : result.toArray();
    }
    
    /**
     * Get header value, joined as a string with specific delimiter
     * @param key
     * @param delimiter
     * @return
     */
    public String getParam(String key,String delimiter){
        List<Object> vals = get(key);
        if(vals == null) return  StringUtils.EMPTY;
        if(StringUtils.isEmpty(delimiter)) delimiter = ShenStringUtils.SEMICOLON;
        String[] headerVals = vals.stream()
            .map(val -> DataUtils.toString(val)).toArray(String[]::new);
        return ArrayUtils.isEmpty(headerVals) ? StringUtils.EMPTY : StringUtils.join(headerVals, delimiter);
    }

    /**
     * To query string
     * @return
     */
    public String toQuery() {
        StringBuilder builder = new StringBuilder();
        for(String key : keySet()){
            Object[] vals = getAll(key);
            if(ArrayUtils.isEmpty(vals)){
                builder.append(key).append(HttpUtils.EQ);
                builder.append(HttpUtils.AMP);
                continue;
            }
            for(Object val : vals){
                builder.append(key).append(HttpUtils.EQ).append(DataUtils.toString(val)).append(HttpUtils.AMP);
            }
        }
        //remove last & character
        if(builder.length() > 0) builder = builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    /**
     * Get as Integer
     * @param key
     * @return
     */
    public Integer getInteger(String key) {
        return DataUtils.toInt(getParam(key));
    }

    /**
     * Get Json object.
     * @param key
     * @param clazz
     * @return
     */
    public <T> T getJson(String key, Class<T> clazz) {
        return GsonUtils.fromJson(getParam(key), clazz);
    }
    /**
     * Get by type.
     * @param key
     * @param type
     * @return
     */
    public <T> T getJson(String key, Type type) {
        return GsonUtils.fromJson(getParam(key), type);
    }
}
