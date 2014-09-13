package net.craftingstore.connector.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAEncoder {

	//convert a byte array into a hexadecimal string
	private static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes)
			result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}

	/**
	 * convert a string into a hexadecimal sha256 encrypted string
	 * 
	 * @param data
	 * @return a hex string version of the encryption
	 * @throws NoSuchAlgorithmException
	 */
	public static String hash256(String data) throws NoSuchAlgorithmException {
		//choosing our encryption
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		//take the bytes of the data string and encrypt them!
		md.update(data.getBytes());

		//convert the byte array into a hex string and return it
		return bytesToHex(md.digest());
	}
}
