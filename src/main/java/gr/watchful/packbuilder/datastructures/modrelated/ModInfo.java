package gr.watchful.packbuilder.datastructures.modrelated;

import java.util.ArrayList;

/**
 * A set of general details about a mod
 */
public class ModInfo {
	public StringDataElement name;
	public StringDataElement author;
	public StringDataElement modid;
	public StringDataElement url;

	public ModInfo(SimpleModInfo simpleModInfo, DataSource source) {
		name = new StringDataElement();
		author = new StringDataElement();
		modid = new StringDataElement();
		url = new StringDataElement();

		addSimpleModInfo(simpleModInfo, source);
	}

	public void addSimpleModInfo(SimpleModInfo simpleModInfo, DataSource source) {
		name.addValue(simpleModInfo.name, source);
		for (String authorStr : simpleModInfo.author) {
			author.addValue(authorStr, source);
		}
		for (String modidStr : simpleModInfo.modid) {
			modid.addValue(modidStr, source);
		}
		url.addValue(simpleModInfo.url, source);
	}




	private ArrayList<ModVersion> versions;
}
