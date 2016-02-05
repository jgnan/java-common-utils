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
 * @author jam
 *
 */
public class StatusUtils {

    /**
     * 检查目标位掩码是否一致
     * @param status 当前状态
     * @param bitMask 目标位掩码
     * @return
     */
    public static boolean isStatusSetted(Integer status, int bitMask) {
        return status == null ? false : ((status & bitMask) == bitMask); 
    }
    
    /**
     * 计算置一后状态 <br/>
     * 【注意】该方法不能作用置零
     * @param status 当前状态
     * @param bitMask 目标位掩码
     * @return 对应状态位设1后结果
     */
    public static Integer calcSettedStatus(Integer status, int bitMask) {
        return status == null ? bitMask : (status | bitMask);
    }
    
    /**
     * 计算置零后状态 <br/>
     * 【注意】该方法不能作用置一
     * @param status 当前状态
     * @param bitMask 目标位掩码
     * @return 对应状态位设0后结果
     */
    public static Integer calcUnsettedStatus(Integer status, int bitMask) {
        return status == null ? 0 : ((status | bitMask) ^ bitMask);
    }
}
