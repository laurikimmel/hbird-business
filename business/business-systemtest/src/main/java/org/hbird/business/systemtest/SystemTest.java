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

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.hbird.exchange.configurator.StartCommandComponent;
import org.hbird.exchange.configurator.StartArchiveComponent;
import org.hbird.exchange.configurator.StartNavigationComponent;
import org.hbird.exchange.configurator.StartQueueManagerComponent;
import org.hbird.exchange.configurator.StartTaskExecutorComponent;
import org.hbird.exchange.dataaccess.CommitRequest;
import org.hbird.exchange.dataaccess.DeletionRequest;

public abstract class SystemTest {

	private static org.apache.log4j.Logger LOG = Logger.getLogger(SystemTest.class);

	protected boolean exitOnFailure = true;
	
	@Produce(uri = "direct:injection")
	protected ProducerTemplate injection;

	protected Listener monitoringListener = null;

	protected Listener parameterListener = null;
	
	protected Listener commandingListener = null;

	protected Listener failedCommandRequestListener = null;

	protected Listener businessCardListener = null;
	
	protected Listener orbitalStateListener = null;
	
	protected Listener locationEventListener = null;
	
	protected Listener labelListener = null;
	
	protected Listener stateListener = null;
	
	protected CamelContext context = null;
	
	protected void azzert(boolean assertion) {
		if (assertion == false) {
			LOG.error("FAILED.");
			if (exitOnFailure) {
				System.exit(1);
			}
		}
	}

	protected void azzert(boolean assertion, String message) {
		if (assertion == false) {
			LOG.error("SYSTEM TEST: " + message + " (FAILED)");
			if (exitOnFailure) {
				System.exit(1);
			}
		}
		else {
			LOG.info("SYSTEM TEST: " + message + " (OK)");
		}
	}

	public Listener getMonitoringListener() {
		return monitoringListener;
	}

	public void setMonitoringListener(Listener monitoringListener) {
		this.monitoringListener = monitoringListener;
	}

	public Listener getCommandingListener() {
		return commandingListener;
	}

	public void setCommandingListener(Listener commandingListener) {
		this.commandingListener = commandingListener;
	}

	public Listener getFailedCommandRequestListener() {
		return failedCommandRequestListener;
	}

	public void setFailedCommandRequestListener(
			Listener failedCommandRequestListener) {
		this.failedCommandRequestListener = failedCommandRequestListener;
	}


	protected static boolean monitoringArchiveStarted = false;
	public void startMonitoringArchive() throws InterruptedException {

		if (monitoringArchiveStarted == false) {
			LOG.info("Issuing command for start of a parameter archive.");

			StartArchiveComponent request = new StartArchiveComponent("Archive");
			injection.sendBody(request);

			/** Give the component time to startup. */
			Thread.sleep(1000);
			
			/** TODO Send command to the archive to delete all data. */
			injection.sendBody(new DeletionRequest("SystemTest", "Archive", "*:*"));	

	        /** Send command to commit all changes. */
			injection.sendBody(new CommitRequest("SystemTest", "Archive"));	

			monitoringArchiveStarted = true;
		}		
	}

	protected static List<String> startedTaskComponents = new ArrayList<String>();
	public void startTaskComponent(String name) throws InterruptedException {

		if (startedTaskComponents.contains(name) == false) {
			LOG.info("Issuing command for start of a task executor component '" + name + "'.");

			injection.sendBody(new StartTaskExecutorComponent(name));
			
			/** Give the component time to startup. */
			Thread.sleep(1000);
			
			startedTaskComponents.add(name);
		}		
	}

	protected static boolean commandingChainStarted = false;
	public void startCommandingChain() throws InterruptedException {

		if (commandingChainStarted == false) {
			LOG.info("Issuing command for start of a commanding chain.");

			/** Create command component. */
			injection.sendBody(new StartCommandComponent("CommandingChain1"));
			commandingChainStarted = true;
		}
	}
	
	protected static boolean orbitPredictorStarted = false;
	public void startOrbitPredictor() throws InterruptedException {

		if (orbitPredictorStarted == false) {
			LOG.info("Issuing command for start of a orbital predictor.");

			/** Create command component. */
			injection.sendBody(new StartNavigationComponent("OrbitPredictor"));
			orbitPredictorStarted = true;
		}
	}

	protected static boolean queueManagerStarted = false;
	public void startQueueManager() throws InterruptedException {

		if (queueManagerStarted == false) {
			LOG.info("Issuing command for start of a queue manager.");

			/** Create command component. */
			injection.sendBody(new StartQueueManagerComponent("CommandingQueueManager"));
			queueManagerStarted = true;
		}
	}
	
	public Listener getBusinessCardListener() {
		return businessCardListener;
	}

	public void setBusinessCardListener(Listener businessCardListener) {
		this.businessCardListener = businessCardListener;
	}

	public Listener getParameterListener() {
		return parameterListener;
	}

	public void setParameterListener(Listener parameterListener) {
		this.parameterListener = parameterListener;
	}	
	
	public Listener getOrbitalStateListener() {
		return orbitalStateListener;
	}

	public void setOrbitalStateListener(Listener orbitalStateListener) {
		this.orbitalStateListener = orbitalStateListener;
	}

	public Listener getLocationEventListener() {
		return locationEventListener;
	}

	public void setLocationEventListener(Listener locationEventListener) {
		this.locationEventListener = locationEventListener;
	}

	public Listener getLabelListener() {
		return labelListener;
	}

	public void setLabelListener(Listener labelListener) {
		this.labelListener = labelListener;
	}

	public Listener getStateListener() {
		return stateListener;
	}

	public void setStateListener(Listener orbitalListener) {
		this.stateListener = orbitalListener;
	}

	public CamelContext getContext() {
		return context;
	}

	public void setContext(CamelContext context) {
		this.context = context;
	}

	protected void forceCommit() throws InterruptedException {
		/** Send command to commit all changes. */
		injection.sendBody(new CommitRequest("SystemTest", "ParameterArchive"));	
		Thread.sleep(2000);
	}
}
