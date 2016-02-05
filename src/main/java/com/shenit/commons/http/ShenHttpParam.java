package com.shenit.commons.http;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shenit.commons.utils.ArrayUtils;
import com.shenit.commons.utils.DataUtils;
import com.shenit.commons.utils.ShenStringUtils;

/**
 * Http 参数 封装类.
 * @author jiangnan
 *
 */
public class ShenHttpParam extends LinkedHashMap<String,List<Object>>{
    private static final long serialVersionUID = 8036452208953720373L;
    private static final Logger LOG = LoggerFactory.getLogger(ShenHttpParam.class);
    
    public ShenHttpParam(Object... keysAndVals){
        addAll(keysAndVals);
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
        if(ArrayUtils.isEmpty(vals)){
            LOG.warn("[add] no value");
            return this;
        }
        if(!containsKey(key)) put(key,new ArrayList<>());
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
}