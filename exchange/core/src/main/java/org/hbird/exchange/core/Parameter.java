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
package org.hbird.exchange.core;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A NUMERICAL parameter. The parameter type is at the core of the information model. It 
 * is used to describe a name-value pair, attaching the meta-data for description 
 * and unit.
 * 
 * There is no predefined set of parameters in the form of a database. Creating
 * a new parameter is simply the creation of a Parameter with a new name.   
 *
 */
public class Parameter extends Named implements Comparable<Parameter> {

	/** The unique UID. */
	private static final long serialVersionUID = 889400984561961325L;
	
	/** The value of the parameter. May be any type. */
	protected Number value;

	/** The unit of the argument. */
	protected String unit;

	/**
	 * Creates a Parameter with a timestamp set to 'now'.
	 * 
	 * @param name The name of the parameter
	 * @param description A description of the parameter.
	 * @param value An object holding the value.
	 * @param unit The unit of the value.
	 */
	public Parameter(String issuedBy, String name, String type, String description, Number value, String unit) {
		super(issuedBy, name, "Parameter", description);
		this.unit = unit;
		this.value = value;
	}

	public Parameter(Parameter base) {
		this(base.issuedBy, base.name, base.type, base.description, base.value, base.unit);
	}
	
	/**
	 * Creates a Parameter with a timestamp set to 'now'.
	 * 
	 * @param name The name of the parameter
	 * @param description A description of the parameter.
	 * @param value An object holding the value.
	 * @param unit The unit of the value.
	 */
	public Parameter(String issuedBy, String name, String type, String description, Number value, String unit, long timestamp) {
		super(issuedBy, name, "Parameter", description, timestamp);
		this.unit = unit;
		this.value = value;
	}

	/**
	 * Constructor of the parameter.
	 * 
	 * @param name The name of the parameter
	 * @param description A description of the parameter.
	 * @param timestamp The timestamp of the parameter.
	 * @param value An object holding the value.
	 * @param unit The unit of the value.
	 */
	public Parameter(String issuedBy, String name, String type, String description, long timestamp, Number value, String unit) {
		super(issuedBy, name, "Parameter", description, timestamp);
		this.unit = unit;
		this.value = value;
	}
	
	public Parameter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the value of the Parameter as an Object. 
	 * 
	 * @return An Object holding the value. The class type can of cause be found using 
	 * reflection, or through the 'clazz' attribute.
	 */
	public Number getValue() {
		return value;
	}
	
	public double asDouble() {
		return value.doubleValue();
	}

	public int asInt() {
		return value.intValue();
	}

	/**
	 * Setter for the value. Will also set the 'clazz' attribute to the Java name of the
	 * class set as value.
	 * 
	 * @param value The value to be set.
	 */
	public void setValue(Number value) {
		this.value = value;
	}
	
	/**
	 * Returns the string representing the unit of this parameter.
	 * 
	 * @return STring representing the unit.
	 */
	public String getUnit() {
		return unit;
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * Notice that the method has extra been implemented to allow comparison of types that are
	 * not the same, i.e. Integer and Double.
	 */
	public int compareTo(Parameter rhs) {

        if(this.value instanceof Short && rhs.value instanceof Short)
        {
            return ((Short) this.value).compareTo((Short) rhs.value);
        }
        else if(this.value instanceof Long && rhs.value instanceof Long)
        {
            return ((Long) this.value).compareTo((Long) rhs.value);
        }
        else if(this.value instanceof Integer && rhs.value instanceof Integer)
        {
            return ((Integer) this.value).compareTo((Integer) rhs.value);
        }
        else if(this.value instanceof Float && rhs.value instanceof Float)
        {
            return ((Float) this.value).compareTo((Float) rhs.value);
        }
        else if(this.value instanceof Double && rhs.value instanceof Double)
        {
            return ((Double) this.value).compareTo((Double) rhs.value);
        }
        else if(this.value instanceof Byte && rhs.value instanceof Byte)
        {
            return ((Byte) this.value).compareTo((Byte) rhs.value);
        }
        else if(this.value instanceof BigInteger && rhs.value instanceof BigInteger)
        {
            return ((BigInteger) this.value).compareTo((BigInteger) rhs.value);
        }
        else if(this.value instanceof BigDecimal && rhs.value instanceof BigDecimal)
        {
            return ((BigDecimal) this.value).compareTo((BigDecimal) rhs.value);
        }
        else
        {
            throw new RuntimeException("Ooopps!");
        }
	}

	public String prettyPrint() {
		return "Parameter {name=" + name + ", value=" + value.toString() + ", timestamp=" + timestamp + "}";
	}
}
