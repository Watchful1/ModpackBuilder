package gr.watchful.packbuilder.datastructures.modrelated;

import java.util.*;

/**
 * A mod info element to keep track of source and update time for a string
 */
public class StringDataElement {
	private HashMap<String, EnumSet<DataSource>> values;
	private HashMap<DataSource, HashSet<String>> sources;

	public StringDataElement() {
		values = new HashMap<>();
		sources = new HashMap<>();
	}

	public void addValue(String value, DataSource source) {
		if (value == null || value.equals("")) return;
		EnumSet<DataSource> temp = values.get(value);
		if (temp == null) {
			values.put(value, EnumSet.of(source));
		} else {
			temp.add(source);
		}
		if (sources.containsKey(source)) {
			sources.get(source).add(value);
		} else {
			HashSet<String> temp2 = new HashSet<>();
			temp2.add(value);
			sources.put(source, temp2);
		}
	}

	public HashSet<String> getSourceValues(DataSource source) {
		return sources.get(source);
	}

	public HashSet<String> getBestValues() {
		return null;
	}
}
