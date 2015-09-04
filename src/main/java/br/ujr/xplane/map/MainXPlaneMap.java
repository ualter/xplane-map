package br.ujr.xplane.map;

import static br.ujr.xplane.comm.XPlaneMapPluginData.*;
import static br.ujr.xplane.comm.XPlaneMapPluginData.LATITUDE;
import static br.ujr.xplane.comm.XPlaneMapPluginData.LONGITUDE;
import static br.ujr.xplane.comm.XPlaneMapPluginData.FUEL_QUANTITY;
import static br.ujr.xplane.comm.XPlaneMapPluginData.AIRSPEED;
import static br.ujr.xplane.comm.XPlaneMapPluginData.DESTINATION;
import static br.ujr.xplane.comm.XPlaneMapPluginData.ALTITUDE;
import static br.ujr.xplane.comm.XPlaneMapPluginData.BAROMETER;
import static br.ujr.xplane.comm.XPlaneMapPluginData.COMPASS_HEADING;
import static br.ujr.xplane.comm.XPlaneMapPluginData.GAME_PAUSED;
import static br.ujr.xplane.comm.XPlaneMapPluginData.NAV1_FREQUENCY;
import static br.ujr.xplane.comm.XPlaneMapPluginData.NAV2_FREQUENCY;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ujr.xplane.comm.UDPMessageListenerXPlaneDataInput;
import br.ujr.xplane.comm.UDPReceiverXPlaneDataInput;
import br.ujr.xplane.comm.UDPSenderXPlaneDataOuput;
import br.ujr.xplane.comm.XPlaneMapPluginConnection;
import br.ujr.xplane.comm.XPlaneMapPluginListener;
import br.ujr.xplane.comm.message.DATAMessage;
import br.ujr.xplane.comm.message.DATAREFMessage;
import br.ujr.xplane.comm.message.DSELMessage;
import br.ujr.xplane.comm.message.DataSetXPlane;
import br.ujr.xplane.comm.message.PAUSMessage;
import br.ujr.xplane.comm.message.UDPMessage;
import br.ujr.xplane.map.fmsdata.Airport;
import br.ujr.xplane.map.fmsdata.FMSDataManager;
import br.ujr.xplane.map.fmsdata.Fix;
import br.ujr.xplane.map.fmsdata.Navaid;
import br.ujr.xplane.map.fmsdata.flightplan.FlightPlan;
import br.ujr.xplane.map.fmsdata.flightplan.FlightPlanLoadMessages;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MainXPlaneMap implements UDPMessageListenerXPlaneDataInput, XPlaneMapPluginListener {

	public static Logger				logger				= LoggerFactory.getLogger(MainXPlaneMap.class);

	public static FMSDataManager		fms;
	private UDPSenderXPlaneDataOuput	udpSender;
	private UDPReceiverXPlaneDataInput	udpReceiver;
	private XPlaneMapPluginConnection	pluginConnection;
	private String						dataToCapture		= "20,103,132";
	private PlanesList					planeList			= new PlanesList();
	private int							pluginPort			= 8881;
	private int							portSender			= 49000;
	private int							portReceiver		= 49003;
	private Set<String>					receivedFromPlugin	= new HashSet<String>();
	private String						valueBuffer;

	public static void main(String[] args) {

		new MainXPlaneMap();

		/*
		 * if (Desktop.isDesktopSupported()) { Desktop desktop =
		 * Desktop.getDesktop(); try { desktop.browse(new URI(url)); } catch
		 * (Exception e) { e.printStackTrace(); } } else { Runtime runtime =
		 * Runtime.getRuntime(); try { runtime.exec("xdg-open " + url); } catch
		 * (IOException e) { e.printStackTrace(); } }
		 */

	}

	public MainXPlaneMap() {
		this.init();
		this.registerDATAMessages(dataToCapture);
	}

	public void init() {
		fms = new FMSDataManager();
		logger.info("Started listening to X-Plane");

		HttpServer server = null;
		Socket socket = null;
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext("/", new MyHandler(this, this.planeList));
			server.setExecutor(null);
			server.start();
			logger.info("Started the web server");

			socket = new Socket("google.com", 80);
			String ip = socket.getLocalAddress().getHostAddress();
			String url = "http://" + ip + ":8000/";
			socket.close();

			int chunks = dataToCapture.indexOf(",") > -1 ? dataToCapture.split(",").length : 1;
			udpSender = new UDPSenderXPlaneDataOuput(ip, portSender);
			udpReceiver = new UDPReceiverXPlaneDataInput(ip, portReceiver, chunks);
			udpReceiver.addUDPMessageListener(this);
			udpReceiver.start();
			pluginConnection = new XPlaneMapPluginConnection(pluginPort);
			pluginConnection.addXPlaneMapPluginListener(this);
			pluginConnection.start();

			logger.info("Map is accessible by the: " + url);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				logger.warn(e.getMessage());
			}
		}
	}

	public void registerDATAMessages(String data) {
		DSELMessage message = new DSELMessage(data);
		udpSender.send(message.toByteBuffer());
	}

	static class MyHandler implements HttpHandler {
		private MainXPlaneMap	xPlaneMap;
		private PlanesList		planesList;

		public MyHandler(MainXPlaneMap xPlaneMap, PlanesList list_) {
			this.xPlaneMap = xPlaneMap;
			this.planesList = list_;
		}

		@SuppressWarnings("unchecked")
		public void handle(HttpExchange t) throws IOException {
			String req = t.getRequestURI().toString();
			if (req.startsWith("/data")) {
				JSONObject planes = this.planesList.toJSON();
				sendJSONObject(t, planes);
			} else if (req.startsWith("/flightplan")) {
				Map<String, String> parameters = extractParameters(req);
				FlightPlanRequest flightPlanRequest = new FlightPlanRequest(parameters);
				JSONObject flightPlanJSON = new JSONObject();
				FlightPlan flightPlan = new FlightPlan();
				FlightPlanLoadMessages messages = new FlightPlanLoadMessages();

				this.loadFlightPlan(flightPlanRequest, flightPlan, messages);

				if (flightPlan.isValid()) {
					logger.info(flightPlan.toString());
					flightPlan.calculateInfoRoute();
					sendJSONObject(t, flightPlan);
				} else {
					flightPlanJSON.put("messages", messages);
					sendJSONObject(t, flightPlanJSON);
				}
			} else if (req.startsWith("/pause")) {
				this.xPlaneMap.pauseXPlane();
			} else {
				String resource = req.replaceAll("/", "");
				logger.debug("Resource required to Web Server: {}", resource);
				if (StringUtils.isNotBlank(resource)) {
					sendFile(t, resource);
				} else {
					sendFile(t, "index.html");
				}
			}
		}

		private void loadFlightPlan(FlightPlanRequest flightPlanRequest, FlightPlan flightPlan, FlightPlanLoadMessages messages) {
			Airport departure = fms.getAirports().get(flightPlanRequest.getDeparture());
			if (departure != null) {
				flightPlan.setDeparture(departure);
				Airport destination = fms.getAirports().get(flightPlanRequest.getDestination());
				if (destination != null) {
					flightPlan.setDestination(destination);

					if (flightPlanRequest.getWaypoints() != null && flightPlanRequest.getWaypoints().length > 0) {
						for (String wpt : flightPlanRequest.getWaypoints()) {
							Navaid navaid = null;
							Fix fix = null;
							boolean lastCharIsNumber = !Character.isLetter(wpt.charAt(wpt.length() - 1));
							String wptFull = null;
							if (lastCharIsNumber) {
								wptFull = wpt.substring(0, wpt.length() - 1) + "-" + wpt.substring(wpt.length() - 1);
							}

							// Look first for a Navaid
							if (lastCharIsNumber) {
								logger.info("Try loading specifically Navaid {}", wptFull);
								navaid = fms.getNavaids().get(wptFull);
								if (navaid != null) {
									flightPlan.addNavaid(navaid);
									logger.info("Loaded Navaid {} {}", wptFull, navaid.toString());
								}
							} else {
								for (int i = 0; i < 3; i++) {
									logger.info("Try loading Navaid {}", wpt + "-" + (i));
									navaid = fms.getNavaids().get(wpt + "-" + (i));
									if (navaid != null) {
										flightPlan.addNavaid(navaid);
										logger.info("Loaded Navaid {} {}", wpt + "-" + (i), navaid.toString());
										break;
									}
								}
							}

							// Then Look for a Fix if in Navaid registers
							// was not found the wpt
							if (navaid == null) {
								if (lastCharIsNumber) {
									logger.info("Try loading specifically Fix {}", wptFull);
									fix = fms.getFixes().get(wptFull);
									if (fix != null) {
										flightPlan.addFix(fix);
										logger.info("Loaded Fix {} {}", wptFull, fix.toString());
									}
								} else {
									for (int i = 0; i < 3; i++) {
										logger.info("Try loading Fix {}", wpt + "-" + (i));
										fix = fms.getFixes().get(wpt + "-" + (i));
										if (fix != null) {
											flightPlan.addFix(fix);
											logger.info("Loaded Fix {} {}", wpt + "-" + (i), fix.toString());
											break;
										}
									}
								}
							}

							// Nothing found, neither Navaid, nor Fix
							if (navaid == null && fix == null) {
								logger.info("Waypoint {} not found.", wpt);
								messages.addMessage(wpt + " was not found.");
							}
						}
					}
				} else {
					logger.info("Destination airport {} not found.", flightPlanRequest.getDestination());
					messages.addMessage(flightPlanRequest.getDestination() + " was not found.");
				}
			} else {
				logger.info("Departure airport {} not found.", flightPlanRequest.getDeparture());
				messages.addMessage(flightPlanRequest.getDeparture() + " was not found.");
			}
		}

		private Map<String, String> extractParameters(String req) throws UnsupportedEncodingException {
			Map<String, String> map = null;
			if (req.contains("?")) {
				map = new HashMap<String, String>();
				String parameters[] = req.substring(req.indexOf("?") + 1).split("&");
				for (String param : parameters) {
					String pairs[] = param.split("=");
					if (pairs.length > 1) {
						String name = pairs[0];
						String value = URLDecoder.decode(pairs[1], "UTF-8");
						map.put(name, value);
					}
				}
			}
			return map;
		}

		private void sendJSONObject(HttpExchange t, JSONObject jsonObject) throws IOException {
			StringWriter out = new StringWriter();
			jsonObject.writeJSONString(out);
			String response = out.toString();
			sendHTTPResponse(t, response);
		}

		private void sendJSONObject(HttpExchange t, Object bean) throws IOException {
			ObjectMapper mapper = new ObjectMapper();
			StringWriter out = new StringWriter();
			mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
			// mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE,
			// true);
			mapper.writeValue(out, bean);
			String response = out.toString();
			sendHTTPResponse(t, response);
		}

		private void sendHTTPResponse(HttpExchange t, String response) throws IOException {
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}

		private void sendFile(HttpExchange t, String file) {
			try {
				InputStream is = getClass().getClassLoader().getResourceAsStream(file);
				t.sendResponseHeaders(200, 0L);
				OutputStream os = t.getResponseBody();

				byte[] buffer = new byte['?'];
				int len = is.read(buffer);
				while (len != -1) {
					os.write(buffer, 0, len);
					len = is.read(buffer);
				}
				is.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setDataRefValue(String dataRef, float value) {
		DATAREFMessage drefMessage = new DATAREFMessage(dataRef, value);
		this.sendMessage(drefMessage);
	}

	public void pauseXPlane() {
		PAUSMessage pausMessage = new PAUSMessage();
		this.sendMessage(pausMessage);
	}

	public void sendMessage(UDPMessage xpm) {
		this.udpSender.send(xpm.toByteBuffer());
	}

	public void listenToXPlaneDataInput(InetAddress IPAddress, DATAMessage message) {
		if (logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer(message.getIndex() + " [");
			int param = 0;
			for (float f : message.getRxData()) {
				sb.append(param + "=" + f);
				sb.append(",");
			}
			sb.append("]");
			logger.debug(sb.toString());
		}
		this.updateDataXPlaneDataInput(IPAddress, message);
	}

	public void listenToXPlaneMapPlugin(InetAddress ip, String[] messages) {
		String label, value;
		for (String message : messages) {

			//System.out.println(message);

			String[] messageParts = message.trim().split("=");
			if (messageParts.length > 1) {
				label = messageParts[0];
				value = messageParts[1];

				if (message.contains(DESTINATION)) {
					this.planeList.updateDestination(ip, value);
				} else if (message.contains(LATITUDE)) {
					this.valueBuffer = value;
				} else if (message.contains(LONGITUDE)) {
					this.planeList.updateLatitudeLongitude(ip, this.valueBuffer, value);
				} else if (message.contains(GAME_PAUSED)) {
					this.planeList.updatePause(ip, value);
				} else if (message.contains(BAROMETER)) {
					this.planeList.updateBarometer(ip, value);
				} else if (message.contains(COMPASS_HEADING)) {
					this.planeList.updateCompassHeading(ip, value);
				} else if (message.contains(NAV1_FREQUENCY)) {
					this.planeList.updateNav1Frequency(ip, value);
				} else if (message.contains(NAV2_FREQUENCY)) {
					this.planeList.updateNav2Frequency(ip, value);
				} else if (message.contains(ALTITUDE)) {
					if (!this.receivedFromPlugin.contains(ALTITUDE))
						this.receivedFromPlugin.add(ALTITUDE);
					this.planeList.updateAltitude(ip, value);
				} else if (message.contains(AIRSPEED) && !message.contains("true") ) {
					this.planeList.updateAirspeed(ip, value);
				} else if (message.contains(FUEL_QUANTITY)) {
					this.planeList.updateFuelQuantity(ip, value);
				} else if (message.contains(VERTICAL_SPEED)) {
					this.planeList.updateVerticalSpeed(ip, value);
				} else if (message.contains(GROUND_SPEED)) {
					this.planeList.updateGroundSpeed(ip, value);
				} else if (message.contains(GPS_DISTANCE_METERS)) {
					this.planeList.updateGpsDistanceMeters(ip, value); 
				} else if (message.contains(GPS_DISTANCE_SECONDS)) {
					this.planeList.updateGpsDistanceSeconds(ip, value);
				} else if (message.contains(OUTSIDE_TEMPERATURE_CELSIUS)) {
					this.planeList.updateOutsideTemparature(ip, value);
				} else if (message.contains(WEIGHT_TOTAL_FUEL)) {
					//
				} else if (message.contains(COM1_FREQUENCY)) {
					this.planeList.updateCom1Freq(ip, value);
				} else if (message.contains(COM2_FREQUENCY)) {
					this.planeList.updateCom2Freq(ip, value);
				}
			}
		}
	}

	/**
	 * Disabled update via Net Connection 
	 */
	private void updateDataXPlaneDataInput(InetAddress ip, DATAMessage message) {
		switch (message.getIndex()) {
			case DataSetXPlane.LAT_LON_ALTITUDE: {
				// this.planeList.updateLatitudeLongitude(ip, message);
				//if (!this.receivedFromPlugin.contains(ALTITUDE))
				//	this.planeList.updateAltitude(ip, message);
				break;
			}
			case DataSetXPlane.CLIMB_STATUS: {
				//this.planeList.updateClimbStatus(ip, message);
				break;
			}
			default:
				break;
		}
	}

}
