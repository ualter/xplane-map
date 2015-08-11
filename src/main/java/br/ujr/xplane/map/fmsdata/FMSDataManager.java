package br.ujr.xplane.map.fmsdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ujr.xplane.map.fmsdata.Navaid.NavaidType;
import ch.qos.logback.classic.Level;

public class FMSDataManager {

	public static Logger			logger		= LoggerFactory.getLogger(FMSDataManager.class);
	private Map<Coordinate, Place>	coordinates	= new HashMap<Coordinate, Place>();
	private Map<String, Navaid>		navaids		= new HashMap<String, Navaid>();
	private Map<String, Airport>	airports	= new HashMap<String, Airport>();
	private Map<String, Waypoint>	waypoints	= new HashMap<String, Waypoint>();
	private Map<String, Airway>		airways		= new HashMap<String, Airway>();

	public FMSDataManager() {
		super();
		
		//Temporarily
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.WARN);
		
		init();
	}

	public void init() {
		loadNavaids();
		loadAirports();
		loadWaypoints();
		loadAirways();
	}

	private void loadNavaids() {
		BufferedReader fileReader = null;
		try {

			fileReader = new BufferedReader(new FileReader(new File(Utils.NAVAID_SOURCE)));
			String line = fileReader.readLine();

			int count = 0;

			while (line != null) {
				String[] columns = line.split("\\|");

				String id = columns[0];
				String desc = columns[1];
				String freq = Utils.parseFreq(columns[2]);
				int    type = Integer.parseInt(columns[3]);
				float lat = Utils.parseCoord(columns[6]);
				float lon = Utils.parseCoord(columns[7]);
				
				NavaidType navaidType = null;
				switch(type) {
					case 1: {
						navaidType = NavaidType.VOR;
						break;
					}
					default: {
						navaidType = NavaidType.NDB;
					}
				}

				Navaid navaid = new Navaid(id, desc, freq, lat, lon, navaidType);
				this.navaids.put(id, navaid);
				savePlace(navaid);

				count++;
				logger.info("Loading Navaid: {}", navaid.getCode() + "-" + navaid.getDescription());

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

			fileReader = new BufferedReader(new FileReader(new File(Utils.AIRPORT_SOURCE)));
			String line = fileReader.readLine();

			int count = 0;
			Airport airport = null;

			while (line != null) {

				if (count > 1) {
					if (StringUtils.isNotBlank(line)) {

						String[] columns = line.split("\\|");
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
							String latitude = columns[7];
							String longitude = columns[8];
							String elevation = columns[9];

							Airport.Runway runway = new Airport.Runway();
							runway.setNumber(number);
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

						logger.info("Loading Airport: {}", new Object[] { (airport != null ? airport.getIcaoId() + "[" + airport.listRunways() + "]" : "") });
					}
				}

				count++;
				line = fileReader.readLine();
			}

			this.airports.put(airport.getIcaoId(), airport);
			savePlace(airport);

			logger.info("Loading Airport: {}", new Object[] { (airport != null ? airport.getIcaoId() + "[" + airport.listRunways() + "]" : "") });

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

			fileReader = new BufferedReader(new FileReader(new File(Utils.WAYPOINTS_SOURCE)));
			String line = fileReader.readLine();

			int count = 0;

			while (line != null) {
				String[] columns = line.split("\\|");

				String id = columns[0];
				float lat = Utils.parseCoord(columns[1]);
				float lon = Utils.parseCoord(columns[2]);

				Waypoint waypoint = new Waypoint(0,id, lat, lon);
				
				while(this.waypoints.containsKey(waypoint.getKey())) {
					waypoint.setIndex(waypoint.getIndex() + 1);
				}
				
				this.waypoints.put(waypoint.getKey(), waypoint);
				savePlace(waypoint);

				count++;
				logger.info("Loading Waypoint: {}", waypoint);

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

			fileReader = new BufferedReader(new FileReader(new File(Utils.AIRWAYS_SOURCE)));
			String line = fileReader.readLine();

			int count = 0;
			Airway airway = null;
			Waypoint finalWaypoint = null;

			while (line != null) {

				if (StringUtils.isNotBlank(line)) {

					String[] columns = line.split("\\|");
					String type = columns[0];

					if ("A".equalsIgnoreCase(type)) {
						String code = columns[1];
						String number = columns[2];

						airway = new Airway(0,code,number);

					} else if ("S".equalsIgnoreCase(type)) {
						String code = columns[1];
						String latitude = columns[2];
						String longitude = columns[3];
						String code2 = columns[4];
						String latitude2 = columns[5];
						String longitude2 = columns[6];

						Waypoint waypoint = new Waypoint(0,code,latitude,longitude);
						airway.getWayPoints().put(code, waypoint);
						finalWaypoint = new Waypoint(0,code2,latitude2,longitude2);
					}
				} else {
					airway.getWayPoints().put(finalWaypoint.getCode(), finalWaypoint);
					
					while (this.airways.containsKey(airway.getKey())) {
						airway.setIndex(airway.getIndex() + 1);
					}
					
					this.airways.put(airway.getKey(), airway);
					logger.info("Loading Airways: {}", new Object[] { (airway != null ? airway.toString() : "") });
				}

				count++;
				line = fileReader.readLine();
			}

			/*while (this.airways.containsKey(airway.getKey())) {
				airway.setIndex(airway.getIndex() + 1);
			}
			this.airways.put(airway.getKey(), airway);
			logger.info("Loading Airways: {}", new Object[] { (airway != null ? airway.toString() : "") });*/

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

	public Map<String, Waypoint> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(Map<String, Waypoint> waypoints) {
		this.waypoints = waypoints;
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

	public static void main(String[] args) {
		FMSDataManager fmsDataManager = new FMSDataManager();
		
		Airport sp = fmsDataManager.getAirports().get("SBSP");
		Airport rj = fmsDataManager.getAirports().get("SBRJ");
		Waypoint lodog = fmsDataManager.getWaypoints().get("LODOG-0");
		Waypoint xokix = fmsDataManager.getWaypoints().get("XOKIX-0");
		
		System.out.println(xokix);
		
		
		
		
	}

}
