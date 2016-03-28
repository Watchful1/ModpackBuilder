package gr.watchful.packbuilder.utils;

public class OsTypes {
	public enum OSType {
		WINDOWS, MACOS, LINUX, OTHER
	};

	private static OSType detectedOS;

	public static OSType getOperatingSystemType() {
		if (detectedOS == null) {
			String OS = System.getProperty("os.name", "generic").toLowerCase();
			if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
				detectedOS = OSType.MACOS;
			} else if (OS.indexOf("win") >= 0) {
				detectedOS = OSType.WINDOWS;
			} else if (OS.indexOf("nux") >= 0) {
				detectedOS = OSType.LINUX;
			} else {
				detectedOS = OSType.OTHER;
			}
		}
		return detectedOS;
	}
}
