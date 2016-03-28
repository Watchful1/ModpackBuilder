package gr.watchful.packbuilder.datastructures.global;

import gr.watchful.packbuilder.settings.Constants;
import gr.watchful.packbuilder.utils.FileUtils;

import java.io.File;

public class Preferences {
	public static File dataCache;

	public static void init() {
		if (dataCache == null) dataCache = new File(FileUtils.getAppStore() + File.separator + "modDataCache");

		System.setProperty("http.agent", "Watchful-PackBuilder/"+ Constants.version);
	}
}
