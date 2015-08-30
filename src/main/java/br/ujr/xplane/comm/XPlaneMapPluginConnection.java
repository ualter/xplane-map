package br.ujr.xplane.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ujr.xplane.comm.message.DATAMessage;

@SuppressWarnings("unused")
public class XPlaneMapPluginConnection extends DaemonThread implements XPlaneMapPluginListener {

	/*
	 * "sim/cockpit/radios/nav1_has_dme"; "sim/cockpit/radios/nav1_freq_hz";
	 * "sim/cockpit2/radios/indicators/nav1_dme_distance_nm";
	 * "sim/cockpit2/radios/indicators/nav1_dme_time_min";
	 * "sim/cockpit/radios/nav2_has_dme"; "sim/cockpit/radios/nav2_freq_hz";
	 * "sim/cockpit2/radios/indicators/nav2_dme_distance_nm";
	 * "sim/cockpit2/radios/indicators/nav2_dme_time_min";
	 * "sim/cockpit2/gauges/indicators/altitude_ft_pilot";
	 * "sim/cockpit2/gauges/indicators/airspeed_kts_pilot";
	 * "sim/cockpit/radios/gps_dme_dist_m";
	 * "sim/cockpit/radios/gps_dme_time_secs";
	 */

	public static Logger					logger		= LoggerFactory.getLogger(XPlaneMapPluginConnection.class);

	private int								port;
	private byte[]							buffer;
	private DatagramSocket					updSocket;
	private InetAddress                     inetAddress;
	boolean									has_reception;
	private List<XPlaneMapPluginListener>	listeners	= new ArrayList<XPlaneMapPluginListener>();

	public XPlaneMapPluginConnection(int port) {
		this.port = port;
		this.buffer = new byte[10000];
		this.has_reception = true;
		this.runState = true;
		this.init();
	}

	public void addXPlaneMapPluginListener(XPlaneMapPluginListener listener) {
		this.listeners.add(listener);
	}

	public void init() {
		Socket socket = null;
		try {
			socket = new Socket("google.com", 80);
			String ip = socket.getLocalAddress().getHostAddress();
			this.inetAddress = socket.getLocalAddress();
			this.updSocket = new DatagramSocket(port, socket.getLocalAddress());
			this.updSocket.setSoTimeout(2000);
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void run() {
		logger.info("X-Plane Map Plugin Receiver listening on port " + updSocket.getLocalPort());
		DatagramPacket packet = null;
		while (this.runState) {
			try {
				packet = new DatagramPacket(this.buffer, buffer.length);
				updSocket.receive(packet);

				byte[] sim_data = packet.getData();

				ByteBuffer byteBuffer = ByteBuffer.wrap(sim_data, 0, sim_data.length);
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
				String message = new String(byteBuffer.array());
				logger.debug("X-Plane Map Plugin Receive: " + message);
				
				/*
				 * Spread the information
				 */
				for (XPlaneMapPluginListener l : this.listeners) {
					l.listenToXPlaneMapPlugin(this.inetAddress, message.split("\\;"));
				}

				byteBuffer.clear();
				sim_data = null;

			} catch (SocketTimeoutException ste) {
				if (this.has_reception == true) {
					logger.info("X-Plane Map Plugin: No data reception");
					this.has_reception = false;
				}
			} catch (Exception e) {
				logger.info("X-Plane Map Plugin: Caught error while waiting for UDP packets! (" + e.toString() + ")");
			}
		}
		logger.info("X-Plane Map Plugin: receiver stopped");
	}
	
	public static void main(String[] args) {
		
		XPlaneMapPluginConnection x = new XPlaneMapPluginConnection(8881);
		x.addXPlaneMapPluginListener(x);
		x.start();
		while(true);
		
	}

	public void listenToXPlaneMapPlugin(InetAddress IPAddress, String[] message) {
		for(String m : message) {
			System.out.println(m);
		}
	}

}
