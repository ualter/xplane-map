package br.ujr.xplane.map;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPListener implements Runnable {

	public static Logger	logger			= LoggerFactory.getLogger(UDPListener.class);
	private boolean			receiveFake		= false;
	private PlanesList		list;

	DatagramSocket			serverSocket	= null;

	public UDPListener(PlanesList list_) {
		this.list = list_;
	}

	public void run() {
		try {
			serverSocket = new DatagramSocket(49003);
			byte[] receiveData = new byte[32];

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			for (;;) {
				serverSocket.receive(receivePacket);
				InetAddress IPAddress = receivePacket.getAddress();

				if (!receiveFake) {
					int ident = receivePacket.getData()[5];
					if (ident == 20) {
						Float lat = Float.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(receivePacket.getData(), 9, 13)).order(ByteOrder.LITTLE_ENDIAN).getFloat());
						Float lon = Float.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(receivePacket.getData(), 13, 17)).order(ByteOrder.LITTLE_ENDIAN).getFloat());
						Float alt = Float.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(receivePacket.getData(), 17, 21)).order(ByteOrder.LITTLE_ENDIAN).getFloat());
						
						//Float hSpeed = Float.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(receivePacket.getData(), 29, 34)).order(ByteOrder.LITTLE_ENDIAN).getFloat());
						//System.out.println(hSpeed + ", " + alt);
						System.out.println(new String(receivePacket.getData()));
						
						this.updatePosition(lat, lon, alt, IPAddress);
						
					} else {
						logger.info("Received ident:" + ident);
					}
				} else {
					String data[] = new String(receivePacket.getData()).split(";");

					Float lat = Float.valueOf(data[0]);
					Float lon = Float.valueOf(data[1]);
					Float alt = Float.valueOf(data[2]);
					this.updatePosition(lat, lon, alt, IPAddress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}

	private void updatePosition(Float lat, Float lon, Float alt, InetAddress IPAddress) {
		this.list.setPlaneLat(IPAddress, lat.floatValue());
		this.list.setPlaneLon(IPAddress, lon.floatValue());
		this.list.setPlaneAlt(IPAddress, alt.floatValue());

		logger.debug("Received from X-Plane: Latitude {}, Longitude {}, Altitude {}", new Object[] { lat, lon, alt });
	}
}
