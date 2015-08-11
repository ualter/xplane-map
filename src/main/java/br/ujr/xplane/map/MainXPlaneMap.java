package br.ujr.xplane.map;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MainXPlaneMap {
	
	public static Logger logger = LoggerFactory.getLogger(MainXPlaneMap.class);
	
	public static void main(String[] args) throws Exception {
		PlanesList list = new PlanesList();

		new Thread(new UDPListener(list)).start();
		logger.info("Started listening to X-Plane");

		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new MyHandler(list));
		server.setExecutor(null);
		server.start();
		logger.info("Started the web server");

		Socket s = new Socket("google.com", 80);
		String url = "http://" + s.getLocalAddress().getHostAddress() + ":8000/";
		s.close();
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.info("Started browser");
		logger.info("The map is now visible at address " + url + " on this computer and any device on the same network.");
	}

	static class MyHandler implements HttpHandler {
		private PlanesList	planesList;

		public MyHandler(PlanesList list_) {
			this.planesList = list_;
		}

		public void handle(HttpExchange t) throws IOException {
			String req = t.getRequestURI().toString();
			
			if (req.startsWith("/data")) {
				JSONObject planes = new JSONObject();
				for (String ip : this.planesList.getLatMap().keySet()) {
					JSONObject latlon = new JSONObject();
					latlon.put("lat", this.planesList.getLatMap().get(ip));
					latlon.put("lon", this.planesList.getLonMap().get(ip));
					latlon.put("alt", this.planesList.getAltMap().get(ip));
					planes.put(ip.replace('.', '-').substring(1), latlon);
				}
				StringWriter out = new StringWriter();
				planes.writeJSONString(out);
				String response = out.toString();

				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			} else if (req.startsWith("/map.js")) {
				sendFile(t, "map.js");
			} else if (req.startsWith("/markerwithlabel.js")) {
				sendFile(t, "markerwithlabel.js");	
			} else if (req.startsWith("/map.css")) {
				sendFile(t, "map.css");	
			} else if (req.startsWith("/airport.png")) {
				sendFile(t, "airport.png");		
			} else if (req.startsWith("/VOR.png")) {
				sendFile(t, "VOR.png");
			} else if (req.startsWith("/NDB.png")) {
				sendFile(t, "NDB.png");	
			} else {
				sendFile(t, "index.html");
			}
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

}
