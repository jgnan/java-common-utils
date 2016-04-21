package com.shenit.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;

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
     * Open a location's url connection instance using GET method
     * @param loc Location to visit
     * @return
     */
    public static HttpURLConnection getConnection(String loc){
        return getConnection(loc,null,null);
    }
    /**
     * Open a location's url connection instance using GET method
     * @param loc Location to visit
     * @param headers Headers
     * @param proxy Proxy to use
     * @return
     */
    public static HttpURLConnection getConnection(String loc, ShenHttpParam headers,Proxy proxy){
        return openConnection(loc, ShenHttpMethod.Get,headers, proxy);
    }
    
    /**
     * Open a connection with the specific location and method
     * @param location Location to open.
     * @param method Http ethod
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnection(String location,ShenHttpMethod method){
        return openConnection(location,method,null,null);
    }
    /**
     * Open a location with get method. 
     * @param location
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openConnection(String location){
        return openConnection(location,ShenHttpMethod.Get,null,null);
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
     * Open connection with proxy and connection timeout.
     * @param location Location
     * @param proxy Proxy to use
     * @param connectTimeout Connect time out to the website
     * @param socketTimeout Socket timeout for proxy
     * @return
     */
    public static HttpURLConnection openConnection(String location,ShenHttpMethod method,Proxy proxy, int connectTimeout, int socketTimeout){
        return openConnection(url(location),method,null,null, proxy,connectTimeout, socketTimeout);
    }
    
    /**
     * Open connction with http method and headers
     * @param location Location to open
     * @param method Http method
     * @param headers Headers for use
     * @return
     */
    public static HttpURLConnection openConnection(String location,ShenHttpMethod method, ShenHttpParam headers){
        return openConnection(location, method, headers,null,null,0,0);
    }
    
    
    /**
     * Open connction with http method and headers
     * @param location Location to open
     * @param method Http method
     * @param headers Headers for use
     * @param proxy Proxy to use
     * @return
     */
    public static HttpURLConnection openConnection(String location,ShenHttpMethod method, ShenHttpParam headers,Proxy proxy){
        return openConnection(location, method, headers,null,proxy,0,0);
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
    public static void writerCookies(HttpURLConnection conn, Cookie... cookies) {
        if(ArrayUtils.isEmpty(cookies)){
            if(LOG.isDebugEnabled()) LOG.debug("[writerCookies] No cookie input");
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
            if (LOG.isDebugEnabled()) LOG.debug("[writerHeaders] No headers input");
            return conn;
        }
//        conn.setDoOutput(true);
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
     * Encode url with utf8
     * @param string
     * @return
     */
    public static String encode(String string){
        if(StringUtils.isEmpty(string)) return null;
        try {
            return URLEncoder.encode(string,HttpUtils.ENC_UTF8);
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn("[encode] Could not encode url -> {}", string);
        }
        return null;
    }
    
    /**
     * Write body to connection.
     * @param conn A HttpUrlConnection
     * @param params Body params
     * @return
     */
    public static HttpURLConnection formData(HttpURLConnection conn, ShenHttpParam form) {
        if(MapUtils.isEmpty(form)) {
            LOG.warn("[formData] No data input!");
            return conn;
        }
        conn.setDoOutput(true);
        IOStreamUtils.write(conn,form.toQuery());
        return conn;
    }
    public static OutputStream out(URLConnection conn){
        if(conn == null){
            LOG.warn("[out] No connection given");
            return null;
        }
        conn.setDoOutput(true);
        try {
            return conn.getOutputStream();
        }
        catch (IOException e) {
            LOG.warn("[out] Could not get output stream due to exception", e);
        }
        return null;
    }
    
    /**
     * Get input stream from a connection
     * @param conn
     * @return
     */
    public static InputStream in(URLConnection conn) {
        if(conn == null){
            LOG.warn("[in] No connection given");
            return null;
        }
        conn.setDoInput(true);
        try {
            return conn.getInputStream();
        }
        catch (IOException e) {
            LOG.warn("[in] Could not get input stream due to exception", e);
        }
        return null;
    }
}
