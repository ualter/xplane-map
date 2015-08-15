package br.ujr.xplane.map.fmsdata;

public class Utils {

	public static String	PATH_SOURCE			= "c:/X-Plane 10/Aircraft/CRJ-200/plugins/CRJAvionics/navdata/";

	public static String	NAVAID_SOURCE		= PATH_SOURCE + "Navaids.txt";
	public static String	AIRPORT_SOURCE		= PATH_SOURCE + "Airports.txt";
	public static String	WAYPOINTS_SOURCE	= PATH_SOURCE + "Waypoints.txt";
	public static String	AIRWAYS_SOURCE		= PATH_SOURCE + "ATS.txt";

	public static String parseFreq(String vlr) {
		if (vlr.length() > 5) {
			return vlr.substring(0, 3) + "." + vlr.substring(3, 5);
		} else {
			return "---";
		}
	}

	public static float parseCoord(String vlr) {
		if (vlr.length() > 6) {
			int start = vlr.length() - 6;
			String decimal = vlr.substring(start);
			String integer = vlr.substring(0, vlr.indexOf(decimal));
			String coord = integer + "." + decimal;
			return Float.parseFloat(coord);
		} else {
			return Float.parseFloat(vlr);
		}
	}

	public static void main(String[] args) {
		/*String lat = "47449889";
		String lng = "-122311778";

		System.out.println(Utils.parseCoord(lat));
		System.out.println(Utils.parseCoord(lng));*/
		
		int a = -276;
		
		if ( a < -275) {
			System.out.println("menor");
		}

	}

}
