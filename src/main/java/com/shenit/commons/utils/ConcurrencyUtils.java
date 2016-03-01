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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 并发编程工具.
 * @author jiangnan
 *
 */
public final class ConcurrencyUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrencyUtils.class);
     
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
	
	/**
     * Get value from future task.
     * @param task
     * @return
     */
	public static <V> V getFuture(FutureTask<V> task) {
	    return getFuture(task,null);
	}
	/**
	 * Get value from future task.
	 * @param task
	 * @param defaultVal Fallback when no value get from task.s
	 * @return
	 */
    public static <V> V getFuture(FutureTask<V> task,V defaultVal) {
        try {
            return task == null ? null : task.get();
        }
        catch (InterruptedException | ExecutionException e) {
            LOG.warn("[getFuture] Get future task result with exception.",e);
        }
        return defaultVal;
    }
}
