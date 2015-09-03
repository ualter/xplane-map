package br.ujr.xplane.map;

import java.net.InetAddress;
import java.util.HashMap;

import org.json.simple.JSONObject;

import br.ujr.xplane.comm.message.DATAMessage;

public class PlanesList {

	private HashMap<String, Float>	latMap			= new HashMap<String, Float>();
	private HashMap<String, Float>	lonMap			= new HashMap<String, Float>();
	private HashMap<String, Float>	latAnteriorMap	= new HashMap<String, Float>();
	private HashMap<String, Float>	lonAnteriorMap	= new HashMap<String, Float>();
	private HashMap<String, Float>	altMap			= new HashMap<String, Float>();
	private HashMap<String, Float>	airSpeed		= new HashMap<String, Float>();
	private HashMap<String, Float>	vSpeed			= new HashMap<String, Float>();
	private HashMap<String, Float>	groundSpeed		= new HashMap<String, Float>();
	private HashMap<String, Float>	bearing			= new HashMap<String, Float>();

	public HashMap<String, Float> getLatMap() {
		return this.latMap;
	}

	public HashMap<String, Float> getLonMap() {
		return this.lonMap;
	}

	public HashMap<String, Float> getAltMap() {
		return this.altMap;
	}

	public boolean hasPlane(InetAddress ip) {
		return (this.latMap.containsKey(ip.toString())) && (this.lonMap.containsKey(ip.toString()));
	}

	public void setPlaneLat(InetAddress ip, float lat) {
		this.latMap.put(ip.toString(), Float.valueOf(lat));
	}

	public void setPlaneLon(InetAddress ip, float lon) {
		this.lonMap.put(ip.toString(), Float.valueOf(lon));
	}

	public void setPlaneAlt(InetAddress ip, float alt) {
		this.altMap.put(ip.toString(), Float.valueOf(alt));
	}

	public float[] getPlaneCoordinates(InetAddress ip) throws Exception {
		float[] result = new float[3];
		if (hasPlane(ip)) {
			result[0] = getPlaneLat(ip);
			result[1] = getPlaneLon(ip);
			result[2] = getPlaneAlt(ip);
		} else {
			throw new Exception("Plane ip " + ip.toString() + "does not exist");
		}
		return result;
	}

	public float getPlaneLat(InetAddress ip) {
		return this.latMap.get(ip.toString()).floatValue();
	}

	public float getPlaneLon(InetAddress ip) {
		return this.lonMap.get(ip.toString()).floatValue();
	}

	public float getPlaneAlt(InetAddress ip) {
		return this.altMap.get(ip.toString()).floatValue();
	}

	public Float getPlaneHSpeed(String ip) {
		return this.airSpeed.get(ip);
	}

	public Float getPlaneVSpeed(String ip) {
		return this.vSpeed.get(ip);
	}

	public HashMap<String, Float> getGroundSpeed() {
		return groundSpeed;
	}

	public void setGroundSpeed(HashMap<String, Float> groundSpeed) {
		this.groundSpeed = groundSpeed;
	}

	public HashMap<String, Float> getBearing() {
		return bearing;
	}

	public void setBearing(HashMap<String, Float> bearing) {
		this.bearing = bearing;
	}
	
	public HashMap<String, Float> getLatAnteriorMap() {
		return latAnteriorMap;
	}

	public void setLatAnteriorMap(HashMap<String, Float> latAnteriorMap) {
		this.latAnteriorMap = latAnteriorMap;
	}

	public HashMap<String, Float> getLonAnteriorMap() {
		return lonAnteriorMap;
	}

	public void setLonAnteriorMap(HashMap<String, Float> lonAnteriorMap) {
		this.lonAnteriorMap = lonAnteriorMap;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject planes = new JSONObject();
		for (String ip : this.getLatMap().keySet()) {
			JSONObject data = new JSONObject();
			data.put("lat", this.getLatMap().get(ip));
			data.put("lon", this.getLonMap().get(ip));
			data.put("alt", this.getAltMap().get(ip));
			data.put("airSpeed", this.getPlaneHSpeed(ip));
			data.put("vSpeed", this.getPlaneVSpeed(ip));
			data.put("gSpeed", this.getGroundSpeed().get(ip));
			data.put("bearing", this.getBearing().get(ip));
			planes.put(ip.replace('.', '-').substring(1), data);
		}
		return planes;
	}

	// Update from Data Output
	public void updateClimbStatus(InetAddress ip, DATAMessage message) {
		this.airSpeed.put(ip.toString(), new Float(message.getRxData()[0]));
		this.vSpeed.put(ip.toString(), new Float(message.getRxData()[1]));
	}

	// Update from Data Plugin
	public void updateVerticalSpeed(InetAddress ip, String verticalSpeed) {
		this.vSpeed.put(ip.toString(), new Float(verticalSpeed));
	}

	// Update from Data Output
	public void updateLatitudeLongitude(InetAddress ip, DATAMessage message) {
		this.latMap.put(ip.toString(), new Float(message.getRxData()[0]));
		this.lonMap.put(ip.toString(), new Float(message.getRxData()[1]));
		
		float lat1 = this.getLatAnteriorMap().get(ip);
		float lon1 = this.getLonAnteriorMap().get(ip);
		this.calculateBearing(ip, lat1, lon1, 
				new Float(message.getRxData()[0]), new Float(message.getRxData()[1]));
	}

	// Update from Plugin
	public void updateLatitudeLongitude(InetAddress ip, String latitude, String longitude) {
		this.latMap.put(ip.toString(), new Float(latitude));
		this.lonMap.put(ip.toString(), new Float(longitude));
		
		float lat1 = this.getLatAnteriorMap().containsKey(ip.toString()) ? this.getLatAnteriorMap().get(ip.toString()) : 0;
		float lon1 = this.getLonAnteriorMap().containsKey(ip.toString()) ? this.getLonAnteriorMap().get(ip.toString()) : 0;
		this.calculateBearing(ip, lat1, lon1, new Float(latitude), new Float(longitude));
		
		this.latAnteriorMap.put(ip.toString(), new Float(latitude));
		this.lonAnteriorMap.put(ip.toString(), new Float(longitude));
	}

	// Update from Data Output
	public void updateAltitude(InetAddress ip, DATAMessage message) {
		this.altMap.put(ip.toString(), new Float(message.getRxData()[2]));
	}

	// Update from Plugin
	public void updateAltitude(InetAddress ip, String altitude) {
		this.altMap.put(ip.toString(), new Float(altitude));
	}

	// Update from Plugin
	public void updateGroundSpeed(InetAddress ip, String groundSpeed) {
		float gs = (float) (((Float.parseFloat(groundSpeed) * 3600) / 1.852) / 1000);
		this.groundSpeed.put(ip.toString(), new Float(gs));
	}

	// Update from Plugin
	public void updateAirspeed(InetAddress ip, String airspeed) {
		this.airSpeed.put(ip.toString(), new Float(airspeed));
	}

	private void calculateBearing(InetAddress ip, double lat1, double lon1, double lat2, double lon2) {
		double longitude1 = lon1;
		double longitude2 = lon2;
		double latitude1 = Math.toRadians(lat1);
		double latitude2 = Math.toRadians(lat2);
		double longDiff = Math.toRadians(longitude2 - longitude1);
		double y = Math.sin(longDiff) * Math.cos(latitude2);
		double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

		int result = (int) ((Math.toDegrees(Math.atan2(y, x)) + 360) % 360);
		if ( result > 0 ) {
			this.bearing.put(ip.toString(), new Float(result));
		}
	}
}
