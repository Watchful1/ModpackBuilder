package gr.watchful.packbuilder.datastructures.modrelated;

import java.util.ArrayList;
import java.util.logging.Logger;

public class SimpleModInfo {
	private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public String name;
	public ArrayList<String> author;
	public ArrayList<String> modid;
	public String url;
	public DataSource source;

	public SimpleModInfo(DataSource source) {
		this.source = source;

		author = new ArrayList<>();
		modid = new ArrayList<>();
	}

	public void setName(String name) {
		logger.fine("  Parsing "+name+" from "+source);
		this.name = name;
	}

	public void setUrl(String url) {
		logger.finer("    Parsing "+url+" from "+source);
		this.url = url;
	}

	public void setAuthor(String authors) {
		logger.finer("    Parsing "+authors+" from "+source);
		for (String author : authors.split(",\\s*")) {
			this.author.add(author);
		}
	}

	public void setModid(String modids) {
		logger.finer("    Parsing "+modids+" from "+source);
		for (String modid : modids.split(",\\s*")) {
			this.modid.add(modid);
		}
	}
}
