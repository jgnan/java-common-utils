package com.shenit.commons.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;
import com.shenit.commons.http.ShenHttpMethod;
import com.shenit.commons.http.ShenHttpParam;

/**
 * A new verion of http utils. Using URLConnection.
 * 
 * @author jiangnan
 *
 */
public class URLUtils {
    private static final Logger LOG = LoggerFactory.getLogger(URLUtils.class);
    public static final String COOKIE_PATTERN = "%s=%s; ";
    
    /**
     * Open a connection with the specific location and method
     * @param location Location to open.
     * @param method Http ethod
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnection(String location,ShenHttpMethod method){
        return openConnection(location,method,null);
    }
    /**
     * Open a location with get method. 
     * @param location
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnection(String location){
        return openConnection(location,ShenHttpMethod.Get,null);
    }
    /**
     * Http url connection
     * @param url
     * @param proxy
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnection(String location,Proxy proxy){
        return openConnection(location,ShenHttpMethod.Get,proxy);
    }
    
    /**
     * Open a url connection with location, method and proxy
     * @param location Location to open
     * @param method Http method
     * @param proxy Proxy to use
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnection(String location,ShenHttpMethod method, Proxy proxy){
        return openConnection(url(location),method,proxy);
    }
    
    /**
     * Http url connection
     * @param url
     * @param proxy
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnection(URL url, ShenHttpMethod method , Proxy proxy){
        return openConnection(url, method, null,null,proxy,0,0);
    }
    
    /**
     * Execute a url using http url connection.
     * @param location
     * @param headers
     * @param cookies
     * @param proxy
     * @param connectTimeout
     * @param socketTimeout
     * @return
     */
    public static HttpURLConnection openConnection(String url,ShenHttpMethod method, 
                    ShenHttpParam headers,Cookie[] cookies,Proxy proxy,int connectTimeout, int readTimeout) {
        return openConnection(url(url), method, headers,cookies,proxy, connectTimeout, readTimeout);
    }
    /**
     * Full version open connection method
     * @param url URL to open
     * @param method Http method
     * @param headers Http header fields
     * @param cookies Http cookies
     * @param proxy Proxy to use
     * @param connectTimeout Connecion timeout
     * @param readTimeout Read timeout
     * @return
     */
    public static HttpURLConnection openConnection(URL url,ShenHttpMethod method, 
                        ShenHttpParam headers,Cookie[] cookies,Proxy proxy,int connectTimeout, int readTimeout) {
        if(method == null) method = ShenHttpMethod.Get;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoInput(true);
            conn.setRequestMethod(method.code);
            conn.setInstanceFollowRedirects(true); //follow redirect by default
            writerHeaders(conn,headers);
            writerCookies(conn,cookies);
            return conn;
        }
        catch (IOException e) {
            LOG.warn("[execute] Exception throws when calling url -> {}", url, e);
        }
        return conn;
    }
    
    /**
     * Write cookie information to connection
     * @param conn
     * @param cookies
     */
    public static void writerCookies(HttpURLConnection conn, Cookie[] cookies) {
        if(ArrayUtils.isEmpty(cookies)){
            LOG.warn("[writerCookies] No cookie input");
            return;
        }
        String[] cookiesStr = Arrays.stream(cookies)
            .map(cookie -> String.format(COOKIE_PATTERN,cookie.getName() ,cookie.getValue()))
            .toArray(String[]::new);
        String result = StringUtils.join(cookiesStr);
        if(StringUtils.isNoneBlank(result)) result = result.replaceAll("; $", StringUtils.EMPTY);   //replace the last semicolon
        conn.setRequestProperty(HttpHeaders.COOKIE, result);
    }
    
    /**
     * Writer headers to url connection
     * @param conn An URLConnection instance
     * @param headers Headers to writer
     * @return
     */
    public static URLConnection writerHeaders(URLConnection conn, ShenHttpParam headers) {
        if(conn == null){
            LOG.warn("[writerHeaders] No connection input!");
            return conn;
        }
        if(MapUtils.isEmpty(headers)){
            if (LOG.isInfoEnabled()) LOG.info("[writerHeaders] No headers input");
            return conn;
        }
        conn.setDoOutput(true);
        for(String key : headers.keySet()){
            //XXX Maybe this would accept malicious codes
            conn.setRequestProperty(key, headers.getParam(key));
        }
        return conn;
    }
    
    /**
     * Wrap a location to url
     * @param location
     * @return
     */
    public static URL url(String location){
        if(StringUtils.isEmpty(location)) return null;
        try {
            return new URL(location);
        }
        catch (MalformedURLException e) {
            LOG.warn("[url] Could not load url to location -> {}", location);
        }
        return null;
    }
    
    /**
     * Write body to connection.
     * @param conn A HttpUrlConnection
     * @param params Body params
     * @return
     */
    public static HttpURLConnection formData(HttpURLConnection conn, Map<String, List<Object>> form) {
        return null;
    }
}
