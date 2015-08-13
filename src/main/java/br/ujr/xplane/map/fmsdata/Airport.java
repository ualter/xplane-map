package br.ujr.xplane.map.fmsdata;

import java.util.HashMap;
import java.util.Map;

public class Airport implements Place {

	private String				icaoId;
	private String				name;
	private float				latitude;
	private float				longitude;
	private Map<String, Runway>	runways	= new HashMap<String, Runway>();
	private Airport.Runway[]	arrayRunways;

	public Airport() {
		super();
	}

	public Airport(String icaoId, String name, float latitude, float longitude, Map<String, Runway> runways) {
		super();
		this.icaoId = icaoId;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.runways = runways;
	}

	public String getIcaoId() {
		return icaoId;
	}

	public void setIcaoId(String icaoId) {
		this.icaoId = icaoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = Utils.parseCoord(latitude);
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = Utils.parseCoord(longitude);
	}

	public Map<String, Runway> getRunways() {
		return runways;
	}

	public void setRunways(Map<String, Runway> runways) {
		this.runways = runways;
	}

	public Airport.Runway[] getArrayRunways() {
		this.arrayRunways = new Airport.Runway[runways.size()];
		runways.values().toArray(this.arrayRunways);
		return arrayRunways;
	}

	public void setArrayRunways(Airport.Runway[] arrayRunways) {
		this.arrayRunways = arrayRunways;
	}

	public String listRunways() {
		StringBuilder sb = new StringBuilder();
		for (Runway r : this.runways.values()) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(r.getNumber());
		}
		return sb.toString();
	}

	public static class Runway implements Place {

		private String	number;
		private int		heading;
		private int		length;
		private float	latitude;
		private float	longitude;
		private String	frequency;
		private int		elevation;

		public Runway() {
			super();
		}

		public Runway(String number, int heading, int length, String frequency, int elevation) {
			super();
			this.number = number;
			this.heading = heading;
			this.length = length;
			this.frequency = frequency;
			this.elevation = elevation;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public int getHeading() {
			return heading;
		}

		public void setHeading(int heading) {
			this.heading = heading;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public String getFrequency() {
			return frequency;
		}

		public void setFrequency(String frequency) {
			this.frequency = frequency;
		}

		public int getElevation() {
			return elevation;
		}

		public void setElevation(int elevation) {
			this.elevation = elevation;
		}

		public float getLatitude() {
			return latitude;
		}

		public void setLatitude(float latitude) {
			this.latitude = latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = Utils.parseCoord(latitude);
		}

		public float getLongitude() {
			return longitude;
		}

		public void setLongitude(float longitude) {
			this.longitude = longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = Utils.parseCoord(longitude);
		}

		public String getName() {
			return "RWY " + this.getNumber();
		}

		public PlaceType getType() {
			return PlaceType.RUNWAY;
		}

	}

	public PlaceType getType() {
		return PlaceType.AIRPORT;
	}

}
