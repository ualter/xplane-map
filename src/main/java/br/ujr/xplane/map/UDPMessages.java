package br.ujr.xplane.map;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPMessages {

	public static Logger logger = LoggerFactory.getLogger(UDPMessages.class);
	
	public static void main(String[] args) {
		new Thread(new SendMessages()).start();
	}

	public static class SendMessages implements Runnable {

		boolean			on		= true;
		int				port	= 49003;
		float			lat		= -23.45679f;
		float			lon		= -46.45679f;
		float			alt		= 10000;
		byte[]			data	= new byte[1024];
		DatagramSocket	socket	= null;
		Random			rnd		= new Random();
		DecimalFormat	df		= new DecimalFormat("##.#####");

		public SendMessages() {
		}

		public void run() {
			while (on) {
				try {
					Thread.sleep(1000);
					send();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		public void send() {

			try {

				int random = rnd.nextInt(10);
				if ( random % 2 == 0 ) {
					lat = lat - 00.01000f; 
				} else {
					lon = lon - 00.01000f;
				}

				StringBuffer sb = new StringBuffer();
				sb.append(df.format(lat).replaceAll(",", ".")).append(";");
				sb.append(df.format(lon).replaceAll(",", ".")).append(";");
				sb.append(df.format(alt));

				logger.info("Sending: {}",sb.toString());
				data = sb.toString().getBytes();

				socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), port);
				socket.send(packet);

			} catch (SocketException e) {
				throw new RuntimeException(e);
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
		}

	}

}
