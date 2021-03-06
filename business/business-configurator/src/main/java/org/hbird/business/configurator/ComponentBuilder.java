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

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.hbird.business.core.Scheduler;
import org.hbird.exchange.businesscard.BusinessCard;
import org.hbird.exchange.commandrelease.CommandRequest;
import org.hbird.exchange.configurator.StandardEndpoints;
import org.hbird.exchange.configurator.StartComponent;
import org.hbird.exchange.core.Command;
import org.hbird.exchange.core.State;
import org.hbird.exchange.tasking.Task;


/**
 * Base classs for all component builders.
 * 
 * A component builder is a class that based on a StartComponent command will
 * create the beans and routes needed to assembly the component. The assembly
 * is done at runtime.
 * 
 * @author Gert Villemos
 *
 */
public abstract class ComponentBuilder extends RouteBuilder {

	/** The Start request of the component. */
	protected StartComponent command = null;

	protected List<Command> commands = new ArrayList<Command>();
	
	/**
	 * Sets the command which this builder use to create the component.
	 * 
	 * @param command The command to start a component
	 */
	public void setCommand(StartComponent command) {
		this.command = command;
	};

	@Override
	public void configure() throws Exception {

		/** Setup the component specific services. */
		doConfigure();
		
		/** Setup the BusinessCard*/
		BusinessCard card = new BusinessCard(getComponentName(), command.getHeartbeat(), commands);
		ProcessorDefinition<?> route = from("timer:businessCard_" + getComponentName() + "?period=" + command.getHeartbeat()).bean(card);
		addInjectionRoute(route);
	}

	protected void addCommandHandler() {
		/** Route for commands to this component, i.e. configuration commands. */
		from(StandardEndpoints.commands + "?" + addDestinationSelector(getComponentName())).bean(new DefaultCommandHandler(), "receiveCommand");
	}
	
	/** The component specific configuration. */
	protected abstract void doConfigure();

	/**
	 * Creates a string that can be used as a selector on activemq 
	 * 
	 * @param name The type of the object to be retrieved
	 * @return The string to be used as selector
	 */
	protected String addTypeSelector(String type) {
		return "selector=type='" + type + "'";
	}

	/**
	 * Creates a string that can be used as a selector on activemq 
	 * 
	 * @param name The destination of the object to be retrieved
	 * @return The string to be used as selector
	 */
	protected String addDestinationSelector(String destination) {
		return "selector=destination='" + destination + "'";
	}
	
	/**
	 * Creates a string that can be used as a selector on activemq 
	 * 
	 * @param name The name of the object to be retrieved
	 * @return The string to be used as selector
	 */
	protected String addNameSelector(String name) {
		return "selector=name='" + name + "'";
	}
	
	/**
	 * Returns the name of the component to be build.
	 * 
	 * @return The name of the component to be build, as set in the received command.
	 */
	public String getComponentName() {
		return (String) command.getArgument("componentname");
	}

	/**
	 * Adds to a route the injection path into hummingbird. The injection path can
	 * be complex. It will set headers for routing and filtering purposes. It will
	 * route different kinds of messages to different endpoints for distribution.
	 * 
	 * @param route
	 */
	protected void addInjectionRoute(ProcessorDefinition<?> route) {
		Scheduler scheduler = new Scheduler();

		route

		/** Dont route messages with a NULL body. */
		.choice()
		.when(simple("${in.body} == null"))
		.stop()
		.end()
		
		/** Set standard headers. */
		.setHeader("name", simple("${in.body.name}"))
		.setHeader("issuedBy", simple("${in.body.issuedBy}"))
		.setHeader("type", simple("${in.body.type}"))
		.setHeader("datasetidentifier", simple("${in.body.datasetidentifier}"))

		
		/** Set object specific headers. */
		.choice()
		.when(body().isInstanceOf(State.class))
		.setHeader("isStateOf", simple("${in.body.isStateOf}"))
		.when(body().isInstanceOf(Command.class))
		.setHeader("destination", simple("${in.body.destination}"))
		.end()

		/** Schedule the release, if this object implements IScheduled. */
		.bean(scheduler)

		/** Route to the topic / query. */
		.choice()
		.when((body().isInstanceOf(Task.class))).to(StandardEndpoints.tasks)
		.when((body().isInstanceOf(CommandRequest.class))).to(StandardEndpoints.requests)
		.when((body().isInstanceOf(Command.class))).to(StandardEndpoints.commands)
		.otherwise().to(StandardEndpoints.monitoring);
	}
}
