package br.ujr.xplane.map.fmsdata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	public static boolean	IN_DEVELOPMENT		= false;
	public static Logger	logger				= LoggerFactory.getLogger(Utils.class);
	public static String	NAVAID_SOURCE		= "Navaids.txt";
	public static String	AIRPORT_SOURCE		= "Airports.txt";
	public static String	WAYPOINTS_SOURCE	= "Waypoints.txt";
	public static String	AIRWAYS_SOURCE		= "ATS.txt";

	public static BufferedReader getSourceReader(String file) {
		InputStream fis;
		String path = "./navdata/" + file;
		if (IN_DEVELOPMENT) {
			path = "/Users/Ualter/Developer/xplane-map/src/main/resources/navdata/" + file;
		}
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			logger.error(path + " nao foi encontrado - " + e.getMessage(), e);
			throw new RuntimeException(path + " nao foi encontrado - " + e.getMessage());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		return br;
	}

	public static BufferedReader getNavaidSourceReader() {
		return Utils.getSourceReader(NAVAID_SOURCE);
	}

	public static BufferedReader getAirportSourceReader() {
		return Utils.getSourceReader(AIRPORT_SOURCE);
	}

	public static BufferedReader getWaypointsSourceReader() {
		return Utils.getSourceReader(WAYPOINTS_SOURCE);
	}

	public static BufferedReader getAirwaysSourceReader() {
		return Utils.getSourceReader(AIRWAYS_SOURCE);
	}

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

	public static Integer[] toIntArray(String data) {
		String[] s = data.split(",");
		Integer[] aux = new Integer[s.length];

		for (int i = 0; i < aux.length; i++) {
			aux[i] = Integer.parseInt(s[i]);
		}
		return aux;
	}

	public static void main(String[] args) {
		/*
		 * String lat = "47449889"; String lng = "-122311778";
		 * 
		 * System.out.println(Utils.parseCoord(lat));
		 * System.out.println(Utils.parseCoord(lng));
		 */

		/*
		 * int a = -276;
		 * 
		 * if ( a < -275) { System.out.println("menor"); }
		 */

		String s = "LODOG1";
		boolean lastIsNumber = !Character.isLetter(s.charAt(s.length() - 1));

		String wptFull = s.substring(0, s.length() - 1) + "-" + s.substring(s.length() - 1);

		System.out.println(lastIsNumber + "," + wptFull);

	}

}
