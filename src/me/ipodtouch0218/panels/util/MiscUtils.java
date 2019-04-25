package me.ipodtouch0218.panels.util;

public class MiscUtils {

	private MiscUtils() {}
	
	public static int limit(int number, int lowerlimit, int higherlimit) {
		return Math.max(lowerlimit, Math.min(number, higherlimit));
	}
	
}
