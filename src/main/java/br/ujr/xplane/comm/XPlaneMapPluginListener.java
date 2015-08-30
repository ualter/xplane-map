package br.ujr.xplane.comm;

import java.net.InetAddress;


public interface XPlaneMapPluginListener {
	
	public void listenToXPlaneMapPlugin(InetAddress IPAddress, String[] message);

}