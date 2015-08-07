package br.ujr.xplane.map;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.Arrays;

public class UDPListener implements Runnable {
	private PlanesList	list;
	
	private MessageFormat message = new MessageFormat("Received from X-Plane: Latitude {0}, Longitude {1}, Altitude {2}");

	public UDPListener(PlanesList list_) {
		this.list = list_;
	}

	public void run() {
		try {
			DatagramSocket serverSocket = new DatagramSocket(49003);
			byte[] receiveData = new byte[32];

			System.out.printf("Listening on udp: %s:%d%n", new Object[] { InetAddress.getLocalHost().getHostAddress(), Integer.valueOf(49003) });
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			for (;;) {
				serverSocket.receive(receivePacket);

				int ident = receivePacket.getData()[5];
				if (ident == 20) {
					Float lat = Float.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(receivePacket.getData(), 9, 13)).order(ByteOrder.LITTLE_ENDIAN).getFloat());
					Float lon = Float.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(receivePacket.getData(), 13, 17)).order(ByteOrder.LITTLE_ENDIAN).getFloat());
					Float alt = Float.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(receivePacket.getData(), 17, 21)).order(ByteOrder.LITTLE_ENDIAN).getFloat());
					InetAddress IPAddress = receivePacket.getAddress();

					this.list.setPlaneLat(IPAddress, lat.floatValue());
					this.list.setPlaneLon(IPAddress, lon.floatValue());
					this.list.setPlaneAlt(IPAddress, alt.floatValue());
					
					System.out.println(message.format(new Object[]{lat,lon,alt}));
				} else {
					System.out.println("Received ident:" + ident);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
