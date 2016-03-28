package gr.watchful.packbuilder.datastructures.modrelated;

/**
 * Information about a specific version of a mod
 * Most of the fields are not used by default, but override the ModInfo fields if present
 */
public class ModVersion {
	private String version;
	private String source;
	private String minecraftVersion;
	private String md5;
}
