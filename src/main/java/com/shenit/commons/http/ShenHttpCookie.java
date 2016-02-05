package com.shenit.commons.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shenit.commons.utils.ArrayUtils;

/**
 * Http cookie wrapper.
 * @author jiangnan
 *
 */
public class ShenHttpCookie {
    private static final String COOKIE_ATTR_DELIMITERS = "=|; ";
    private Map<String,Object> attributes;
    public String key;
    public String value;
    public ShenHttpCookie(){
        this(null,null);
    }
    
    public ShenHttpCookie(String key, String value){
        this.key = key;
        this.value = value;
        attributes = new HashMap<>();
    }
    
    public Map<String,Object> attributes(){
        return attributes;
    }
    
    /**
     * Parse a cookie.
     * @param cookieStr
     * @return
     */
    public static ShenHttpCookie parse(String cookieStr){
        if(StringUtils.isEmpty(cookieStr)) return null;
        String[] keyVals = cookieStr.split(COOKIE_ATTR_DELIMITERS);
        if(ArrayUtils.isEmpty(keyVals)) return null;
        String key = ArrayUtils.get(keyVals, 0);
        String val = ArrayUtils.get(keyVals,1);
        ShenHttpCookie cookie = new ShenHttpCookie(key,val);
        String attribute;
        String attrValue;
        for(int i=2;i<keyVals.length;i+=2){
            attribute = StringUtils.trim(ArrayUtils.get(keyVals,i));
            attrValue = StringUtils.trimToEmpty(ArrayUtils.get(keyVals, i+1));
            cookie.attributes.put(attribute, attrValue);
        }
        return cookie;
    }
}
