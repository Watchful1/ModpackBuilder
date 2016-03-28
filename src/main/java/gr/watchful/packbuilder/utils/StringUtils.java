package gr.watchful.packbuilder.utils;

public class StringUtils {
	public static String compressString(String input) {
		return input.toLowerCase().replaceAll("[^a-z0-9]", "");
	}
}
