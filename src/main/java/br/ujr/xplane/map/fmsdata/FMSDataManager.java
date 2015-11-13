package br.ujr.xplane.map.fmsdata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ujr.xplane.map.fmsdata.Navaid.NavaidType;

public class FMSDataManager {

	public static Logger			logger				= LoggerFactory.getLogger(FMSDataManager.class);
	private static String			FIELDS_SEPARATOR	= ",";
	private Map<Coordinate, Place>	coordinates			= new HashMap<Coordinate, Place>();
	private Map<String, Navaid>		navaids				= new HashMap<String, Navaid>();
	private Map<String, Airport>	airports			= new HashMap<String, Airport>();
	private Map<String, Fix>		fixs				= new HashMap<String, Fix>();
	private Map<String, Airway>		airways				= new HashMap<String, Airway>();

	public FMSDataManager() {
		super();
		/*
		 * ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)
		 * org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.
		 * ROOT_LOGGER_NAME); root.setLevel(Level.INFO);
		 */
		init();
	}

	public void init() {
		logger.info("Start load Navdata");
		logger.info("Loading NAVAIDS");
		loadNavaids();
		logger.info("Loading AIRPORTS");
		loadAirports();
		logger.info("Loading WAYPOINTS");
		loadWaypoints();
		logger.info("Loading AIRWAYS");
		loadAirways();
		logger.info("Finished load Navdata");
	}

