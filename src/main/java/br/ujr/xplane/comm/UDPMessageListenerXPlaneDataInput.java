package br.ujr.xplane.comm;

import java.net.InetAddress;

import br.ujr.xplane.comm.message.DATAMessage;


public interface UDPMessageListenerXPlaneDataInput {
	
	public void listenToXPlaneDataInput(InetAddress IPAddress, DATAMessage message);

}