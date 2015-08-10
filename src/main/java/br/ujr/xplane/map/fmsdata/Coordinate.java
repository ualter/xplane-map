package br.ujr.xplane.map.fmsdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Coordinate {
	
	private float latitude;
	private float longitude;
	private List<Place> places = new ArrayList<Place>();
	
	public Coordinate(float latitude, float longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Coordinate(Place place) {
		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
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
	
	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	@Override
	public int hashCode() {
		return this.convertCoordinateToInt();
	}
	
	public int convertCoordinateToInt() {
		String strLatitude = String.valueOf(this.latitude).replaceAll("\\.", "");
		String strLongitude = String.valueOf(this.longitude).replaceAll("\\.", "");
		
		return Integer.parseInt(strLatitude) + Integer.parseInt(strLongitude);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coordinate ) {
			Coordinate that = (Coordinate)obj;
			if ( (that.getLatitude() == this.getLatitude()) && (that.getLongitude() == this.getLongitude()) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE).
				append("latitude",latitude).
				append("longitude", longitude).
				toString();
	}
	

}
