package br.ujr.xplane.map.fmsdata;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
		
		if ( strLatitude.contains("E") || strLatitude.contains("e") ) {
			BigDecimal bd = new BigDecimal(strLatitude).setScale(6,RoundingMode.DOWN);
			strLatitude = bd.toPlainString().replaceAll("\\.", "");
		}
		if ( strLongitude.contains("E") || strLongitude.contains("e") ) {
			BigDecimal bd = new BigDecimal(strLongitude).setScale(6,RoundingMode.DOWN);
			strLongitude = bd.toPlainString().replaceAll("\\.", "");
		}
		
		int result = 0;
		try {
			result = Integer.parseInt(strLatitude) + Integer.parseInt(strLongitude);
		} catch (NumberFormatException e) {
			throw e;
		}
		return result;
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
