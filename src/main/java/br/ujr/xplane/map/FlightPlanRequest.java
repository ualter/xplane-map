package br.ujr.xplane.map;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class FlightPlanRequest {
	
	private String departure;
	private String[] waypoints;
	private String destination;
	
	public FlightPlanRequest() {
		super();
	}
	public FlightPlanRequest(Map<String,String> parameters) {
		this.departure = parameters.get("departure");
		this.destination = parameters.get("destination");
		
		String wpts = parameters.get("waypoints");
		if ( wpts != null ) {
			waypoints = wpts.split("\\+");
		}
	}
	public FlightPlanRequest(String departure, String[] waypoints, String destination) {
		super();
		this.departure = departure;
		this.waypoints = waypoints;
		this.destination = destination;
	}
	public String getDeparture() {
		return departure;
	}
	public void setDeparture(String departure) {
		this.departure = departure;
	}
	public String[] getWaypoints() {
		return waypoints;
	}
	public void setWaypoints(String[] waypoints) {
		this.waypoints = waypoints;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("departure",departure).append("waypoints",this.waypoints).append("destination",destination).toString();
	}
	
	
	
	
	

}
