package org.hbird.exchange.dataaccess;

import java.util.List;

public class ParameterRequest extends DataRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2494842649084114764L;

	public ParameterRequest(String parameter, int rows) {
		super("Assembly", "ParameterArchive", "ParameterRequest", "A request for the latest value of a parameter");

		setClass("parameter");
		addName(parameter);

		arguments.put("sort", "timestamp");
		arguments.put("sortorder", "DESC");
		arguments.put("rows", rows);
	}

	public ParameterRequest(String parameter, Long from, Long to) {
		super("Assembly", "ParameterArchive", "ParameterRequest", "A request for the latest value of a parameter");

		setClass("parameter");
		addName(parameter);

		if (from != null) {
			setFrom(from);
		}
		if (to != null) { 
			setTo(to);
		}

		arguments.put("sort", "timestamp");
		arguments.put("sortorder", "ASC");
	}

	public ParameterRequest(List<String> parameters, Long from, Long to) {
		super("Assembly", "ParameterArchive", "ParameterRequest", "A request for the latest value of a parameter");

		setClass("parameter");
		for (String parameter : parameters) {
			addName(parameter);
		}
		
		if (from != null) {
			setFrom(from);
		}
		if (to != null) { 
			setTo(to);
		}

		arguments.put("sort", "timestamp");
		arguments.put("sortorder", "ASC");
	}
}
