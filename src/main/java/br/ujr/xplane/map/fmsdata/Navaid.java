package br.ujr.xplane.map.fmsdata;

public class Navaid implements Place {
	
	public enum NavaidType {
		VOR, NDB
	}

	private String	code;
	private String	description;
	private String	frequency;
	private float	longitude;
	private float	latitude;
	private NavaidType navaidType;

	public Navaid() {
		super();
	}

	public Navaid(String code, String description, String frequency, float longitude, float latitude, NavaidType navaidType) {
		super();
		this.code = code;
		this.description = description;
		this.frequency = frequency;
		this.longitude = longitude;
		this.latitude = latitude;
		this.navaidType = navaidType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public String getName() {
		return this.getDescription();
	}

	public PlaceType getType() {
		return PlaceType.NAVAID;
	}

	public NavaidType getNavaidType() {
		return navaidType;
	}

	public void setNavaidType(NavaidType navaidType) {
		this.navaidType = navaidType;
	}

}
