package br.ujr.xplane.comm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ujr.xplane.comm.message.DATAMessage;

public class UDPSender extends DaemonThread {

	public static Logger				logger	= LoggerFactory.getLogger(UDPSender.class);

	private DatagramSocket				datagramSocket;
	private int							port;
	private String						ipAddress;
	private BlockingQueue<DATAMessage>	messageQueue;

	public UDPSender(String ipAddress, int port) throws SocketException, UnknownHostException {
		super();
		this.port = port;
		this.ipAddress = ipAddress;
		datagramSocket = new DatagramSocket();
		messageQueue = new ArrayBlockingQueue<DATAMessage>(5000, true);
	}

	public void sendDATAMessage(DATAMessage message) {
		send(message.toByteBuffer());
	}

	public void send(ByteBuffer bb) {
		send(bb.array());
	}

	public void send(byte[] buffer) {
		try {
			datagramSocket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ipAddress), port));
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public void run() {
		while (this.runState) {
			if (messageQueue.size() > 0) {

				int bufferSize = messageQueue.size() * 36 + 5;
				if (bufferSize > 41) {
					bufferSize = 41;
				}
				ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
				DATAMessage m = messageQueue.poll();
				byteBuffer.put(m.toByteBuffer().array());
				send(byteBuffer);
				try {
					sleep(100);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public BlockingQueue<DATAMessage> getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(BlockingQueue<DATAMessage> messageQueue) {
		this.messageQueue = messageQueue;
	}
}