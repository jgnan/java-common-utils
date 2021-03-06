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
package com.shenit.commons.repos.domain;

import java.io.Serializable;

public class Identifiable implements Serializable{
	private static final long serialVersionUID = 1961624854794650588L;
	public long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
