package com.shenit.commons.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;
import com.shenit.commons.utils.ArrayUtils;
import com.shenit.commons.utils.DataUtils;
import com.shenit.commons.utils.MapUtils;
import com.shenit.commons.utils.ShenStringUtils;

/**
 * Http response object containing HTTP response messages.
 * @author jiangnan
 *
 */
public class ShenHttpResponse extends HashMap<String, List<String>>{
    private static final long serialVersionUID = -8906186106701652416L;
    private static final Logger LOG = LoggerFactory.getLogger(ShenHttpResponse.class);
    public Map<String,ShenHttpCookie> cookies;
    public int code= 0;
    
    public ShenHttpResponse(){
    }
    
    public ShenHttpResponse(HttpURLConnection conn){
        if(conn == null) {
            if(LOG.isDebugEnabled())LOG.debug("[ShenitHttpResponse] No connection");
            return;
        }
        try {
            code = conn.getResponseCode();
            putAll(conn.getHeaderFields());
            if(containsKey(HttpHeaders.SET_COOKIE)) loadCookies();
            
        }
        catch (IOException e) {
            LOG.warn("[ShenitHttpResponse] Error getting connection values",e);
        }
    }

    /*
     * load cookies
     */
    private void loadCookies() {
        String[] vals = vals(HttpHeaders.SET_COOKIE);
        if(ArrayUtils.isEmpty(vals)) return;
        cookies = new HashMap<>();
        for(String val : vals){
            ShenHttpCookie cookie = ShenHttpCookie.parse(val);
            if(cookie != null) cookies.put(cookie.key, cookie);
        }
    }
    
    /**
     * Get a cookie
     * @param key
     * @return
     */
    public ShenHttpCookie getCookie(String key){
        return (ShenHttpCookie) MapUtils.get(cookies, key);
    }


    /**
     * Get values from response.
     * @param key
     * @return
     */
    public String[] vals(String key){
        List<String> vals = (List<String>) get(key);
        return vals == null ? null : vals.toArray(new String[0]);
    }
    
    /**
     * Get a header value
     * @param key
     * @return
     */
    public String val(String key){
        return val(key,StringUtils.EMPTY);
    }
    
    /**
     * Get a header value, if multiple values return will be join as a single string with delimiter ';'
     * @param key
     * @param defaultVal
     * @return
     */
    public String val(String key, String defaultVal){
        String[] vals = vals(key);
        return vals == null ? StringUtils.EMPTY : StringUtils.join(vals,ShenStringUtils.SEMICOLON);
    }
    
    /**
     * Get header value as integer type
     * @param key
     * @param defaultVal
     * @return
     */
    public Integer getInt(String key,Integer defaultVal){
        return DataUtils.toInt(val(key),defaultVal);
    }
    
    
    /**
     * Get header value as long
     * @param key
     * @param defaultVal
     * @return
     */
    public Long getLong(String key, Long defaultVal){
        return DataUtils.toLong(val(key),defaultVal);
    }
}