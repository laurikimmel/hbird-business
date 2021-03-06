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
package org.hbird.business.navigation;

import java.util.Date;
import java.util.List;

import org.apache.camel.Body;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.impl.DefaultExchange;
import org.apache.log4j.Logger;
import org.hbird.exchange.core.Named;
import org.hbird.exchange.dataaccess.LocationRequest;
import org.hbird.exchange.dataaccess.TlePropagationRequest;
import org.hbird.exchange.dataaccess.TleRequest;
import org.hbird.exchange.navigation.Location;import org.hbird.exchange.navigation.TleOrbitalParameters;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.tle.TLE;
import org.orekit.tle.TLEPropagator;
import org.orekit.utils.PVCoordinates;


public class TleOrbitalPredictor {

	private static org.apache.log4j.Logger LOG = Logger.getLogger(TleOrbitalPredictor.class);

	protected KeplianOrbitPredictor keplianOrbitPredictor = null;

	public String name = "OrbitPredictor";

	
	
	/**
	 * Method used to receive a TLE and generate a propagation request based on the TLE.
	 * 
	 * @param tleParameters
	 * @param context
	 * @return
	 * @throws OrekitException
	 */
//	public List<DataSet> receiveTleParameters(@Body TleOrbitalParameters tleParameters, CamelContext context) throws OrekitException {
//		TlePropagationRequest request = new TlePropagationRequest("", tleParameters.getSatellite(), tleParameters, null);
//		return predictOrbit(request, context);
//	} 
	
	
	/** 
	 * Method to predict the orbit. 
	 * 
	 * @throws OrekitException */
	@Handler
	public List<Named> predictOrbit(@Body TlePropagationRequest request, CamelContext context) throws OrekitException {

		/** Create the TLE element */
		TleOrbitalParameters parameters = request.getTleParameters();
		if (parameters == null) {
			parameters = getTleParameters(request.getSatellite(), context);
		}

		List<Named> results = null;

		/** If no TLE found, then we cant propagate.*/
		if (parameters == null) {
			LOG.error("Request holds no TLE object and Failed to find TLE in archive for satellite '" + request.getSatellite() + "'");
		}
		else {
			TLE tle = new TLE(parameters.getTleLine1(), parameters.getTleLine2());

			/** Get the definition of the Locations. */
			List<Location> locations = getLocations(request.getLocations(), context);

			/** Get the initial orbital state at the requested start time.*/
			AbsoluteDate startDate = new AbsoluteDate(new Date(request.getStartTime()), TimeScalesFactory.getUTC());
			PVCoordinates initialOrbitalState = TLEPropagator.selectExtrapolator(tle).getPVCoordinates(startDate);

			/** Calculate the orbital states and the contact events from the initial time forwards */
			results = keplianOrbitPredictor.predictOrbit(locations, initialOrbitalState, request.getStartTime(), request.getSatellite(), request.getStepSize(), request.getDeltaPropagation(), request.getContactDataStepSize(), parameters, context, request.getPublish());
		}

		/** Return the data. */
		return results;
	}

	private TleOrbitalParameters getTleParameters(String satellite, CamelContext context) {
		TleRequest request = new TleRequest("OrbitPredictor", satellite);

		Exchange arg1 = new DefaultExchange(context);
		arg1.getIn().setBody(request);
		context.createProducerTemplate().send("direct:locationstore", arg1);
		List<Named> results = (List<Named>) arg1.getOut().getBody();

		TleOrbitalParameters parameters = null;
		if (results.size() == 1) {
			parameters = (TleOrbitalParameters) results.get(0);
		}

		return parameters;		
	}

	private List<Location> getLocations(List<String> list, CamelContext context) {

		LocationRequest request = new LocationRequest("OrbitPredictor", list);

		Exchange arg1 = new DefaultExchange(context);
		arg1.getIn().setBody(request);
		context.createProducerTemplate().send("direct:locationstore", arg1);
		List<Location> locations = (List<Location>) arg1.getOut().getBody();

		return locations;
	}

	public void setKeplianOrbitPredictor(KeplianOrbitPredictor keplianOrbitPredictor) {
		this.keplianOrbitPredictor = keplianOrbitPredictor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
