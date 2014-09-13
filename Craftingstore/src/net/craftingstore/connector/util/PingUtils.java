package net.craftingstore.connector.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;

import net.craftingstore.connector.Connector;

public class PingUtils {

	//class for global api nodes
	public class Mode {

		public static final String ONLINE_PING  = "onlinePing";
		public static final String OFFLINE_PING = "offlinePing";
		public static final String GET_VERSION  = "getVersion";
		public static final String ADD_POINTS   = "addPointsToUser";
	}

	public static String connect(String url) throws IOException {
		if (Connector.debugMode)
			Connector.logger().info("DEBUG ping url: " + url);
		URLConnection con = new URL(url).openConnection();
		InputStream is = con.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String input = reader.readLine();
		is.close();
		reader.close();
		return input;
	}

	public static String getExchangeHash(String subdomain, String mode, String password, String playerName, long value) {
		try {
			return SHAEncoder.hash256(subdomain + mode + playerName + value + password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getHash(String subdomain, String mode, String password) {
		try {
			return SHAEncoder.hash256(subdomain + mode + password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
