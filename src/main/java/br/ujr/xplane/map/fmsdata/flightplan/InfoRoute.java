package br.ujr.xplane.map.fmsdata.flightplan;

public class InfoRoute {

	private String	nextPoint;
	private String	currentPoint;
	private String	distance;
	private String	bearing;
	private float   latitude;
	private float   longitude;

	public InfoRoute() {
		super();
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getBearing() {
		return bearing;
	}

	public void setBearing(String bearing) {
		this.bearing = bearing;
	}

	public String getNextPoint() {
		return nextPoint;
	}

	public void setNextPoint(String nextPoint) {
		this.nextPoint = nextPoint;
	}

	public String getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(String currentPoint) {
		this.currentPoint = currentPoint;
	}
	
	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.currentPoint);
		sb.append(" ");
		sb.append(this.bearing);
		sb.append(" / ");
		sb.append(this.distance);
		sb.append(" ");
		sb.append(nextPoint);
		return sb.toString();
	}
	
	

}
