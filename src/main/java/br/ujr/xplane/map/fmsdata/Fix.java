package br.ujr.xplane.map.fmsdata;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Fix implements Place {

	private int		index;
	private String	code;
	private float	latitude;
	private float	longitude;

	public Fix(int index, String code, float latitude, float longitude) {
		super();
		this.index = index;
		this.code = code;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Fix(int index, String code, String latitude, String longitude) {
		super();
		this.index = index;
		this.code = code;
		this.latitude = Utils.parseCoord(latitude);
		this.longitude = Utils.parseCoord(longitude);
	}

	public Fix() {
		super();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getKey() {
		return this.code + "-" + this.index;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
		return this.code + " (" + this.latitude + "," + this.longitude + ")";
	}

	public String getName() {
		return this.code;
	}

	public PlaceType getType() {
		return PlaceType.FIX;
	}

	@Override
	public int hashCode() {
		return this.index + this.code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Fix) {
			Fix that = (Fix) obj;
			if ((that.getIndex() == this.index) && (this.getCode().equalsIgnoreCase(that.getCode()))) {
				return true;
			}
		}
		return false;
	}

}
