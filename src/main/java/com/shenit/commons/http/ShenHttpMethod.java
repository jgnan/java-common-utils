package com.shenit.commons.http;
/**
 * Http 可用方法.
 * @author jiangnan
 *
 */
public enum ShenHttpMethod {
    Get("GET"), Post("POST"), Put("PUT"), Head("HEAD"), Delete("DELETE");
    public String code;
    ShenHttpMethod(String code){this.code = code;}
}
