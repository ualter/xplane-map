package br.ujr.xplane.map.fmsdata.flightplan;

import java.util.ArrayList;
import java.util.List;

import br.ujr.xplane.map.fmsdata.Airport;
import br.ujr.xplane.map.fmsdata.Fix;
import br.ujr.xplane.map.fmsdata.Navaid;
import br.ujr.xplane.map.fmsdata.flightplan.Waypoint.WaypointType;

public class FlightPlan {

	private Airport			departure;
	private List<Waypoint>	waypoints	= new ArrayList<Waypoint>();
	private Airport			destination;

	public FlightPlan() {
		super();
	}

	public FlightPlan(Airport departure, List<Waypoint> waypoints, Airport destination) {
		super();
		this.departure = departure;
		this.waypoints = waypoints;
		this.destination = destination;
	}

	public Airport getDeparture() {
		return departure;
	}

	public void setDeparture(Airport departure) {
		this.departure = departure;
	}

	public List<Waypoint> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}

	public Airport getDestination() {
		return destination;
	}

	public void setDestination(Airport destination) {
		this.destination = destination;
	}
	
	public void addNavaid(Navaid navaid) {
		WaypointType wtype = null;
		switch(navaid.getNavaidType()) {
			case VOR: {
				wtype = WaypointType.VOR;
				break;
			}
			case NDB: {
				wtype = WaypointType.NDB;
				break;
			}
			
		}
		Waypoint w = new Waypoint(navaid.getCode(), navaid.getLatitude(),
				navaid.getLongitude(), wtype, navaid.getFrequency(),navaid.getName());
		this.waypoints.add(w);
	}
	
	public void addFix(Fix fix) {
		Waypoint w = new Waypoint(fix.getCode(), fix.getLatitude(), fix.getLongitude());
		this.waypoints.add(w);
	}
	
	public boolean isValid() {
		if ( this.departure == null ) return false;
		if ( this.destination == null ) return false;
		return true;
	}

}
