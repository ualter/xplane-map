package br.ujr.xplane.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPReceiver extends DaemonThreads {

	DatagramSocket						datagram_socket;
	byte[]								receive_buffer;
	boolean								has_reception;
	private List<UDPMessageListener>	udpListeners	= new ArrayList<UDPMessageListener>();
	public static Logger				logger			= LoggerFactory.getLogger(UDPReceiver.class);
	private int							chunks;

	public UDPReceiver(String ipAddress, int port, int chunks) throws SocketException {
		super();
		this.receive_buffer = new byte[10000];
		this.datagram_socket = new DatagramSocket(port);
		this.datagram_socket.setSoTimeout(1000);
		this.has_reception = true;
		this.chunks = chunks;
	}

	public void addUDPMessageListener(UDPMessageListener listener) {
		this.udpListeners.add(listener);
	}

	public DatagramPacket receive() throws IOException {
		DatagramPacket packet = new DatagramPacket(receive_buffer, receive_buffer.length);
		datagram_socket.receive(packet);
		return packet;
	}

	public void run() {
		logger.info("X-Plane receiver listening on port " + datagram_socket.getLocalPort());
		DatagramPacket packet = null;
		while (this.runState) {
			try {
				packet = receive();

				if (this.has_reception == false) {
					this.has_reception = true;
					logger.info("UDP reception re-established");
				}

				this.receiveData(packet.getData());

			} catch (SocketTimeoutException ste) {
				if (this.has_reception == true) {
					logger.info("No UDP reception");
					this.has_reception = false;
				}
			} catch (Exception e) {
				logger.info("Caught error while waiting for UDP packets! (" + e.toString() + ")");
			}
		}
		logger.info("X-Plane receiver stopped");
	}

	public void receiveData(byte[] sim_data) throws Exception {
		String packet_type = new String(sim_data, 0, 4).trim();

		if (packet_type.equals("DATA")) {

			int pos = 5;
			for (int times = 0; times < this.chunks; times++) {
				int index; // , segments;
				float value = -999;
				
				ByteBuffer byteBuffer = ByteBuffer.wrap(sim_data, pos, sim_data.length - pos);
				byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

				index = byteBuffer.getInt();
				/*
				 * Compose the Data
				 */
				DATAMessage dataMessage = new DATAMessage();
				dataMessage.setIndex(index);
				for (int i = 0; i < 8; i++) {
					value = byteBuffer.getFloat();
					dataMessage.getRxData()[i] = value;
				}

				/*
				 * Spread the information
				 */
				for (UDPMessageListener l : this.udpListeners) {
					l.listenTo(dataMessage);
				}
				
				// next chunk of data
				pos += 36;
			}
		}
	}
}