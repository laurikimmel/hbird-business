/**
 * Licensed under the Apache License, Version 2.0. You may obtain a copy of 
 * the License at http://www.apache.org/licenses/LICENSE-2.0 or at this project's root.
 */

package org.hbird.business.parameterstorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Body;
import org.apache.log4j.Logger;
import org.hbird.exchange.core.Named;
import org.hbird.exchange.core.State;


/**
 * Very simple parameter storage. Will store only the last received value of 
 * each parameter within a Map.
 */
public class InMemoryParameterBuffer {
	
	private static org.apache.log4j.Logger LOG = Logger.getLogger(InMemoryParameterBuffer.class);
	
	/* Map holding the buffered values. */
	protected Map<String, Named> latestParameterValue = new HashMap<String, Named>();

	/** Semantic parameter. */
	protected Map<String, List<Named>> latestParameterValueSemantic = new HashMap<String, List<Named>>();
	
	protected Pattern parameterName = Pattern.compile("name=(.+)");
	protected Pattern isStateOf = Pattern.compile("isStateOf=(.+)");
	
	/**
	 * Method to retrieve a parameter value based on the parameter name.
	 *  
	 * @param name The name of the parameter to be retrieved.
	 * @return The parameter object
	 */
	public Object getParameter(@Body String query) {
		LOG.info("Received query '" + query + "'.");
		
		Matcher name = parameterName.matcher(query);
		if (name.find()) {
			return latestParameterValue.get(name.group(1));
		}
		
		Matcher stateOf = isStateOf.matcher(query);
		if (stateOf.find()) {
			return latestParameterValueSemantic.get(stateOf.group(1));
		}
		
		return null;
	}
	
	
	/**
	 * Method to store a parameter into the buffer.
	 * 
	 * @param parameter The parameter to be stored.
	 */
	
	/** TODO The method should consider the timestamp as there is no guarantee of order */
	public void storeParameter(@Body Named parameter) {
		latestParameterValue.put(parameter.getName(), parameter);
		
		if (parameter instanceof State) {
			State state = (State) parameter;
			if (state.getIsStateOf() != null) {
				
				if (latestParameterValueSemantic.get(state.getIsStateOf()) == null) {
					latestParameterValueSemantic.put((String) state.getIsStateOf(), new ArrayList<Named>());
				}
				
				latestParameterValueSemantic.get(state.getIsStateOf()).add(state);
			}
		}
	}

	
	/**
	 * Getter of the buffer.
	 * 
	 * @return The Map acting as a buffer.
	 */
	public Map<String, Named> getLatestParameterValue() {
		return latestParameterValue;
	}
}
