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
package org.hbird.business.systemmonitoring;

import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;

import org.hbird.exchange.core.Parameter;

/**
 * A parameter reporting the current memory usage (bytes). Each call will lead to
 * the creation of a Parameter instance, holding the memory usage in bytes as a long.
 * 
 * To create a Parameter instance every 60 seconds and inject this into a Activemq parameter topic,
 * configure the following;
 * 
 * 	<bean id="memory" class="org.hbird.business.systemmonitoring.MemoryParameter">
 *    <constructor-arg index="0" value="Memory"/>
 *    <constructor-arg index="1" value="The memory (bytes) currently used by the system."/>
 *  </bean>
 *  <camelContext id="context" xmlns="http://camel.apache.org/schema/spring">
 *    <route>
 *      <from uri="timer://memory?fixedRate=true&amp;period=60000" />
 *      <to uri="bean:memory"/>
 *      <to uri="activemq:topic:Parameters"/>
 *    </route>
 *  </camelContext>
 * 
 */
public class HeapMemoryUsageMonitor extends Monitor {
	
	public HeapMemoryUsageMonitor(String componentId) {
		super(componentId);
	}

	/**
	 * Method to create a new instance of the memory parameter. The body of the 
	 * exchange will be updated.
	 * 
	 * @param exchange The exchange to hold the new value.
	 * @throws UnknownHostException 
	 */
	public Parameter check() throws UnknownHostException {
		return new Parameter(componentId, "Heap Memory Usage", "MonitoredResource", "The heap memory usage", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed(), "Byte");
	}	
}
