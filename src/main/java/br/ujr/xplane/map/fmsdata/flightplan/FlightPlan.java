package br.ujr.xplane.map.fmsdata.flightplan;

import java.util.ArrayList;
import java.util.List;

import br.ujr.xplane.map.fmsdata.Airport;
import br.ujr.xplane.map.fmsdata.FMSDataManager;
import br.ujr.xplane.map.fmsdata.Fix;
import br.ujr.xplane.map.fmsdata.Navaid;
import br.ujr.xplane.map.fmsdata.flightplan.Waypoint.WaypointType;

public class FlightPlan {

	private Airport			departure;
	private List<Waypoint>	waypoints	= new ArrayList<Waypoint>();
	private Airport			destination;
	private InfoRoute[]		infoRoute;

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

	public InfoRoute[] getInfoRoute() {
		this.calculateInfoRoute();
		return infoRoute;
	}

	public void calculateInfoRoute() {
		String currentPoint = this.departure.getIcaoId();
		double latitude1 = this.departure.getLatitude();
		double longitude1 = this.departure.getLongitude();
		String nextPoint = "";
		double latitude2 = 0;
		double longitude2 = 0;
		double distance = 0;
		int bearing;

		List<InfoRoute> listaInfoRoutes = new ArrayList<InfoRoute>();

		for (Waypoint w : this.waypoints) {
			InfoRoute infoRoute = new InfoRoute();
			nextPoint = w.getId();
			latitude2 = w.getLatitude();
			longitude2 = w.getLongitude();
			distance = calculateDistance(latitude1, longitude1, latitude2, longitude2);
			bearing = calculateBearing(latitude1, longitude1, latitude2, longitude2);
			infoRoute.setLatitude((float)latitude1);
			infoRoute.setLongitude((float)longitude1);
			infoRoute.setDistance(formatDistance(distance));
			infoRoute.setCurrentPoint(currentPoint);
			infoRoute.setNextPoint(nextPoint);
			infoRoute.setBearing(formatBearing(bearing));
			listaInfoRoutes.add(infoRoute);
			currentPoint = nextPoint;
			latitude1 = latitude2;
			longitude1 = longitude2;

		}

		latitude2 = this.destination.getLatitude();
		longitude2 = this.destination.getLongitude();
		nextPoint = this.destination.getIcaoId();
		distance = calculateDistance(latitude1, longitude1, latitude2, longitude2);
		bearing = calculateBearing(latitude1, longitude1, latitude2, longitude2);
		InfoRoute infoRoute = new InfoRoute();
		infoRoute.setLatitude((float)latitude1);
		infoRoute.setLongitude((float)longitude1);
		infoRoute.setDistance(formatDistance(distance));
		infoRoute.setCurrentPoint(currentPoint);
		infoRoute.setNextPoint(nextPoint);
		infoRoute.setBearing(formatBearing(bearing));
		listaInfoRoutes.add(infoRoute);
		
		this.infoRoute = new InfoRoute[listaInfoRoutes.size()];
		listaInfoRoutes.toArray(this.infoRoute);
	}

	private String formatDistance(double d) {
		return ((int) d) + "nm";
	}

	private String formatBearing(int b) {
		return b + "d";
	}

	public void setInfoRoute(InfoRoute[] infoRoute) {
		this.infoRoute = infoRoute;
	}

	public void addNavaid(Navaid navaid) {

		WaypointType wtype = null;
		switch (navaid.getNavaidType()) {
			case VOR: {
				wtype = WaypointType.VOR;
				break;
			}
			case NDB: {
				wtype = WaypointType.NDB;
				break;
			}

		}
		Waypoint w = new Waypoint(navaid.getCode(), navaid.getLatitude(), navaid.getLongitude(), wtype, navaid.getFrequency(), navaid.getName());
		this.waypoints.add(w);
	}

	public void addFix(Fix fix) {
		Waypoint w = new Waypoint(fix.getCode(), fix.getLatitude(), fix.getLongitude());
		this.waypoints.add(w);
	}

	public boolean isValid() {
		return true;
	}

	public static void main(String[] args) {
		FMSDataManager fmsDataManager = new FMSDataManager();

		Airport sp = fmsDataManager.getAirports().get("SBSP");
		Airport rj = fmsDataManager.getAirports().get("SBRJ");
		Navaid tbe = fmsDataManager.getNavaids().get("TBE-0");
		Fix lodog = fmsDataManager.getFixes().get("LODOG-0");
		Fix vurep = fmsDataManager.getFixes().get("VUREP-0");
		Fix sidur = fmsDataManager.getFixes().get("SIDUR-0");

		FlightPlan fp = new FlightPlan();
		fp.setDeparture(sp);
		fp.setDestination(rj);
		fp.addNavaid(tbe);
		fp.addFix(lodog);
		fp.addFix(vurep);
		fp.addFix(sidur);
		
		fp.calculateInfoRoute();

		InfoRoute[] route = fp.getInfoRoute();

		for (InfoRoute r : route) {
			System.out.println(r);
		}

	}

	private double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
		// Distance in Kilometers
		// double EARTH_RAD = 6372.8;
		// Distance in Nautical Miles
		double EARTH_RAD = 3440;
		double dLat = rad(latitude2 - latitude1);
		double dLong = rad(longitude2 - longitude1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(latitude1)) * Math.cos(rad(latitude2)) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = EARTH_RAD * c;
		return d;

	}

	private double rad(double d) {
		return d * Math.PI / 180;
	}

	private int calculateBearing(double lat1, double lon1, double lat2, double lon2) {
		int degree = 180;
		
		lon1 = lon1 * Math.PI / degree; 
		lon2 = lon2 * Math.PI / degree; 
		lat1 = lat1 * Math.PI / degree; 
		lat2 = lat2 * Math.PI / degree;
		 
		double y = Math.sin(lon2 - lon1) * Math.cos(lat2); 
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1); 
		double brng = Math.atan2(y, x);
		 
		double result = brng / Math.PI * degree; 
		System.out.println(result);
		return (int) (result + 22);
		 

		/*double longitude1 = lon1;
		double longitude2 = lon2;
		double latitude1 = Math.toRadians(lat1);
		double latitude2 = Math.toRadians(lat2);
		double longDiff = Math.toRadians(longitude2 - longitude1);
		double y = Math.sin(longDiff) * Math.cos(latitude2);
		double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

		double result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
		System.out.println(result);
		return (int)result;*/

	}

}
