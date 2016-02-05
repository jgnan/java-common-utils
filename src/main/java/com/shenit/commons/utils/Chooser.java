/***********************************************************************************************************************
 * 
 * Copyright (C) 2013, 2014 by huya (http://www.huya.com)
 * http://www.huya.com/
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


/**
 * <h1>选择器</h1>
 * <p>可以免除大家写switch...case的情况.</p>
 * <h3>使用方法：</h3>
 * <pre>
 * int status = 2;
 * Chooser<Integer, String>.choose(status)
 *      .when(1,"success")
 *      .when(2,"failed")
 *      .otherwise("unkown");
 * System.out.println(chooser.getValue());  //failed
 * </pre>
 * 
 * @author jiangnan
 *
 */
public class Chooser<T,V> {
    private V value = null;
    private T chooser = null;
    public Chooser(T chooser){
        this.chooser = chooser;
    }
    /**
     * 选择某个值
     * @param cond
     * @param val
     * @return
     */
    public Chooser<T,V> when(T cond, V val){
        if(ValidationUtils.eq(chooser, cond)) value = val;
        return this;
    }
    /**
     * 对于其他情况设置默认值.
     * @param defaultVal
     * @return
     */
    public Chooser<T,V> otherwise(V defaultVal){
        value = defaultVal;
        return this;
    }
    
    /**
     * 获取最终值.
     * @return
     */
    public V getValue(){
        return getValue(null);
    }
    
    /**
     * 返回最终值，可以给予默认值，如果value为空则会返回默认值
     * @param defaultVal 提供的默认值
     * @return
     */
    public V getValue(V defaultVal){
        return value == null && defaultVal != null ? defaultVal : value;
    }
}
