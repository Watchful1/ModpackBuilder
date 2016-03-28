package gr.watchful.packbuilder.utils;

import gr.watchful.packbuilder.settings.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class FileUtils {
	public static String getMD5(File file) {
		try (FileInputStream inputStream = new FileInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance("MD5");

			byte[] bytesBuffer = new byte[1024];
			int bytesRead = -1;

			while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				digest.update(bytesBuffer, 0, bytesRead);
			}

			byte[] hashedBytes = digest.digest();

			return convertByteArrayToHexString(hashedBytes);
		} catch (NoSuchAlgorithmException | IOException ex) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("Couldn't generate hash for file: " + file.getAbsolutePath());
			return null;
		}
	}

	private static String convertByteArrayToHexString(byte[] arrayBytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return stringBuffer.toString();
	}

	public static String getFileExtension(File file) {
		if(file == null || !file.exists()) return null;
		int extPos = file.getAbsolutePath().lastIndexOf(".");
		int sepPos = file.getAbsolutePath().lastIndexOf(File.pathSeparator);
		if(extPos == -1) return null;
		if(sepPos > extPos) return null;
		return file.getAbsolutePath().substring(extPos+1);
	}

	public static File getAppStore() {
		switch (OsTypes.getOperatingSystemType()) {
			case WINDOWS:
				return new File(System.getenv("APPDATA") + File.separator + Constants.projectName);
			case MACOS:
				return new File(System.getProperty("user.home") + File.separator +
						"Library/Application Support" + File.separator + Constants.projectName);
			case LINUX:
				return new File(System.getProperty("user.home") + File.separator + "." + Constants.projectName);
			default:
				return new File("." + Constants.projectName);
		}
	}

	public static boolean downloadToFile(String url, File destination) {
		try {
			org.apache.commons.io.FileUtils.copyURLToFile(new URL(url), destination, 1000, 1000);
		} catch (IOException e) {
			LogUtils.logException("Exception downloading file: " +
					destination.getAbsolutePath() + " from url: " + url, e);
			return false;
		}
		return true;
	}

	public static String readFileToString(File source) {
		String output;
		try {
			output = org.apache.commons.io.FileUtils.readFileToString(source);
		} catch (IOException e) {
			LogUtils.logException("Unable to read file to string: " + source.getAbsolutePath(), e);
			return null;
		}
		return output;
	}
}
