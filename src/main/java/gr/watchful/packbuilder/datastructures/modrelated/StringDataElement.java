package gr.watchful.packbuilder.datastructures.modrelated;

import java.util.*;

/**
 * A mod info element to keep track of source and update time for a string
 */
public class StringDataElement {
	private HashSet<String> values;

	private ArrayList<String> unknownValue;
	private ArrayList<String> localHardValue;
	private ArrayList<String> ftbSiteValue;
	private ArrayList<String> notEnoughModsValue;
	private ArrayList<String> curseValue;
	private ArrayList<String> localUserValue;

	public StringDataElement() {
		values = new HashSet<>();

		unknownValue = new ArrayList<>();
		localHardValue = new ArrayList<>();
		ftbSiteValue = new ArrayList<>();
		notEnoughModsValue = new ArrayList<>();
		curseValue = new ArrayList<>();
		localUserValue = new ArrayList<>();
	}

	public void addValue(String value, DataSource source) {
		if (value == null || value.equals("")) return;
		switch (source) {
			case LOCALHARD:
				checkAdd(localHardValue, value);
				break;
			case FTBSITE:
				checkAdd(ftbSiteValue, value);
				break;
			case NOTENOUGHMODS:
				checkAdd(notEnoughModsValue, value);
				break;
			case CURSE:
				checkAdd(curseValue, value);
				break;
			case LOCALUSER:
				checkAdd(localUserValue, value);
				break;
			default:
				checkAdd(unknownValue, value);
				break;
		}
	}

	private void checkAdd(ArrayList<String> currentArray, String value) {
		if (!currentArray.contains(value)) {
			currentArray.add(value);
		}
	}

	public ArrayList<String> getValues() {
		ArrayList<String> outputValues =  new ArrayList<>();

		if (localUserValue.size() > 0) {
			outputValues.addAll(localUserValue);
		} else {
			addUnique(outputValues, curseValue);
			addUnique(outputValues, notEnoughModsValue);
			addUnique(outputValues, ftbSiteValue);
			addUnique(outputValues, localHardValue);
			addUnique(outputValues, unknownValue);
		}
		return outputValues;
	}

	public ArrayList<String> getSpecificValues(DataSource source) {
		switch (source) {
			case LOCALHARD:
				return localHardValue;
			case FTBSITE:
				return ftbSiteValue;
			case NOTENOUGHMODS:
				return notEnoughModsValue;
			case CURSE:
				return curseValue;
			case LOCALUSER:
				return localUserValue;
			default:
				return unknownValue;
		}
	}
	
	private void addUnique(ArrayList<String> master, ArrayList<String> sub) {
		for (String str : sub) {
			if (!master.contains(str)) {
				master.add(str);
			}
		}
	}

	private class StringSources {
		private String str;
		private EnumSet<DataSource> sources;

		public StringSources(String str, DataSource source) {
			this.str = str;
			sources = EnumSet.of(source);
		}

		public String getStr() {
			return str;
		}

		public void addSource(DataSource source) {
			sources.add(source);
		}

		public boolean containsSource(DataSource source) {
			return sources.contains(source);
		}

		@Override
		public int hashCode() {
			return str.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof StringSources)) return false;
			if (o == this) return true;
			return str.equals(((StringSources) o).getStr());
		}
	}
}
