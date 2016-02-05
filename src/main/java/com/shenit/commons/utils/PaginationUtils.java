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

/**
 * 分页工具类.
 * @author jiangnan
 *
 */
public class PaginationUtils
{
	/**
	 * 计算开始index
	 * @param page 页数
	 * @param size 每页size
	 * @return
	 */
	public static long begin(int page,long size){
		return Math.max(0, (page - 1) * size);
	}
	
	/**
	 * 计算总页数
	 * @param count 总记录数
	 * @param size 数量
	 * @return
	 */
	public static int pages(int count, long size){
		return (int) Math.max(0, (count - 1) / size) + 1;
	}
}
