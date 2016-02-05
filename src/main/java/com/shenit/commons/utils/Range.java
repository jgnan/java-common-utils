/***********************************************************************************************************************
 * 
 * Copyright (C) 2015 by koogroup (http://www.koogroup.co)
 * http://www.koogroup.co/
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

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * Range object.
 * @author jiangnan
 *
 */
public class Range<T extends Number> {
    public T from;
    public T to;
    public Range(T from, T to){
        this.from = from;
        this.to = to;
        fix();
    }
    
    @SafeVarargs
    public Range(T... ranges){
        if(ranges == null) return;
        if(ranges.length > 0) from =ranges[0];
        if(ranges.length > 1) to =ranges[1];
        fix();
    }
    
    
    /**
     * Parse a comma join range string to range object 
     * @param ranges
     * @return
     */
    public static Range<Long> range(String rangeStr){
        String[] ranges  = ShenStringUtils.split(
                        ShenStringUtils.str(rangeStr), 
                        ShenStringUtils.DELIMITER_COMMA);
        if(ranges == null) return null;
        return new Range<Long>(Arrays.stream(ranges).map((item) -> {
            return StringUtils.isNumeric(item) ? Long.parseLong(item) : null;
        }).toArray(Long[]::new));
    }
    
    /**
     * Check whether a value is within a specific range
     * @param val
     * @return
     */
    public boolean isWithin(T val){
        return within(val) == val;
    }
    
    /**
     * Ensure a value is within a specific range
     * @param val
     * @return
     */
    public T within(T val){
        if(from == null && to == null) return val;
        T result = val;
        double valDouble = val.doubleValue();
        if(from != null) result = valDouble < from.doubleValue() ? from : val;
        if(to != null) result = valDouble > to.doubleValue() ? to : val;
        return result;
    }
    
    
    /*
     * fix the range, making lower value set to from and bigger set to to
     */
    private void fix(){
        if(from != null && to != null && from.longValue() > to.longValue()){
            T tmp = to;
            to  = from;
            from = tmp;
        }
    }
    
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "["+ShenStringUtils.str(from)+","+ShenStringUtils.str(to)+"}";
    }
}