	private void loadNavaids() {
		BufferedReader fileReader = null;
		try {

			fileReader = Utils.getNavaidSourceReader();
			String line = fileReader.readLine();

			int count = 0;

			while (line != null) {
				String[] columns = line.split(FMSDataManager.FIELDS_SEPARATOR);

				String id = columns[0];
				String desc = columns[1];
				String freq = Utils.parseFreq(columns[2]);
				int type = Integer.parseInt(columns[3]);
				float lat = Utils.parseCoord(columns[6]);
				float lon = Utils.parseCoord(columns[7]);

				NavaidType navaidType = null;
				switch (type) {
					case 1: {
						navaidType = NavaidType.VOR;
						break;
					}
					default: {
						navaidType = NavaidType.NDB;
					}
				}

				Navaid navaid = new Navaid(0, id, desc, freq, lat, lon, navaidType);

				while (this.navaids.containsKey(navaid.getKey())) {
					navaid.setIndex(navaid.getIndex() + 1);
				}

				this.navaids.put(navaid.getKey(), navaid);
				savePlace(navaid);

				count++;
				logger.debug("Loading Navaid: {}", navaid.getKey() + "-" + navaid.getDescription());

				line = fileReader.readLine();
			}

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (fileReader != null)
				try {
					fileReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
		}
	}

	private void loadAirports() {
		BufferedReader fileReader = null;
		try {

			fileReader = Utils.getAirportSourceReader();
			String line = fileReader.readLine();

			int count = 0;
			Airport airport = null;

			while (line != null) {
				if (count > 1) {
					if (StringUtils.isNotBlank(line)) {

						String[] columns = line.split(FMSDataManager.FIELDS_SEPARATOR);
						String type = columns[0];

						if ("A".equalsIgnoreCase(type)) {
							String icaoId = columns[1];
							String name = columns[2];
							String latitude = columns[3];
							String longitude = columns[4];

							airport = new Airport();
							airport.setIcaoId(icaoId);
							airport.setName(name);
							airport.setLatitude(latitude);
							airport.setLongitude(longitude);

						} else if ("R".equalsIgnoreCase(type)) {
							String number = columns[1];
							String heading = columns[2];
							String length = columns[3];
							// 4 = Width
							// 5 = ILS (1 or 0)
							String frequency = columns[6];
							// 7 = CRS (Course Degree)
							String latitude = columns[8];
							String longitude = columns[9];
							String elevation = columns[10];

							Airport.Runway runway = new Airport.Runway();
							runway.setNumber(number);
							runway.setFrequency(Utils.parseFreq(frequency));
							runway.setHeading(Integer.parseInt(heading));
							runway.setLength(Integer.parseInt(length));
							runway.setLongitude(longitude);
							runway.setLatitude(latitude);
							runway.setElevation(Integer.parseInt(elevation));

							airport.getRunways().put(number, runway);
						}
					} else {
						this.airports.put(airport.getIcaoId(), airport);
						savePlace(airport);

						logger.debug("Loading Airport: {}", new Object[] { (airport != null ? airport.getIcaoId() + "[" + airport.listRunways() + "]" : "") });
					}
				}

				count++;
				line = fileReader.readLine();
			}

			this.airports.put(airport.getIcaoId(), airport);
			savePlace(airport);

			logger.debug("Loading Airport: {}", new Object[] { (airport != null ? airport.getIcaoId() + "[" + airport.listRunways() + "]" : "") });

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (fileReader != null)
				try {
					fileReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
		}
	}

	private void loadWaypoints() {
		BufferedReader fileReader = null;
		try {

			fileReader = Utils.getWaypointsSourceReader();
			String line = fileReader.readLine();

			int count = 0;

			while (line != null) {
				String[] columns = line.split(FMSDataManager.FIELDS_SEPARATOR);

				String id = columns[0];
				if (StringUtils.isNotBlank(id)) {
					float lat = Utils.parseCoord(columns[1]);
					float lon = Utils.parseCoord(columns[2]);

					Fix fix = new Fix(0, id, lat, lon);

					while (this.fixs.containsKey(fix.getKey())) {
						fix.setIndex(fix.getIndex() + 1);
					}

					this.fixs.put(fix.getKey(), fix);
					savePlace(fix);

					count++;
					logger.debug("Loading Waypoint: {}", fix);
				}

				line = fileReader.readLine();
			}

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (fileReader != null)
				try {
					fileReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
		}
	}

	private void loadAirways() {
		BufferedReader fileReader = null;
		try {

			fileReader = Utils.getAirwaysSourceReader();
			String line = fileReader.readLine();

			int count = 0;
			Airway airway = null;
			Fix finalWaypoint = null;

			while (line != null) {

				if (StringUtils.isNotBlank(line)) {

					String[] columns = line.split(FMSDataManager.FIELDS_SEPARATOR);
					String type = columns[0];

					if ("A".equalsIgnoreCase(type)) {
						String code = columns[1];
						String number = columns[2];

						airway = new Airway(0, code, number);

					} else if ("S".equalsIgnoreCase(type)) {
						String code = columns[1];
						String latitude = columns[2];
						String longitude = columns[3];
						String code2 = columns[4];
						String latitude2 = columns[5];
						String longitude2 = columns[6];

						Fix fix = new Fix(0, code, latitude, longitude);
						airway.getWayPoints().put(code, fix);
						finalWaypoint = new Fix(0, code2, latitude2, longitude2);
					}
				} else {
					airway.getWayPoints().put(finalWaypoint.getCode(), finalWaypoint);

					while (this.airways.containsKey(airway.getKey())) {
						airway.setIndex(airway.getIndex() + 1);
					}

					this.airways.put(airway.getKey(), airway);
					logger.debug("Loading Airways: {}", new Object[] { (airway != null ? airway.toString() : "") });
				}

				count++;
				line = fileReader.readLine();
			}

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (fileReader != null)
				try {
					fileReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
		}
	}

	public Map<Coordinate, Place> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Map<Coordinate, Place> coordinates) {
		this.coordinates = coordinates;
	}

	public Map<String, Fix> getFixes() {
		return fixs;
	}

	public void setFixes(Map<String, Fix> fixs) {
		this.fixs = fixs;
	}

	public Map<String, Airway> getAirways() {
		return airways;
	}

	public void setAirways(Map<String, Airway> airways) {
		this.airways = airways;
	}

	private void savePlace(Place p) {
		this.coordinates.put(new Coordinate(p), p);
	}

	public Map<String, Navaid> getNavaids() {
		return navaids;
	}

	public void setNavaids(Map<String, Navaid> navaids) {
		this.navaids = navaids;
	}

	public Map<String, Airport> getAirports() {
		return airports;
	}

	public void setAirports(Map<String, Airport> airports) {
		this.airports = airports;
	}

	public void saveCoordinate(Place place) {
		Coordinate coordinate = new Coordinate(place.getLatitude(), place.getLongitude());
		this.coordinates.put(coordinate, place);
	}

	public Map<String, Navaid> searchNavaidStartsWith(String keySearched) {

		Map<String, Navaid> result = new LinkedHashMap<String, Navaid>();

		TreeMap<String, Navaid> subset = new TreeMap<String, Navaid>(this.getNavaids());
		SortedMap<String, Navaid> found = subset.tailMap(keySearched);

		for (String key : found.keySet()) {
			if (key.startsWith(keySearched)) {
				result.put(key, found.get(key));
			} else {
				break;
			}
		}

		return result;
	}

	public Map<String, Fix> searchFixStartsWith(String keySearched) {

		Map<String, Fix> result = new LinkedHashMap<String, Fix>();

		TreeMap<String, Fix> subset = new TreeMap<String, Fix>(this.getFixes());
		SortedMap<String, Fix> found = subset.tailMap(keySearched);

		for (String key : found.keySet()) {
			if (key.startsWith(keySearched)) {
				result.put(key, found.get(key));
			} else {
				break;
			}
		}

		return result;
	}

	public static void main(String[] args) throws Exception {

		long timeIn = System.currentTimeMillis();

		FMSDataManager fmsDataManager = new FMSDataManager();

		Airport sp = fmsDataManager.getAirports().get("SBSP");
		Airport rj = fmsDataManager.getAirports().get("SBRJ");
		Fix lodog = fmsDataManager.getFixes().get("LODOG-0");
		Fix xokix = fmsDataManager.getFixes().get("XOKIX-0");

		Map<String, Navaid> listNavaid = fmsDataManager.searchNavaidStartsWith("KEVUN");
		for (String k : listNavaid.keySet()) {
			Navaid n = listNavaid.get(k);
			System.out.println(k + " = " + n.getCode() + "," + n.getLatitude() + "," + n.getLongitude());
		}

		Map<String, Fix> listFix = fmsDataManager.searchFixStartsWith("KEVUN");
		for (String k : listFix.keySet()) {
			Fix n = listFix.get(k);
			System.out.println(k + " = " + n.getCode() + "," + n.getLatitude() + "," + n.getLongitude());
		}

		System.out.println("FIM");

		long timeOut = System.currentTimeMillis();

		System.out.println((timeOut - timeIn));

	}

}
