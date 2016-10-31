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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;
import com.shenit.commons.mvc.model.JsonSerializable;

public final class HttpUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
    private static final String DEFAULT_TEST_PROXY_URL = "http://www.baidu.com";

    public static final String JSONP_CALLBACK = "callback";
    public static final String ENC_UTF8 = "utf-8";
    // 通过nginx传过来的真实用户IP
    public static final String HEADER_REAL_IP = "X-Real-IP";
    // 通过nginx传过来的真实用户IP
    public static final String HEADER_FORWARDED_FOR = "X-Forwarded-For";
    // 用户访问的客户端信息
    public static final String HEADER_USER_AGENT = "User-Agent";

    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    public static final String QUERY_CHAR = "?";
    public static final String HASH = "#";
    public static final String AMP = "&";
    public static final String EQ = "=";
    public static final String SLASH = "/";

    public static final int DEFUALT_CONNECTION_TIMEOUT = 30000;
    public static final int DEFAULT_SO_TIMEOUT = 30000;

    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    public static final String URL_HTTP_PREFIX = "http://";
    public static final String URL_SSL_PREFIX = "https://";

    public static final MediaType APPLICATION_JSON = MediaType.create("application", "json");
    public static final MediaType PLAIN_TEXT = MediaType.create("plain", "text");
    public static final ContentType CONTENT_TYPE_PLAIN_TEXT = ContentType.create("plain/text", ENC_UTF8);
    public static final String STR_CONTENT_TYPE_FILE = ContentType.create("application/octet-stream", ENC_UTF8).toString();
    public static final String STR_CONTENT_TYPE_JSON = APPLICATION_JSON.toString();

    private static final String CALLBACK_PATTERN = "%s(%s);";

    public static final String HASH_CHAR = "#";

    private static final Object SECRET_STRING = "******";
    private static final String PASS_PATTERN = "pass(?:word)?";

    public static enum Encoding {
        DEFAULT("US-ASCII"),
        UTF8("utf-8"),
        UTF16("utf-16");
        String code;

        private Encoding(String name) {
            this.code = name;
        }

        public String getCode() {
            return code;
        }
    }
    
    /**
     * 测试代理类
     * @param proxy
     * @param address
     * @return
     */
    public static long testProxy(Proxy proxy,String address,int times){
        try {
            return testProxy(proxy,new URL(address),0,0,times);
        }
        catch (MalformedURLException e) {
            LOG.warn("[testProxy] Could not parse url -> {}", address,e);
        }
        return -1;
    }
    
    /**
     * 测试代理类
     * @param proxy
     * @param address
     * @param cTimeout 连接超时时间限制
     * @param rTimeout 读取超时时间限制
     * @return
     */
    public static long testProxy(Proxy proxy,String address,int cTimeout, int rTimeout,int testTimes){
        try {
            return testProxy(proxy,new URL(address),cTimeout,rTimeout,testTimes);
        }
        catch (MalformedURLException e) {
            LOG.warn("[testProxy] Could not parse url -> {}", address,e);
        }
        return -1;
    }

    /**
     * 获取用户的真实IP
     * 
     * @param req
     * @return
     */
    public static String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader(HEADER_REAL_IP);
        if (StringUtils.isBlank(ip)) {
            ip = req.getHeader(HEADER_FORWARDED_FOR);
        }
        if (StringUtils.isBlank(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 通过Get方法获取结果字符串
     * 
     * @param url
     *            地址
     * @param limit
     *            响应内容显示长度限制
     * @return
     */
    public static String getAsString(String url) {
        HttpGet request = new HttpGet(url);
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CloseableHttpClient client = clientBuilder.build();
        LOG.info("[getAsString] execute url >> {}", url);
        try {
            CloseableHttpResponse resp = client.execute(request);
            int status = resp.getStatusLine().getStatusCode();
            if (status == 200) {
                String result = EntityUtils
                                .toString(resp.getEntity(), ENC_UTF8);
                LOG.info("[getAsString] execute result for {} >> {}", url,
                                result);
                return StringUtils.trim(result);
            } else {
                LOG.error("execute with incorrect status code >> {}", status);
            }
        }
        catch (IOException e) {
            LOG.error("execute with exception >> {}", e.getMessage());
        }
        return null;
    }

    /**
     * 对所有参数进行编码，然后返回query string
     * 
     * @param params
     * @return
     */
    public static String encodeParams(Map<String, String> params) {
        return encodeParams(params, ENC_UTF8);
    }

    /**
     * 对所有参数进行编码
     * 
     * @param params
     * @param enc
     *            编码格式
     * @return
     */
    public static String encodeParams(Map<String, String> params, String enc) {
        if (params == null) return null;
        // copy one
        StringBuilder builder = new StringBuilder();
        String key;
        for (Iterator<String> keyIter = params.keySet().iterator(); keyIter
                        .hasNext();) {
            key = keyIter.next();
            builder.append(key)
                            .append(EQ)
                            .append(StringUtils.isEmpty(enc) ? params.get(key)
                                            : encodeUrl(params.get(key)));
            if (keyIter.hasNext()) builder.append(AMP);
        }
        return builder.toString();
    }

    /**
     * Request url result with get method
     * 
     * @param url
     *            URL
     * @return
     */
    public static String execute(HttpRequestBase request, int cTimeout,
                    int sotimeout) {
        return execute(request, null, cTimeout, sotimeout);
    }
    
    /**
     * Execute url without proxy
     * @param request
     * @param context
     * @param cTimeout
     * @param soTimeout
     * @return
     */
    public static String execute(HttpRequestBase request, HttpContext context, int cTimeout,int soTimeout) {
        return execute(request,context,null, cTimeout, soTimeout);
    }

    /**
     * Request url result with get method
     * 
     * @param url
     *            URL
     * @return
     */
    public static String execute(HttpRequestBase request, HttpContext context, HttpHost proxy,int cTimeout,int soTimeout) {
        RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(cTimeout)
                        .setConnectionRequestTimeout(soTimeout)
                        .build();
        request.setConfig(config);

        String result = null;
        CloseableHttpClient client = HttpClientBuilder.create().setProxy(proxy).build();
        try {
            if (LOG.isDebugEnabled()) LOG.debug(
                            "[execute] request -> \n>>>>>>>>>>>>>>>>\n{}\n<<<<<<<<<<<<<<<<\n",
                            dumpRequest(request));
            HttpResponse resp = client.execute(request, context);
            result = EntityUtils.toString(resp.getEntity(),
                            getContentEncoding(resp));
            if (LOG.isDebugEnabled()) LOG.debug(
                            "[execute] response -> \n>>>>>>>>>>>>>>>>\n{}\n<<<<<<<<<<<<<<<<\n",
                            result);
        }
        catch (ClientProtocolException e) {
            LOG.warn("[execute]Error when calling url >> " + request.getURI(),
                            e);
        }
        catch (IOException e) {
            LOG.warn(
                            "[execute]IOException when calling url >> "
                                            + request.getURI(), e);
        }
        return result;
    }

    /**
     * 执行方法
     * 
     * @param request
     * @return
     */
    public static String execute(HttpRequestBase request) {
        return execute(request, null, DEFUALT_CONNECTION_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    /**
     * 执行方法
     * 
     * @param request
     * @return
     */
    public static String execute(HttpRequestBase request, HttpContext context) {
        return execute(request, context, DEFUALT_CONNECTION_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    /**
     * Execute a url
     * @param request
     * @param context
     * @param proxy
     * @return
     */
    public static String execute(HttpRequestBase request, HttpContext context,HttpHost proxy) {
        return execute(request,context, proxy, DEFUALT_CONNECTION_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    /**
     * Encode url to specific character set
     * 
     * @param str
     * @param charset
     * @return
     */
    public static String encodeUrl(String str, String charset) {
        if (str == null) return null;
        charset = charset == null ? ENC_UTF8 : charset;
        String url = str;
        try {
            url = URLEncoder.encode(str, charset);
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn("[encodeUrl]Could not encode url. Error >>> {}",
                            e.getMessage());
        }
        return url;
    }

    /**
     * Encode url using utf-8
     * 
     * @param str
     * @return
     */
    public static String encodeUrl(String str) {
        return encodeUrl(str, ENC_UTF8);
    }

    public static HttpUriRequest multipartFormArrayList(HttpPost request,
                    ArrayList<Object> keyVals) {
        int length = keyVals.size();
        Object[] kvs = new Object[length];
        for (int i = 0; i < length; i++) {
            kvs[i] = keyVals.get(i);
        }
        return multipartForm(request, kvs);
    }

    public static HttpUriRequest multipartFormArrayLists(HttpPost request,
                    List<String[]> keyVals) {
        int length = keyVals.size();
        Object[] kvs = new Object[length * 2];
        for (int i = 0; i < length; i += 2) {
            String[] item = keyVals.get(i);
            kvs[i] = item[0];
            kvs[i + 1] = item[1];
        }
        return multipartForm(request, kvs);
    }

    /**
     * Create a url encoded form
     * 
     * @param request
     *            Request object
     * @param keyVals
     *            Key and value pairs, the even(begins with 0) position params
     *            are key and the odds are values
     * @return
     */
    public static HttpPost urlEncodedForm(HttpPost request, Object... keyVals) {
        return urlEncodedForm(request, Encoding.UTF8, keyVals);
    }

    /**
     * Create a url encoded form
     * 
     * @param request
     *            Request object
     * @param enc
     *            Request body encoding, default is utf8
     * @param keyVals
     *            Key and value pairs, the even(begins with 0) position params
     *            are key and the odds are values
     * @return
     */
    public static HttpPost urlEncodedForm(HttpPost request, Encoding enc,
                    Object... keyVals) {
        if (request == null || ValidationUtils.isEmpty(keyVals)) return request;
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (int i = 0; i < keyVals.length; i += 2) {
            pairs.add(new BasicNameValuePair(ShenStringUtils.str(keyVals[i]),
                            i + 1 < keyVals.length ? ShenStringUtils.str(keyVals[i + 1])
                                            : StringUtils.EMPTY));
        }
        try {
            request.setEntity(new UrlEncodedFormEntity(pairs, enc.code));
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn(
                            "[urlEncodedForm]Fill data to form entity failed. Caused by {}",
                            e.getMessage());
        }
        return request;
    }

    /**
     * Create a url encoded form using utf8 charset
     * 
     * @param request
     * @param keyVals
     * @return
     */
    public static <K, V> HttpPost urlEncodedFormByMap(HttpPost request,
                    Map<K, V> keyVals) {
        return urlEncodedFormByMap(request, Encoding.UTF8, keyVals);
    }

    /**
     * Create a url encoded form
     * 
     * @param request
     *            Request object
     * @param enc
     *            Request body encoding, default is utf8
     * @param keyVals
     *            Key and value pairs, the even(begins with 0) position params
     *            are key and the odds are values
     * @return
     */
    public static <K, V> HttpPost urlEncodedFormByMap(HttpPost request,
                    Encoding enc, Map<K, V> keyVals) {
        if (request == null || ValidationUtils.isEmpty(keyVals)) return request;
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Object key : keyVals.keySet()) {
            pairs.add(new BasicNameValuePair(ShenStringUtils.str(key),
                            ShenStringUtils.str(keyVals.get(key))));
        }
        try {
            if (LOG.isDebugEnabled()) LOG.debug("[urlEncodedFormByMap] enc:{} pairs:{}", new Object[] { enc.code, pairs });
            request.setEntity(new UrlEncodedFormEntity(pairs, enc.code));
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn(
                            "[urlEncodedForm]Fill data to form entity failed. Caused by {}",
                            e.getMessage());
        }
        return request;
    }

    /**
     * Create a multipart form
     * 
     * @param request
     *            Request object
     * @param keyVals
     *            Key and value pairs, the even(begins with 0) position params
     *            are key and the odds are values
     * @return
     */
    public static HttpUriRequest multipartForm(HttpPost request,
                    Object... keyVals) {
        if (request == null || ValidationUtils.isEmpty(keyVals)) return request;
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        boolean hasVal = false;

        String key;
        for (int i = 0; i < keyVals.length; i += 2) {
            key = ShenStringUtils.str(keyVals[i]);
            hasVal = i + 1 < keyVals.length;

            if (!hasVal || keyVals[i + 1] == null) {
                builder.addTextBody(key, StringUtils.EMPTY,CONTENT_TYPE_PLAIN_TEXT);
                break;
            }

            if (keyVals[i + 1].getClass().isAssignableFrom(File.class)) {
                builder.addPart(key, new FileBody((File) keyVals[i + 1]));
            } else {
                builder.addTextBody(key, keyVals[i + 1].toString(),
                                CONTENT_TYPE_PLAIN_TEXT);
            }
        }
        request.setEntity(builder.build());
        return request;
    }

    /**
     * Post url
     * @param url
     * @return
     */
    public static String post(String url) {
        return execute(new HttpPost(url));
    }

    /**
     * 
     * @param url
     * @param body
     *            编码为utf-8
     * @return
     */
    public static String post(String url, String body) {
        return post(url, body, DEFUALT_CONNECTION_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    /**
     * Send a post request
     * @param url URL to visit
     * @param body Body to the request
     * @param connTimeout Connection time out time
     * @param socketTimeout
     * @return
     */
    public static String post(String url, String body, int connTimeout, int socketTimeout) {
        try {
            return post(url, body.getBytes(Encoding.UTF8.code), null,connTimeout, socketTimeout);
        }
        catch (UnsupportedEncodingException e) {
            String err = String.format("[post] error url:%s, body:%s", url, body);
            LOG.error(err, e);
        }

        return null;
    }
    
    /**
     * Send a post request.
     * @param url URL to send
     * @param body Body to send
     * @param proxy HttpProxy to use in request
     * @return
     */
    public static String post(String url, String body,HttpHost proxy) {
        try {
            return post(url, body.getBytes(Encoding.UTF8.code), proxy,DEFUALT_CONNECTION_TIMEOUT, DEFAULT_SO_TIMEOUT);
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn("[post] Illegal encoding not support");    //which should not happen
        }
        return null;
    }
    
    /**
     * Send a post request.
     * @param url URL to send
     * @param body Body to send
     * @param proxy HttpProxy to use in request
     * @return
     */
    public static String post(String url, byte[] body,HttpHost proxy) {
        return post(url, body, proxy,DEFUALT_CONNECTION_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    /**
     * Send a post request.
     * @param url URL to send
     * @param body Body to send
     * @return
     */
    public static String post(String url, byte[] body) {
        return post(url, body, null,DEFUALT_CONNECTION_TIMEOUT, DEFAULT_SO_TIMEOUT);
    }

    /**
     * Send a post request
     * @param url URL to send
     * @param body Body to send
     * @param connectionTimeout Connection timeout
     * @param socketTimeout Socket timeout
     * @return
     */
    public static String post(String url, byte[] body,HttpHost proxy, int connectionTimeout, int socketTimeout) {
        HttpEntity entity = new ByteArrayEntity(body);
        HttpPost post = new HttpPost(url);
        post.setEntity(entity);
        return execute(post, null, proxy,connectionTimeout, socketTimeout);
    }

    /**
     * Execute a get request and return the string result
     * 
     * @param url
     * @return
     */
    public static String get(String url) {
        return execute(new HttpGet(url));
    }

    /**
     * Execute a get request and return the string result
     * 
     * @param url
     * @return
     */
    public static String get(String url, HttpContext context) {
        return execute(new HttpGet(url), context,null);
    }
    
    public static String get(String url, HttpContext context,HttpHost proxy) {
        return execute(new HttpGet(url), context, proxy);
    }
    /**
     * Get response content encoding value. With utf-8 as default value if not
     * exists
     * 
     * @param resp
     * @return
     */
    public static String getContentEncoding(HttpResponse resp) {
        return getContentEncoding(resp, ENC_UTF8);
    }

    /**
     * * Get response content encoding value. With <i>defaultEncoding</i> as
     * default value if not exists
     * 
     * @param resp
     * @param defaultEncoding
     * @return
     */
    public static String getContentEncoding(HttpResponse resp,
                    String defaultEncoding) {
        String encode = defaultEncoding;
        if (resp != null && resp.getEntity() != null
                        && resp.getEntity().getContentEncoding() != null) {
            encode = resp.getEntity().getContentEncoding().getValue();
        }
        return encode;
    }

    /**
     * Get client agent from http request.
     * 
     * @param req
     *            HttpServletRequest
     * @return
     */
    public static String getAgent(HttpServletRequest req) {
        return req.getHeader(HEADER_USER_AGENT);
    }

    /**
     * 批量写入一堆cookie
     * 
     * @param resp
     * @param copyToSession
     *            是否同时创建到session
     * @param cookies
     */
    public static void save(HttpServletRequest req, HttpServletResponse resp,
                    boolean copyToSession, Cookie... cookies) {
        HttpSession session = req.getSession(true);
        for (Cookie cookie : cookies) {
            resp.addCookie(cookie);
            if (copyToSession) session.setAttribute(cookie.getName(), cookie.getValue());
        }
    }

    /**
     * 批量写入一堆cookie
     * 
     * @param resp
     * @param copyToSession
     *            是否同时创建到session
     * @param cookies
     */
    public static void purge(HttpServletRequest req, HttpServletResponse resp,
                    boolean andSession, String... names) {

        if (andSession) purgeSessions(req, names);
        purgeCookies(req, resp, names);
    }

    /**
     * 清空若干个session的值，如果names不传值则清空所有session值
     * 
     * @param req
     * @param names
     */
    public static void purgeSessions(HttpServletRequest req, String... names) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            if (LOG.isDebugEnabled()) LOG.debug("[purgeSessions] No sessions to purge");
            return;
        }
        if (ValidationUtils.isEmpty(names)) {
            // 清理所有session属性
            Enumeration<String> namesEnum = session.getAttributeNames();
            for (; namesEnum.hasMoreElements();) {
                session.removeAttribute(namesEnum.nextElement());
            }
            return;
        }
        // 按照名称清理
        for (String name : names) {
            session.removeAttribute(name);
        }
    }

    /**
     * 清理一个指定的cookie值
     * 
     * @param req
     *            请求
     * @param name
     *            cookie名称
     */
    public static void purgeCookies(HttpServletRequest req, HttpServletResponse resp, String... names) {
        Set<String> nameSet = ValidationUtils.isEmpty(names) ? null :
                        new HashSet<String>(Arrays.asList(names));
        boolean removeAll = ValidationUtils.isEmpty(nameSet);
        for (Cookie cookie : req.getCookies()) {
            if (removeAll || nameSet.contains(cookie.getName())) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
                resp.addCookie(cookie);
                if (!removeAll) nameSet.remove(cookie.getName());;
            }
        }
    }

    /**
     * 尝试从请求、Session和Cookie中获取值
     * 
     * @param name
     *            名称
     * @param req
     * @return
     */
    public static Object getValue(String name, HttpServletRequest req) {
        return getValue(name, null, req);
    }

    /**
     * 尝试从请求、Session和Cookie中获取值
     * 
     * @param name
     *            名称
     * @param defaultVal
     *            默认值
     * @param req
     * @return
     */
    public static Object getValue(String name, Object defaultVal,
                    HttpServletRequest req) {
        // get from request first
        Object value = collapseValue(req.getParameterValues(name));
        if (value != null) return value;
        // get from session
        value = getSessionAttribute(name, null, req);
        if (value != null) return value;
        value = getCookieValue(name, null, req);
        if (value != null) return value;
        // nothing found
        return defaultVal;
    }

    /**
     * 获取Session值
     * 
     * @param name
     *            属性值名称
     * @param req
     * @return
     */
    public static Object getSession(String name, HttpServletRequest req) {
        return getSessionAttribute(name, null, req);
    }

    /**
     * 获取Session值
     * 
     * @param name
     *            名称
     * @param defaultVal
     *            默认值
     * @param req
     * @return
     */
    public static Object getSessionAttribute(String name, Object defaultVal,
                    HttpServletRequest req) {
        HttpSession session = req.getSession();
        return session != null && session.getAttribute(name) != null ? session
                        .getAttribute(name) : defaultVal;
    }

    /**
     * 从Cookie中找值
     * 
     * @param name
     *            名称
     * @param req
     *            请求
     * @return
     */
    public static String getCookie(String name, HttpServletRequest req) {
        return getCookieValue(name, null, req);
    }

    /**
     * 从Cookie中找值
     * 
     * @param name
     *            名称
     * @param defaultVal
     *            默认值
     * @param req
     *            请求
     * @return
     */

    public static String getCookieValue(String name, String defaultVal,
                    HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals(name)) return cookie.getValue();
            }
        }
        // nothing found
        return defaultVal;
    }

    /**
     * 快速创建cookie实例的精简版.
     * 
     * @param name
     *            Cookie名称
     * @param val
     *            值
     * @param expiry
     *            有效秒数
     * @return
     */
    public static Cookie cookie(String name, Object val, Integer expiry) {
        return cookie(name, val, expiry, null, null, true, false);
    }

    /**
     * 快速创建cookie实例
     * 
     * @param name
     *            Cookie名称
     * @param val
     *            值
     * @param expiry
     *            有效秒数
     * @param domain
     *            作用域
     * @param path
     *            作用路径
     * @param httpOnly
     *            是否只能是HTTP的cooie
     * @param secure
     *            是否SSL
     * @return
     */
    public static Cookie cookie(String name, Object val, Integer expiry,
                    String domain, String path, boolean httpOnly, boolean secure) {
        Cookie cookie = new Cookie(name, val == null ? null : val.toString());
        if (expiry != null) cookie.setMaxAge(expiry);
        if (!StringUtils.isEmpty(domain)) cookie.setDomain(domain);
        cookie.setSecure(secure);
        if (!StringUtils.isEmpty(path)) cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }

    /**
     * 把属性值数组组装成一个字符串
     * 
     * @param vals
     *            值
     * @return
     */
    public static String collapseValue(String[] vals) {
        if (ValidationUtils.isEmpty(vals)) return null;
        return StringUtils.join(vals, ShenStringUtils.DELIMITER_COMMA);
    }

    /**
     * Write contents into response' writer.
     * 
     * @param resp
     *            Response
     * @param content
     */
    public static void writeTo(HttpServletResponse resp, String content) {
        try {
            resp.getWriter().append(content);
        }
        catch (IOException e) {}
    }

    /**
     * 获取整型值
     * 
     * @param req
     *            请求
     * @param param
     *            参数名
     * @param defaultVal
     *            默认值
     * @return
     */
    public static Integer getIntParam(HttpServletRequest req, String param,
                    Integer defaultVal) {
        return DataUtils.toInt(req.getParameter(param), defaultVal);
    }

    /**
     * 解码URL
     * 
     * @param response
     * @return
     */
    public static String decodeUrl(String response) {
        return decodeUrl(response, ENC_UTF8);
    }

    /**
     * 解码URL
     * 
     * @param response
     * @return
     */
    public static String decodeUrl(String response, String enc) {
        try {
            return URLDecoder.decode(response, enc);
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn("[decodeUrl(String,String)] encode not supported", e);
        }
        return null;
    }

    /**
     * 把请求内容输出成字符串
     * 
     * @param req
     * @return
     */
    public static String dumpRequest(HttpUriRequest req) {
        if (req == null) return null;
        char column = ':', rtn = '\n', space = ' ';
        StringBuilder builder = new StringBuilder(req.getMethod());
        builder.append(space).append(req.getURI()).append(space)
                        .append(req.getProtocolVersion()).append(rtn).append(rtn);
        builder.append("HEADERS:\n");
        Header[] headers = req.getAllHeaders();
        int length = headers.length;
        for (int i = 0; i < length; i++) {
            builder.append(headers[i].getName()).append(column)
                            .append(headers[i].getValue()).append(rtn);
        }
        if (req instanceof HttpPost || req instanceof HttpPut) {
            builder.append(rtn);
            builder.append("BODY:\n");
            if (null != ((HttpPost) req).getEntity()) {
                BufferedReader reader = null;
                try {
                    InputStreamReader isReader = new InputStreamReader(
                                    ((HttpPost) req).getEntity().getContent());
                    reader = new BufferedReader(isReader);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                }
                catch (IllegalStateException | IOException e) {
                    if (LOG.isWarnEnabled()) LOG.warn(
                                    "[dumpRequest] Could not read request due to exception",
                                    e);
                }
                finally {
                    if (reader != null) try {
                        reader.close();
                    }
                    catch (IOException e) {
                        if (LOG.isWarnEnabled()) LOG.warn(
                                        "[dumpRequest] could not close reader due to exception",
                                        e);
                    }
                }
            }
        }
        return builder.toString();
    }

    /**
     * Dump out things from HttpServletRequest object
     * 
     * @param req
     * @return
     */
    public static String dumpRequest(HttpServletRequest req) {
        if (req == null) return null;
        char column = ':', rtn = '\n', space = ' ';
        StringBuilder builder = new StringBuilder(req.getMethod());
        builder.append(space).append(req.getRequestURL().toString()).append(space)
                        .append(req.getProtocol()).append(rtn);
        Enumeration<String> headers = req.getHeaderNames();
        builder.append("HEADERS:\n");
        String header;
        for (; headers.hasMoreElements();) {
            header = headers.nextElement();
            builder.append(header).append(column)
                            .append(req.getHeader(header)).append(rtn);
        }
        builder.append("COOKIES:\n");
        Cookie cookie;
        Cookie[] cookies = req.getCookies();
        if (!ValidationUtils.isEmpty(cookies)) {
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];
                builder.append(cookie.getName()).append(column)
                                .append(GsonUtils.toJson(cookie)).append(rtn);
            }
        }
        builder.append("BODY:\n");
        Map<String, String[]> params = req.getParameterMap();
        for (String name : params.keySet()) {
            builder.append(name).append(ShenStringUtils.DELIMITER_DOT);
            builder.append(name.matches(PASS_PATTERN) ?
                            params.get(SECRET_STRING) : params.get(name));
        }
        return builder.toString();

    }

    /**
     * 用于解释queryString
     * 
     * @author jiangnan
     * 
     */
    public static class QueryString {
        public Map<String, List<String>> params;
        public String query;

        public QueryString(String query) {
            this.query = query;
            params = parseQueryInternal(query);
        }

        public Integer getInteger(String param) {
            return getInteger(param, null);
        }

        public Integer getInteger(String param, Integer defaultVal) {
            return hasParam(param) ? DataUtils.toInt(params.get(param).get(0),
                            defaultVal) : defaultVal;
        }

        public String get(String param) {
            return get(param, null);
        }

        public String get(String param, String defaultVal) {
            return hasParam(param) ? params.get(param).get(0) : defaultVal;
        }

        public Long getLong(String param) {
            return getLong(param, null);
        }

        public Long getLong(String param, Long defaultVal) {
            return hasParam(param) ? DataUtils.toLong(params.get(param).get(0),
                            defaultVal) : defaultVal;
        }

        public String[] getArray(String param) {
            return hasParam(param) ? params.get(param).toArray(new String[0])
                            : null;
        }

        /**
         * @param param
         * @return
         */
        public boolean hasParam(String param) {
            List<String> vals = params.get(param);
            return !ValidationUtils.isEmpty(vals);
        }

        private Map<String, List<String>> parseQueryInternal(String query) {
            params = new HashMap<String, List<String>>();
            if (StringUtils.isEmpty(query)) return params;
            String[] pairs = query.split(AMP);
            String[] pairValue;
            for (String pair : pairs) {
                pairValue = pair.split(EQ);
                if (!params.containsKey(pairValue[0])) {
                    params.put(pairValue[0], new ArrayList<String>());
                }
                params.get(pairValue[0]).add(pairValue[1]);
            }
            return params;
        }
    }

    /**
     * Create a basic login context.
     * 
     * @param username
     * @param pass
     * @return
     */
    public static HttpContext basicLoginContext(String username, String pass) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, pass);
        provider.setCredentials(AuthScope.ANY, credentials);
        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(provider);
        return context;
    }

    /**
     * Get value from paramMap
     * 
     * @param params
     * @param name
     * @return
     */
    public static String getParam(Map<String, String[]> params, String name) {
        String result = null;
        if (params != null && params.containsKey(name)) {
            String[] vals = params.get(name);
            result = ValidationUtils.isEmpty(vals) ? null : CollectionUtils.join(vals, ShenStringUtils.DELIMITER_COMMA);
        }
        return result;
    }

    /**
     * Get parameter as long
     * 
     * @param req
     * @param name
     * @return
     */
    public static Long getAsLong(HttpServletRequest req, String name) {
        String param = req.getParameter(name);
        Long val = null;
        try {
            val = Long.parseLong(param);
        }
        catch (Exception ex) {}
        return val;
    }

    /**
     * 返回jsonp响应format
     * 
     * @param json
     * @param req
     * @return
     */
    public static String jsonp(JsonSerializable json, HttpServletRequest req) {
        return jsonp(json == null ? null : json.toJson(), req);
    }

    /**
     * 返回jsonp响应format
     * 
     * @param json
     * @param req
     * @return
     */
    public static String jsonp(String json, HttpServletRequest req) {
        String callback = req.getParameter(JSONP_CALLBACK);
        if (StringUtils.isEmpty(callback)) return json;
        return String.format(CALLBACK_PATTERN, callback, json);
    }

    /**
     * 获取参数Hash. 把默认的Map<String,String[]>
     * 转换为Map<String,String>类型，其中对于数组统一变成用,相连的字符串处理
     * 
     * @param req
     * @return
     */
    public static Map<String, String> getParamMap(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        Map<String, String> result = new HashMap<String, String>();
        Set<String> keys = params.keySet();
        for (String key : keys)
            result.put(key, getParam(params, key));
        return result;
    }

    /**
     * 把参数集合到URL中: joinParams("http://www.google.com", "test", "test"); //
     * http://www.google.com?test=test joinParams("http://www.google.com",
     * "test"); // http://www.google.com?test= joinParams(null, "test"); //
     * ?test= joinParams("http://www.google.com",
     * "test","test","test2","test2"); //
     * http://www.google.com?test=test&test2=test2
     * 
     * @param url
     *            - 前置地址，如果为空则只输出query字串
     * @param params
     *            要拼接的地址
     * @return
     */
    public static String joinParams(String url, String... params) {
        if (ValidationUtils.isEmpty(params)) return url;
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(url)) builder.append(url);
        boolean hasQuery = ShenStringUtils.has(url, QUERY_CHAR);
        for (int i = 0; i < params.length; i += 2) {
            builder.append(hasQuery ? AMP : QUERY_CHAR);
            if (!hasQuery) hasQuery = true;
            builder.append(params[i]).append(EQ).append(CollectionUtils.get(params, i + 1));
        }
        return builder.toString();
    }

    /**
     * 把参数集合到URL中.
     * 
     * @param url
     *            - 前置地址，如果为空则只输出query字串
     * @param params
     *            要拼接的地址
     * @return
     */
    public static String joinParams(String url, Map<String, ?> params) {
        if (ValidationUtils.isEmpty(params)) return url;
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotEmpty(url)) builder.append(url);
        boolean hasQuery = ShenStringUtils.has(url, QUERY_CHAR);
        Object val;
        for (Entry<String, ?> kv : params.entrySet()) {
            builder.append(hasQuery ? AMP : QUERY_CHAR);
            if (!hasQuery) hasQuery = true;
            val = kv.getValue();
            builder.append(kv.getKey()).append(EQ).append(DataUtils.isPrimative(val) ? val.toString() : GsonUtils.toJson(val));
        }
        return builder.toString();
    }

    /**
     * 变成字符串
     * 
     * @param uri
     * @param req
     * @return
     */
    public static String toUrl(String uri, HttpServletRequest req) {
        StringBuffer buffer = req.getRequestURL();
        boolean isSsl = buffer.indexOf(URL_SSL_PREFIX) > -1;
        String base = buffer.substring(0, buffer.indexOf(SLASH, URL_SSL_PREFIX.length() - (isSsl ? 0 : 1)));
        return StringUtils.isEmpty(uri) ? base : base + uri;
    }

    /**
     * 准备好下载文件的响应
     * 
     * @param name
     * @param resp
     */
    public static void downloadFile(String name, InputStream is, int bufferSize, HttpServletResponse resp) {
        if (is == null) return;
        try {
            byte[] buffer = new byte[bufferSize];
            resp.setContentType(STR_CONTENT_TYPE_FILE);
            if (StringUtils.isNotBlank(name)) resp.setHeader(HEADER_CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + HttpUtils.encodeUrl(name));
            int read = 0, total = 0;
            OutputStream os = resp.getOutputStream();
            while ((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
                total += read;
                os.flush();
            }
            if (LOG.isDebugEnabled()) LOG.debug("[downloadFile] total read -> {}", total);
        }
        catch (IOException e) {
            LOG.error("[downloadFile] could not open url due to exception.", e);
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 测试一个Proxy是否可靠
     * 
     * @param proxy
     *            Proxy
     * @param url
     */
    public static long testProxy(Proxy proxy, URL url,int ctimeout,int rtimeout,int testTimes) {
        if (proxy == null) return -1;
        if (url == null) {
            try {
                url = new URL(DEFAULT_TEST_PROXY_URL);
            }
            catch (MalformedURLException e1) {
                LOG.warn("[testProxy] illegal url -> {}", url);
                return -1;
            }
        }
        HttpURLConnection conn = null;
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            conn = (HttpURLConnection) url.openConnection(proxy);
            if(ctimeout > 0) conn.setConnectTimeout(ctimeout);
            if(rtimeout > 0) conn.setReadTimeout(rtimeout);
            long available = conn.getInputStream().available(); // try to get input
            int code = conn.getResponseCode();
            if(available < 1 || code != 200) return -1;    //no content get
            watch.stop();
            return watch.getTime();
        }
        catch (Exception e) {
            LOG.warn("[testProxy] Could not connect to proxy -> {}", proxy.address());
            if(testTimes > 0) return testProxy(proxy, url,ctimeout,rtimeout,testTimes-1);
        }
        finally {
            if (conn != null) IOUtils.close(conn);
        }
        return -1;
    }
}
