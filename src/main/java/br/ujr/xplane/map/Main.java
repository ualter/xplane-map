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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {
	public static void main(String[] args) throws Exception {
		PlanesList list = new PlanesList();

		new Thread(new UDPListener(list)).start();
		System.out.println("Started listening to X-Plane");

		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new MyHandler(list));
		server.setExecutor(null);
		server.start();
		System.out.println("Started the web server");

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
		System.out.println("Started browser");
		System.out.println("The map is now visible at address " + url + " on this computer and any device on the same network.");
	}

	static class MyHandler implements HttpHandler {
		private PlanesList	planesList;

		public MyHandler(PlanesList list_) {
			this.planesList = list_;
		}

		public void handle(HttpExchange t) throws IOException {
			String req = t.getRequestURI().toString();
			System.out.println("URI=" + req);

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
