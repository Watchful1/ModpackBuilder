package gr.watchful.packbuilder.moddetection;

import gr.watchful.packbuilder.datastructures.global.Preferences;
import gr.watchful.packbuilder.datastructures.modrelated.ModInfo;
import gr.watchful.packbuilder.settings.Constants;
import gr.watchful.packbuilder.utils.FileUtils;
import gr.watchful.packbuilder.utils.LogUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ModLibrary {
	private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static ArrayList<ModInfo> modInfos;

	public static void init() {
		modInfos = new ArrayList<>();

		initFTBSite();
	}

	private static void initFTBSite() {
		File tempFile = new File(Preferences.dataCache + File.separator + "FTBSite.json");
		File cacheFile = new File(tempFile + ".cache");

		boolean downloadResult = FileUtils.downloadToFile(Constants.ftbUrl, tempFile);
		boolean parseResult = false;
		if (downloadResult) {
			parseResult = processFTBFile(tempFile);
		}
		if (!downloadResult || !parseResult) {
			if (!downloadResult) logger.warning("Could not download FTB json, reverting to cache");
			else if (!parseResult)  logger.warning("Could not parse FTB json, reverting to cache");
			if (!cacheFile.exists()) {
				logger.warning("FTB json cache does not exist, skipping FTB");
				return;
			}

			parseResult = processFTBFile(cacheFile);

			if (!parseResult) logger.warning("Could not parse FTB cache json, skipping FTB");
		}

	}

	private static boolean processFTBFile(File json) {
		String jsonString = FileUtils.readFileToString(json);
		if (jsonString == null) return false;

		JSONParser parser = new JSONParser();
		try {
			JSONArray array = (JSONArray) parser.parse(jsonString);

			for (Object modObject : array) {
				JSONObject jsonObject = (JSONObject) modObject;
				logger.info((String) jsonObject.get("modName"));

			}
		} catch (ParseException e) {
			LogUtils.logException("Failed to parse FTB json", e);
			return false;
		}

		return true;
	}
}
