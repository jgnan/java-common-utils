package com.shenit.commons.mvc.model;

import java.io.Serializable;

/**
 * 响应批量操作数据结果
 * @author jiangnan
 *
 */
public class BatchResultResponse implements Serializable{
    private static final long serialVersionUID = -1589793006128232574L;
    public int errorCount;
    public int successCount;
    public Object data;
    public String message;
    public int getErrorCount() {
        return errorCount;
    }
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
    public int getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    
}
