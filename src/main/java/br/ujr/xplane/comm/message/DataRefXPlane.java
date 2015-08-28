package br.ujr.xplane.comm.message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@SuppressWarnings("unused")
public class DataRefXPlane {

	private String	_userNav1HasDme			= "sim/cockpit/radios/nav1_has_dme";
	private String	_currentFrequencyNav1	= "sim/cockpit/radios/nav1_freq_hz";
	private String	_currentDistanceDmeNav1	= "sim/cockpit2/radios/indicators/nav1_dme_distance_nm";
	private String	_currentTimeDmeNav1		= "sim/cockpit2/radios/indicators/nav1_dme_time_min";
	private String	_userNav2HasDme			= "sim/cockpit/radios/nav2_has_dme";
	private String	_currentFrequencyNav2	= "sim/cockpit/radios/nav2_freq_hz";
	private String	_currentDistanceDmeNav2	= "sim/cockpit2/radios/indicators/nav2_dme_distance_nm";
	private String	_currentTimeDmeNav2		= "sim/cockpit2/radios/indicators/nav2_dme_time_min";
	private String	_currentAltitude		= "sim/cockpit2/gauges/indicators/altitude_ft_pilot";
	private String	_currentAirspeed		= "sim/cockpit2/gauges/indicators/airspeed_kts_pilot";

	private String	_currentGPSDistDme		= "sim/cockpit/radios/gps_dme_dist_m";
	private String	_currentGPSTimeDme		= "sim/cockpit/radios/gps_dme_time_secs";

	public static void main(String[] args) {
		int port = 8881;
		byte[] buffer = new byte[10000];

		Socket socket = null;
		DatagramSocket updSocket = null;
		try {

			socket = new Socket("google.com", 80);
			String ip = socket.getLocalAddress().getHostAddress();
			updSocket = new DatagramSocket(port, socket.getLocalAddress());
			
			while (true) {
				System.out.println("Listening...");
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				updSocket.receive(packet);
				
				System.out.println("Data coming...");
				
				byte[] sim_data = packet.getData();
				
				ByteBuffer byteBuffer = ByteBuffer.wrap(sim_data, 0, sim_data.length);
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
				System.out.println("Received: " + new String(byteBuffer.array()));
				
				byteBuffer.clear();
				sim_data = null;
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (updSocket != null)
				updSocket.close();
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

}
