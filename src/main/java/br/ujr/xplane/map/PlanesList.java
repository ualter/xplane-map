package br.ujr.xplane.map;

import java.net.InetAddress;
import java.util.HashMap;

import org.json.simple.JSONObject;

import br.ujr.xplane.comm.message.DATAMessage;

public class PlanesList {
	private HashMap<String, Float>	latMap	= new HashMap<String, Float>();
	private HashMap<String, Float>	lonMap	= new HashMap<String, Float>();
	private HashMap<String, Float>	altMap	= new HashMap<String, Float>();

	private HashMap<String, Float>	hSpeed	= new HashMap<String, Float>();
	private HashMap<String, Float>	vSpeed	= new HashMap<String, Float>();

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
		return this.hSpeed.get(ip);
	}
	
	public Float getPlaneVSpeed(String ip) {
		return this.vSpeed.get(ip);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject planes = new JSONObject();
		for (String ip : this.getLatMap().keySet()) {
			JSONObject data = new JSONObject();
			data.put("lat", this.getLatMap().get(ip));
			data.put("lon", this.getLonMap().get(ip));
			data.put("alt", this.getAltMap().get(ip));
			data.put("hSpeed", this.getPlaneHSpeed(ip));
			data.put("vSpeed", this.getPlaneVSpeed(ip));
			planes.put(ip.replace('.', '-').substring(1), data);
		}
		return planes;
	}

	public void updateClimbStatus(InetAddress ip, DATAMessage message) {
		this.hSpeed.put(ip.toString(), new Float(message.getRxData()[0]));
		this.vSpeed.put(ip.toString(), new Float(message.getRxData()[1]));
	}
	
	public void updateLatitudeLongitude(InetAddress ip, DATAMessage message) {
		this.latMap.put(ip.toString(), new Float(message.getRxData()[0]));
		this.lonMap.put(ip.toString(), new Float(message.getRxData()[1]));
	}
	
	public void updateAltitude(InetAddress ip, DATAMessage message) {
		this.altMap.put(ip.toString(), new Float(message.getRxData()[2]));
	}
	public void updateAltitude(InetAddress ip, String altitude) {
		// Converter altitude para Float
		//this.altMap.put(ip.toString(), new Float(message.getRxData()[2]));
	}

}
