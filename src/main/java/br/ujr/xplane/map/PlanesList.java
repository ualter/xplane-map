package br.ujr.xplane.map;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import br.ujr.xplane.comm.message.DATAMessage;

public class PlanesList {

	private HashMap<String, Float>	latMap				= new HashMap<String, Float>();
	private HashMap<String, Float>	lonMap				= new HashMap<String, Float>();
	private HashMap<String, Float>	latAnteriorMap		= new HashMap<String, Float>();
	private HashMap<String, Float>	lonAnteriorMap		= new HashMap<String, Float>();
	private HashMap<String, Float>	altMap				= new HashMap<String, Float>();
	private HashMap<String, Float>	airSpeed			= new HashMap<String, Float>();
	private HashMap<String, Float>	vSpeed				= new HashMap<String, Float>();
	private HashMap<String, Float>	groundSpeed			= new HashMap<String, Float>();
	private HashMap<String, Float>	bearing				= new HashMap<String, Float>();
	private HashMap<String, String>	destination			= new HashMap<String, String>();
	private HashMap<String, String>	gpsDistanceNm		= new HashMap<String, String>();
	private HashMap<String, String>	gpsDistanceTime		= new HashMap<String, String>();
	private HashMap<String, String>	barometer			= new HashMap<String, String>();
	private HashMap<String, String>	fuelQuantity		= new HashMap<String, String>();
	private HashMap<String, String>	nav1Freq			= new HashMap<String, String>();
	private HashMap<String, String>	nav2Freq			= new HashMap<String, String>();
	private HashMap<String, String>	pause				= new HashMap<String, String>();
	private HashMap<String, String>	compassHeading		= new HashMap<String, String>();
	private HashMap<String, String>	outsideTemperature	= new HashMap<String, String>();
	private HashMap<String, String>	com1Freq			= new HashMap<String, String>();
	private HashMap<String, String>	com2Freq			= new HashMap<String, String>();
	private DecimalFormat			df					= new DecimalFormat("00");

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

	public HashMap<String, Float> getAirSpeed() {
		return airSpeed;
	}

	public void setAirSpeed(HashMap<String, Float> airSpeed) {
		this.airSpeed = airSpeed;
	}

	public HashMap<String, Float> getvSpeed() {
		return vSpeed;
	}

	public void setvSpeed(HashMap<String, Float> vSpeed) {
		this.vSpeed = vSpeed;
	}

	public HashMap<String, String> getGpsDistanceNm() {
		return gpsDistanceNm;
	}

	public void setGpsDistanceNm(HashMap<String, String> gpsDistanceNm) {
		this.gpsDistanceNm = gpsDistanceNm;
	}

	public HashMap<String, String> getGpsDistanceTime() {
		return gpsDistanceTime;
	}

	public void setGpsDistanceTime(HashMap<String, String> gpsDistanceTime) {
		this.gpsDistanceTime = gpsDistanceTime;
	}

	public HashMap<String, String> getBarometer() {
		return barometer;
	}

	public void setBarometer(HashMap<String, String> barometer) {
		this.barometer = barometer;
	}

	public HashMap<String, String> getFuelQuantity() {
		return fuelQuantity;
	}

	public void setFuelQuantity(HashMap<String, String> fuelQuantity) {
		this.fuelQuantity = fuelQuantity;
	}

	public HashMap<String, String> getNav1Freq() {
		return nav1Freq;
	}

	public void setNav1Freq(HashMap<String, String> nav1Freq) {
		this.nav1Freq = nav1Freq;
	}

	public HashMap<String, String> getNav2Freq() {
		return nav2Freq;
	}

	public void setNav2Freq(HashMap<String, String> nav2Freq) {
		this.nav2Freq = nav2Freq;
	}

	public void setLatMap(HashMap<String, Float> latMap) {
		this.latMap = latMap;
	}

	public void setLonMap(HashMap<String, Float> lonMap) {
		this.lonMap = lonMap;
	}

