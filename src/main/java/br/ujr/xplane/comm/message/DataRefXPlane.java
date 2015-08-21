package br.ujr.xplane.comm.message;

import java.net.SocketException;
import java.net.UnknownHostException;

import br.ujr.xplane.comm.UDPSender;

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
	
	private String	_currentGPSDistDme = "sim/cockpit/radios/gps_dme_dist_m";
	private String	_currentGPSTimeDme = "sim/cockpit/radios/gps_dme_time_secs";
	

}
