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
package org.hbird.business.systemtest;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.hbird.exchange.configurator.StartQueueManagerComponent;
import org.hbird.exchange.dataaccess.DeletionRequest;
import org.hbird.queuemanagement.ClearQueue;
import org.hbird.queuemanagement.ListQueues;
import org.hbird.queuemanagement.QueueHelper;

public class Finisher extends SystemTest {

	private static org.apache.log4j.Logger LOG = Logger.getLogger(Finisher.class);

	public void process(Exchange exchange) throws InterruptedException {
		
		LOG.info("------------------------------------------------------------------------------------------------------------");
		LOG.info("Starting");

		startQueueManager();
		
		LOG.info("System Test done.");
		
		LOG.info("Cleaning archive.");
		
		injection.sendBody(new DeletionRequest("SystemTest", "Archive", "*:*"));	

		LOG.info("Purging queues.");
		
		/** List all queues. */
		List<String> queues = (List<String>) injection.requestBody(new ListQueues("SystemTest", "CommandingQueueManager"));
				
		/** Purge all. */
		for (String canonicalName : queues) {
			injection.requestBody(new ClearQueue("SystemTest", "CommandingQueueManager", QueueHelper.getQueueName(canonicalName)));
		}
		
		LOG.info("Ciao.");
		
		Thread.sleep(2000);
		System.exit(1);
	}	
}
