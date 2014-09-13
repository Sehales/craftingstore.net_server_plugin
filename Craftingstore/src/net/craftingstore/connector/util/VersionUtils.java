package net.craftingstore.connector.util;

import java.util.Arrays;
import java.util.List;

public class VersionUtils {

	private static boolean upToDate = true;
	public static String   downloadLink;

	/*
	 * compare two version strings
	 * 
	 * @param v1 first version string
	 * 
	 * @param v2 the version string to compare
	 * 
	 * @return 0 - if both are same, 1 - if v1 > v2 and -1 - if v2 > v1
	 */
	public static int compareVersions(String v1, String v2) {
		v1 = v1.replaceAll("\\s", "");
		v2 = v2.replaceAll("\\s", "");
		String[] a1 = v1.split("\\.");
		String[] a2 = v2.split("\\.");
		List<String> l1 = Arrays.asList(a1);
		List<String> l2 = Arrays.asList(a2);

		int i = 0;
		while (true) {
			Double d1 = null;
			Double d2 = null;

			try {
				d1 = Double.parseDouble(l1.get(i));
			} catch (IndexOutOfBoundsException e) {
			}

			try {
				d2 = Double.parseDouble(l2.get(i));
			} catch (IndexOutOfBoundsException e) {
			}

			if (d1 != null && d2 != null) {
				if (d1.doubleValue() > d2.doubleValue())
					return 1;
				else if (d1.doubleValue() < d2.doubleValue())
					return -1;
			} else if (d2 == null && d1 != null) {
				if (d1.doubleValue() > 0)
					return 1;
			} else if (d1 == null && d2 != null) {
				if (d2.doubleValue() > 0)
					return -1;
			} else
				break;
			i++;
		}
		return 0;
	}

	public static boolean isUpToDate() {
		return upToDate;
	}

	public static void setUpToDate(boolean upToDate) {
		VersionUtils.upToDate = upToDate;
	}
}
