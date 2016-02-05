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
package com.shenit.commons.mvc.model;

/**
 * Account status.
 * @author jiangnan
 *
 */
public enum AccountStatus {
    /** Most common status to an account */
    Normal(0), 
    /** 
     * Indicates that this account should set its password. 
     * For forget/reset password scenario 
     */
    InitPassowrd(2), 
    /**
     * New register status use for new register client whom missing their
     * email, birthday, or other credential information
     */
    NewRegister(4),
    /**
     * Indicates that this account is locked by wrong password retries
     */
    Locked(6),
    /**
     * Indicates that this account is frozen by admin for some bad reason.
     */
    Frozen(8); 
    public int code;
    AccountStatus(int code){this.code=code;}
    
}
