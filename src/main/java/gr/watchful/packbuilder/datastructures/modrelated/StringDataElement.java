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
		addSource(value, source);
	}

	private void addSource(String value, DataSource source) {
		if (sources.containsKey(source)) {
			sources.get(source).add(value);
		} else {
			HashSet<String> temp = new HashSet<>();
			temp.add(value);
			sources.put(source, temp);
		}
	}
}
