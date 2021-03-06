/**
 * Licensed to the Hummingbird Foundation (HF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The HF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hbird.business.validation.limits;

import org.hbird.exchange.core.Parameter;
import org.hbird.exchange.validation.Limit;

/**
 * Limit class for checking whether a lower end limit has been violated.
 */
public class LowerLimitChecker extends BaseLimitChecker {
	
	public LowerLimitChecker(Limit limit) {
		super(limit);
	}

	/* (non-Javadoc)
	 * @see org.hbird.validation.parameter.BaseLimit#checkLimit()
	 */
	protected boolean checkLimit(Parameter parameter) {
		return parameter.compareTo(limit) >= 0;
	}	
}
