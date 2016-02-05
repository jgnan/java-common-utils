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
package com.shenit.commons.mvc.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.shenit.commons.utils.CollectionUtils;
import com.shenit.commons.utils.GsonUtils;
import com.shenit.commons.utils.MapUtils;

public class ResponseData implements Serializable,JsonSerializable{
	private static final long serialVersionUID = 5921390031018921328L;
	private static final String FIELD_COUNT = "count";
	private static final String FIELD_RECORDS = "records";
	private static final String FIELD_RECORD = "record";
	private static final String FIELD_MESSAGE_TYPE = "message_type";
	private static final String FIELD_MESSAGE = "message";
	
	public Map<String,Object> data;
	public boolean hasErrors = false;
	
	public ResponseData(){
	    this.data = new HashMap<>();
	}
	
	public ResponseData(Object record){
	    this();
	    set(FIELD_RECORD,record);
    }
    
	
	public ResponseData(Collection<?> records){
		this(records,CollectionUtils.size(records));
	}
	
	public ResponseData(Collection<?> records, long totalCount){
	    this();
        data.put(FIELD_RECORDS,records);
        set(FIELD_COUNT,totalCount);
	}
	
	public ResponseData set(String key, Object val){
	    if(data == null) data = new HashMap<>();
	    data.put(key, val);
	    return this;
	}
	
	/**
	 * Get fields.
	 * @param fieldRecords
	 * @return
	 */
    public Object get(String key) {
        return MapUtils.get(data, key);
    }
	
	/**
	 * Get data counts
	 * @return
	 */
	public long getCount() {
		return MapUtils.getLong(data, FIELD_COUNT, 0l);
	}
	
	/**
	 * Set count to the record sets
	 * @param count
	 */
    public void setCount(long count) {
        set(FIELD_COUNT,count);
	}
    
    /**
     * Get Records
     * @return
     */
	public Collection<?> getRecords() {
		return (Collection<?>) get(FIELD_RECORDS);
	}
	
	/**
	 * Set the record fields
	 * @param records
	 */
	public ResponseData setRecords(Collection<?> records) {
		set(FIELD_RECORDS,records);
		return this;
	}
	
	/**
     * Get Records
     * @return
     */
    public Object getRecord() {
        return get(FIELD_RECORD);
    }
	
	/**
	 * Set to record fields
	 * @param record
	 * @return
	 */
    public ResponseData setRecord(Object record) {
        set(FIELD_RECORD,record);
        return this;
    }

    @Override
    public String toJson() {
        return GsonUtils.toJson(data);
    }
    
    /**
     * Set message
     * @param message
     * @param type
     * @param params
     * @return
     */
    public ResponseData setMessage(String message,MessageType type,Object... params){
        set(FIELD_MESSAGE_TYPE,type.toString());
        set(FIELD_MESSAGE,ArrayUtils.isEmpty(params) ? message : String.format(message,params));
        hasErrors = hasErrors || type.equals(MessageType.error);
        return this;
    }
    
    /**
     * Get message fields
     * @return
     */
    public String getMessage(){
        return "["+get(FIELD_MESSAGE_TYPE)+"] " + get(FIELD_MESSAGE);
    }

    /**
     * Message types
     * @author jiangnan
     *
     */
    public static enum MessageType{
        success,info,warning,error;
    }
    
    /**
     * Usual record result codes
     * @author jiangnan
     *
     */
    public static interface ResultCodes{
        String INSUFFICIENT_PARAMS = "insufficient_params";
        String INSUFFICIENT_PRIVILEGE = "insufficient_privilege";
        String NO_RECORD = "no_record";
        String NO_TYPE_RECORD = "no_%s_record";
        String OK = "ok";
        String FAILED = "failed";
        String UNKNOWN_ERROR = "unknown_error";
        String BAD_REQUST = "bad_request";
        String ILLEGAL_VALUE = "illegal_value";
        String USER_NOT_EXISTS = "user_not_exists";
        String DUPLICATE_RECORD = "duplicate_record";
        String DUPLICATE_FIELD = "duplicate_%s";
    }

    public boolean hasErrors() {
        return hasErrors;
    }
}
