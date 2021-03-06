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
import java.util.Date;
import java.util.List;

import org.apache.camel.Handler;
import org.apache.log4j.Logger;
import org.hbird.exchange.commandrelease.CommandRequest;
import org.hbird.exchange.core.Command;
import org.hbird.exchange.core.State;
import org.hbird.exchange.tasking.SetParameter;
import org.hbird.exchange.tasking.Task;

public class CommandingTester extends SystemTest {

	private static org.apache.log4j.Logger LOG = Logger.getLogger(CommandingTester.class);

	@Handler
	public void process() throws InterruptedException {

		LOG.info("------------------------------------------------------------------------------------------------------------");
		LOG.info("Starting");
		
		startMonitoringArchive();
		startCommandingChain();
		
		Thread.sleep(2000);

		/** The command object. Destination is an unknown object. */
		Command command = new Command("SystemTest", "GroundStation1", "COM2", "A test command"); 
		
		/** Send a simple command request. */
		injection.sendBody(new CommandRequest("SystemTest", "COMREQ1", "A simple command request container with no lock states and no tasks.", null, null, command));
		
		Thread.sleep(2000);
		
		azzert(commandingListener.lastReceived.getName().equals("COM2"), "Command request was executed, command 'COM2' was issued.");
		
		
		/** Create a command with a release time. */
		Date now = new Date();
		command.setReleaseTime(now.getTime() + 2000);
		
		injection.sendBody(new CommandRequest("SystemTest", "COMREQ1", "A simple command request container with no lock states and no tasks.", null, null, command));
		
		Thread.sleep(2000);

		
		/** Add tasks to be done and lock states. */
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(new SetParameter("SystemTest", "TASK_SET_PARA10", "A test parameter set by a task", 0, "PARA10", "", "A test parameter more", 9d, "Bananas"));
		tasks.add(new SetParameter("SystemTest", "TASK_SET_PARA11", "A test parameter set by a task", 0, "PARA11", "", "A test parameter more", 1d, "Bananas"));
		
		List<String> states = new ArrayList<String>();		
		states.add("STATE_COM1");
		states.add("STATE_COM2");
		states.add("STATE_COM3");
		states.add("STATE_COM4");
		
		/** Set the values of the states. */
        injection.sendBody(new State("SystemTestSuite", "STATE_COM1", "A test description,", "COM2", true));
        injection.sendBody(new State("SystemTestSuite", "STATE_COM2", "A test description,", "COM2", true));
        injection.sendBody(new State("SystemTestSuite", "STATE_COM3", "A test description,", "COM2", false));
        injection.sendBody(new State("SystemTestSuite", "STATE_COM4", "A test description,", "COM2", true));

        /** Send command to commit all changes. */
		forceCommit();
        
        /** The command should fail, as one of the states is 'false'. */
		injection.sendBody(new CommandRequest("SystemTest", "COMREQ1", "A simple command request container with no lock states and no tasks.", states, tasks, command));
		
		Thread.sleep(2000);
		
		azzert(failedCommandRequestListener.lastReceived.getName().equals("CommandContainerCOMREQ1"));
		failedCommandRequestListener.lastReceived = null;
		
		
		
		
		/** Update state to make the command succeed. */
        injection.sendBody(new State("SystemTestSuite", "STATE_COM3", "A test description,", "COM2", true));

        /** Send command to commit all changes. */
		forceCommit();
		
        /** The command should fail, as one of the states is 'false'. */
		injection.sendBody(new CommandRequest("SystemTest", "COMREQ1", "A simple command request container with no lock states and no tasks.", states, tasks, command));
		
		Thread.sleep(2000);
		
		azzert(failedCommandRequestListener.lastReceived.getName().equals("CommandContainerCOMREQ1"));
		

		
		/** Add a state that is not inserted in the command, but which is a state of the command. See if the command fails.*/
        injection.sendBody(new State("SystemTestSuite", "STATE_OTHER", "A test description,", "COM2", false));

		/** Send command to commit all changes. */
		forceCommit();
		
        /** The command should fail, as one of the states is 'false'. */
		injection.sendBody(new CommandRequest("SystemTest", "COMREQ1", "A simple command request container with no lock states and no tasks.", states, tasks, command));

		Thread.sleep(2000);
		
		azzert(failedCommandRequestListener.lastReceived.getName().equals("CommandContainerCOMREQ1"));
		
		LOG.info("Finished");
	}
}
