/***********************************************************************************************************************
 * 
 * Copyright (C) 2013, 2014 by huanju (http://www.huanju.com)
 * http://www.sunnsoft.com/
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
 * 并发编程工具.
 * @author jiangnan
 *
 */
public final class ConcurrencyUtils
{
    /**
     * Sleep quite
     * @param millis
     */
    public static void sleepQuite(long millis){
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
        }
    }
	/**
	 * 执行所有Runnable接口的run方法
	 * @param tasks
	 */
	public static void runAll(Runnable... tasks){
		for(Runnable task : tasks) task.run();
	}
}
