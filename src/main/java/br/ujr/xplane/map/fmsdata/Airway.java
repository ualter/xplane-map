package br.ujr.xplane.map.fmsdata;

import java.util.LinkedHashMap;
import java.util.Map;

public class Airway {

	private String					code;
	private int                     index;
	private String					number;
	private Map<String, Fix>	fixs	= new LinkedHashMap<String, Fix>();

	public Airway(int index, String code, String number) {
		super();
		this.index = index;
		this.code = code;
		this.number = number;
	}

	public Airway() {
		super();
	}

	public String getKey() {
		return this.code + "-" + this.index;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Map<String, Fix> getWaypoints() {
		return fixs;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.code;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Map<String, Fix> getWayPoints() {
		return fixs;
	}

	public void setWaypoints(Map<String, Fix> fixs) {
		this.fixs = fixs;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(this.getKey());
		s.append(": [");
		StringBuilder waypoints = new StringBuilder();
		for (Fix w : this.fixs.values()) {
			if (waypoints.length() > 0)
				waypoints.append(", ");
			waypoints.append(w.getCode());
		}
		s.append(waypoints);
		s.append("]");
		return s.toString();
	}

	@Override
	public int hashCode() {
		return this.index + this.code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Airway) {
			Airway that = (Airway)obj;
			if ( (that.getIndex() == this.index) && (this.getCode().equalsIgnoreCase(that.getCode())) ) {
				return true;
			}
		}
		return false;
	}
	
	

}