	public void setAltMap(HashMap<String, Float> altMap) {
		this.altMap = altMap;
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

	public HashMap<String, String> getDestination() {
		return destination;
	}

	public void setDestination(HashMap<String, String> destination) {
		this.destination = destination;
	}

	public HashMap<String, String> getPause() {
		return pause;
	}

	public void setPause(HashMap<String, String> pause) {
		this.pause = pause;
	}

	public HashMap<String, String> getCompassHeading() {
		return compassHeading;
	}

	public void setCompassHeading(HashMap<String, String> compassHeading) {
		this.compassHeading = compassHeading;
	}

	public HashMap<String, String> getOutsideTemperature() {
		return outsideTemperature;
	}

	public void setOutsideTemperature(HashMap<String, String> outsideTemperature) {
		this.outsideTemperature = outsideTemperature;
	}

	public HashMap<String, String> getCom1Freq() {
		return com1Freq;
	}

	public void setCom1Freq(HashMap<String, String> com1Freq) {
		this.com1Freq = com1Freq;
	}

	public HashMap<String, String> getCom2Freq() {
		return com2Freq;
	}

	public void setCom2Freq(HashMap<String, String> com2Freq) {
		this.com2Freq = com2Freq;
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
			data.put("dest", this.getDestination().get(ip));
			data.put("gpsDistNm", this.getGpsDistanceNm().get(ip));
			data.put("gpsDistTime", this.getGpsDistanceTime().get(ip));
			data.put("nav1Freq", this.getNav1Freq().get(ip));
			data.put("nav2Freq", this.getNav2Freq().get(ip));
			data.put("barometer", this.getBarometer().get(ip));
			data.put("compassHeading", this.getCompassHeading().get(ip));
			data.put("pause", this.getPause().get(ip));
			data.put("fuelQuantity", this.getFuelQuantity().get(ip));
			data.put("outsideTemp", this.getOutsideTemperature().get(ip));
			data.put("com1Freq", this.getCom1Freq().get(ip));
			data.put("com2Freq", this.getCom2Freq().get(ip));
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
		this.calculateBearing(ip, lat1, lon1, new Float(message.getRxData()[0]), new Float(message.getRxData()[1]));
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

	// Update from Plugin
	public void updateDestination(InetAddress ip, String destination) {
		this.destination.put(ip.toString(), destination);
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
		if (result > 0) {
			this.bearing.put(ip.toString(), new Float(result));
		}
	}

	public void updateGpsDistanceMeters(InetAddress ip, String value) {
		Float distanceMeters = new Float(value);
		// float distanceNm = distanceMeters * 0.00053995680345572f;
		DecimalFormat df = new DecimalFormat("#0.0");
		this.gpsDistanceNm.put(ip.toString(), df.format(distanceMeters));
	}

	/*
	 * Actually is MINUTES not Seconds
	 */
	public void updateGpsDistanceSeconds(InetAddress ip, String value) {
		float distanceMinutes = 0;
		try {
			distanceMinutes = Float.parseFloat(value);
		} catch (NumberFormatException e) {
		}

		if (distanceMinutes > 0) {
			int iDistanceMinutes = (int) distanceMinutes;
			int iDistanceSeconds = (int) ((distanceMinutes - iDistanceMinutes) * 60);
			int iDistanceHours = (int) (distanceMinutes / 60);
			String result = df.format(iDistanceHours) + ":" + df.format(iDistanceMinutes) + ":" + df.format(iDistanceSeconds);
			this.gpsDistanceTime.put(ip.toString(), result);
		} else {
			this.gpsDistanceTime.put(ip.toString(), "0");
		}

	}

	public static void main(String[] args) {
		System.out.println(String.format("%.0f", new Float("378.99")));
	}

	public void updateFuelQuantity(InetAddress ip, String value) {
		this.fuelQuantity.put(ip.toString(), String.format("%.1f", new Float(value)));
	}

	public void updateNav2Frequency(InetAddress ip, String value) {
		if (value.length() >= 5) {
			StringBuffer sb = new StringBuffer(value);
			sb.insert(value.length() - 2, ".");
			this.nav2Freq.put(ip.toString(), sb.toString());
		} else {
			this.nav2Freq.put(ip.toString(), value);
		}
	}

	public void updateNav1Frequency(InetAddress ip, String value) {
		if (value.length() >= 5) {
			StringBuffer sb = new StringBuffer(value);
			sb.insert(value.length() - 2, ".");
			this.nav1Freq.put(ip.toString(), sb.toString());
		} else {
			this.nav1Freq.put(ip.toString(), value);
		}
	}

	public void updatePause(InetAddress ip, String value) {
		this.pause.put(ip.toString(), value);
	}

	public void updateBarometer(InetAddress ip, String value) {
		this.barometer.put(ip.toString(), value.replaceAll("\\.", ""));
	}

	public void updateCompassHeading(InetAddress ip, String value) {
		this.compassHeading.put(ip.toString(), String.valueOf(Math.round(new Float(value))));
	}

	public void updateOutsideTemparature(InetAddress ip, String value) {
		this.outsideTemperature.put(ip.toString(), String.format("%.1f", new Float(value)));

	}

	public void updateCom1Freq(InetAddress ip, String value) {
		if (value.length() >= 5) {
			StringBuffer sb = new StringBuffer(value);
			sb.insert(value.length() - 2, ".");
			this.com1Freq.put(ip.toString(), sb.toString());
		} else {
			this.com1Freq.put(ip.toString(), value);
		}
	}

	public void updateCom2Freq(InetAddress ip, String value) {
		if (value.length() >= 5) {
			StringBuffer sb = new StringBuffer(value);
			sb.insert(value.length() - 2, ".");
			this.com2Freq.put(ip.toString(), sb.toString());
		} else {
			this.com2Freq.put(ip.toString(), value);
		}
	}
}
