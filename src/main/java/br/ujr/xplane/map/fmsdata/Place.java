package br.ujr.xplane.map.fmsdata;

public interface Place {
	
	public enum PlaceType {
		AIRPORT, NAVAID, RUNWAY, FIX, AIRWAY
	}
	
	public String getName();
	public PlaceType getType();
	public float getLatitude();
	public float getLongitude();

}
