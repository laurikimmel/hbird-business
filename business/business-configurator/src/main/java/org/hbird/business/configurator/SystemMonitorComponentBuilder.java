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
package org.hbird.business.configurator;

import org.apache.camel.model.ProcessorDefinition;
import org.hbird.business.systemmonitoring.CpuMonitor;
import org.hbird.business.systemmonitoring.HarddiskMonitor;
import org.hbird.business.systemmonitoring.HeapMemoryUsageMonitor;
import org.hbird.business.systemmonitoring.ThreadCountMonitor;

/**
 * Component builder to create a system monitoring component.
 * 
 * @author Gert Villemos
 *
 */
public class SystemMonitorComponentBuilder extends ComponentBuilder {

	@Override
	public void doConfigure() {
		from("timer://systemmonitor?fixedRate=true&period=10000").multicast().to("seda:heap", "seda:thread", "seda:cpu", "seda:harddisk");

		from("seda:heap").bean(new HeapMemoryUsageMonitor(command.getName())).to("seda:out");
		from("seda:thread").bean(new ThreadCountMonitor(command.getName())).to("seda:out");
		from("seda:cpu").bean(new CpuMonitor(command.getName())).to("seda:out");
		from("seda:harddisk").bean(new HarddiskMonitor(command.getName())).to("seda:out");

		ProcessorDefinition<?> route = from("seda:out");
		addInjectionRoute(route);
		
		addCommandHandler();
	}
}
