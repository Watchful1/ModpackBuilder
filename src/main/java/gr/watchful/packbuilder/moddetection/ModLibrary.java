package gr.watchful.packbuilder.moddetection;

import gr.watchful.packbuilder.datastructures.global.Preferences;
import gr.watchful.packbuilder.datastructures.modrelated.DataSource;
import gr.watchful.packbuilder.datastructures.modrelated.Mod;
import gr.watchful.packbuilder.datastructures.modrelated.ModInfo;
import gr.watchful.packbuilder.datastructures.modrelated.SimpleModInfo;
import gr.watchful.packbuilder.settings.Constants;
import gr.watchful.packbuilder.utils.FileUtils;
import gr.watchful.packbuilder.utils.LogUtils;
import gr.watchful.packbuilder.utils.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

public class ModLibrary {
	private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static ArrayList<ModInfo> modInfos;
	private static HashMap<String, ModInfo> modIds;
	private static HashMap<String, ModInfo> modNames;
	private static HashMap<String, ArrayList<ModInfo>> modAuthors;

	public static void init() {
		modInfos = new ArrayList<>();
		modIds = new HashMap<>();
		modNames = new HashMap<>();
		modAuthors = new HashMap<>();

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

		logger.info("Parsing FTB json");

		JSONParser parser = new JSONParser();
		int i = 0;
		try {
			JSONArray array = (JSONArray) parser.parse(jsonString);

			for (Object modObject : array) {
				JSONObject jsonObject = (JSONObject) modObject;
				SimpleModInfo tempInfo = new SimpleModInfo(DataSource.FTBSITE);

				tempInfo.setName((String) jsonObject.get("modName"));
				tempInfo.setAuthor((String) jsonObject.get("modAuthors"));
				tempInfo.setModid((String) jsonObject.get("modids"));
				tempInfo.setUrl((String) jsonObject.get("modLink"));

				saveModInfo(tempInfo, DataSource.FTBSITE);

				i++;
			}
		} catch (ParseException e) {
			LogUtils.logException("Failed to parse FTB json", e);
			return false;
		} finally {
			logger.info("Parsed mods from FTB json: "+i);
		}

		return true;
	}

	private static void saveModInfo(SimpleModInfo simpleModInfo, DataSource source) {
		ArrayList<ModInfo> tempInfos = findModInfo(simpleModInfo);
		if (tempInfos.size() > 0) {
			logger.info(simpleModInfo.name + " : " + tempInfos.size());
			logger.info("        " + simpleModInfo.name + " : " + simpleModInfo.author.toString() + " : " + simpleModInfo.url + " : " + simpleModInfo.modid);
			logger.info("        " + tempInfos.get(0).name.getSourceValues(DataSource.FTBSITE) + " : " + tempInfos.get(0).author.getSourceValues(DataSource.FTBSITE)
					+ " : " + tempInfos.get(0).url.getSourceValues(DataSource.FTBSITE) + " : " + tempInfos.get(0).modid.getSourceValues(DataSource.FTBSITE));
		}
		insertModInfo(simpleModInfo, source);
	}

	private static ArrayList<ModInfo> findModInfo(SimpleModInfo simpleModInfo) {
		return findModInfo(simpleModInfo, false);
	}

	private static ArrayList<ModInfo> findModInfo(SimpleModInfo simpleModInfo, boolean thorough) {
		HashSet<ModInfo> infos = new HashSet<>();
		if (simpleModInfo.modid.size() > 0) {
			for (String modid : simpleModInfo.modid) {
				ModInfo tempInfo = modIds.get(modid.toLowerCase());
				if (tempInfo != null) infos.add(tempInfo);
			}
		}
		if (simpleModInfo.name != null && !simpleModInfo.name.equals("")) {
			ModInfo tempInfo = modNames.get(StringUtils.compressString(simpleModInfo.name));
			if (tempInfo != null) infos.add(tempInfo);
		}
		if (thorough) {
			if (simpleModInfo.author.size() > 0) {
				for (String author : simpleModInfo.author) {
					ArrayList<ModInfo> tempInfo = modAuthors.get(author);
					if (tempInfo != null) infos.addAll(tempInfo);
				}
			}
		}

		return new ArrayList<>(infos);
	}

	private static void insertModInfo(SimpleModInfo simpleModInfo, DataSource source) {
		ModInfo tempInfo = new ModInfo(simpleModInfo, source);
		modInfos.add(tempInfo);
		modNames.put(StringUtils.compressString(simpleModInfo.name), tempInfo);
		for (String modId : simpleModInfo.modid) {
			modIds.put(modId, tempInfo);
		}
		for (String author : simpleModInfo.author) {
			modIds.put(author, tempInfo);
		}
	}
}
