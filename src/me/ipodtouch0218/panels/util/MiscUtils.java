package me.ipodtouch0218.panels.util;

public class MiscUtils {

	private MiscUtils() {}
	
	public static double limit(double number, double lowerlimit, double higherlimit) {
		return Math.max(lowerlimit, Math.min(number, higherlimit));
	}
	public static int limit(int number, int lowerlimit, int higherlimit) {
		return (int) limit((double) number, lowerlimit, higherlimit);
	}
}
