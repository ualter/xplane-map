package br.ujr.xplane.comm;

import java.net.InetAddress;

import br.ujr.xplane.comm.message.DATAMessage;


public interface UDPMessageListener {
	
	public void listenTo(InetAddress IPAddress, DATAMessage message);

}