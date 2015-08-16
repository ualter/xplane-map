package br.ujr.xplane.map.fmsdata.flightplan;

public class InfoRoute {

	private String	nextPoint;
	private String	currentPoint;
	private String	distance;
	private int     distanceNM;
	private String	bearing;
	private int 	bearingDegree;
	private float	latitude;
	private float	longitude;
	private float	nextLatitude;
	private float	nextLongitude;
	private String  compassHeading;

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

	public float getNextLatitude() {
		return nextLatitude;
	}

	public void setNextLatitude(float nextLatitude) {
		this.nextLatitude = nextLatitude;
	}

	public float getNextLongitude() {
		return nextLongitude;
	}

	public void setNextLongitude(float nextLongitude) {
		this.nextLongitude = nextLongitude;
	}
	
	public int getDistanceNM() {
		return distanceNM;
	}

	public void setDistanceNM(int distanceNM) {
		this.distanceNM = distanceNM;
	}
	
	
	public String getCompassHeading() {
		return compassHeading;
	}

	public void setCompassHeading(String compassHeading) {
		this.compassHeading = compassHeading;
	}

	public int getBearingDegree() {
		return bearingDegree;
	}

	public void setBearingDegree(int bearingDegree) {
		this.bearingDegree = bearingDegree;
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
