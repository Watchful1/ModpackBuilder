package gr.watchful.packbuilder.datastructures.modrelated;

/**
 * An instance of a mod.
 * Contains a specific jar file and mod version
 * Points to a ModInfo with general details about the mod
 */
public class Mod {
	private ModInfo modInfo;
	private String fileName;
	private String version;
	private ModpackVersion parentModpackVersion;
		// not entirely sure this is the right way to implement this, but I really don't want to
		// pass it around everywhere
	private String source; // defaults to the modInfo, version source if not present
}
