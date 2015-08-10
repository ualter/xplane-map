package br.ujr.xplane.map.fmsdata;

public class Utils {

	public static String	PATH_SOURCE			= "c:/X-Plane 10/Aircraft/CRJ-200/plugins/CRJAvionics/navdata/";

	public static String	NAVAID_SOURCE		= PATH_SOURCE + "Navaids.txt";
	public static String	AIRPORT_SOURCE		= PATH_SOURCE + "Airports.txt";
	public static String	WAYPOINTS_SOURCE	= PATH_SOURCE + "Waypoints.txt";
	public static String	AIRWAYS_SOURCE		= PATH_SOURCE + "ATS.txt";

	public static String parseFreq(String vlr) {
		return vlr.substring(0, 3) + "." + vlr.substring(3, 5);
	}

	public static float parseCoord(String vlr) {
		if (vlr.startsWith("-")) {
			return Float.parseFloat(vlr.substring(1, 3) + "." + vlr.substring(3)) * -1;
		} else {
			if (vlr.length() > 3) {
				return Float.parseFloat(vlr.substring(0, 2) + "." + vlr.substring(2));
			} else {
				return Float.parseFloat(vlr);
			}
		}
	}

}
