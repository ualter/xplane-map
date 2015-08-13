package br.ujr.xplane.map.fmsdata.flightplan;

public class Waypoint {

	public enum WaypointType {
		VOR, NDB
	}

	private String			id;
	private float			latitude;
	private float			longitude;
	private WaypointType	type;
	private String			frequency;
	private String			name;

	public Waypoint(String id, float latitude, float longitude, WaypointType type, String frequency, String name) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
		this.frequency = frequency;
		this.name = name;
	}

	public Waypoint(String id, float latitude, float longitude) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public WaypointType getType() {
		return type;
	}

	public void setType(WaypointType type) {
		this.type = type;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
