package gr.watchful.packbuilder.datastructures.modrelated;

import java.io.File;
import java.util.ArrayList;

public class ModFile {
	public File file;
	public ArrayList<String> modIDs;
	public ArrayList<String> codeVersion;

	public String mcmodVersion;
	public String mcmodName;
	public String mcmodID;

	public ModFile(File file) {
		this.file = file;
		modIDs = new ArrayList<>();

		codeVersion = new ArrayList<>();
	}

	public void addID(String ID) {
		modIDs.add(ID);
	}
}
