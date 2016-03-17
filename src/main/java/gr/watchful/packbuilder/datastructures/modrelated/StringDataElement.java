package gr.watchful.packbuilder.datastructures.modrelated;

import com.sun.org.apache.xpath.internal.operations.String;

import java.util.ArrayList;

/**
 * A mod info element to keep track of source and update time for a string
 */
public class StringDataElement {
	private ArrayList<String> unknownValue;
	private ArrayList<String> localHardValue;
	private ArrayList<String> ftbSiteValue;
	private ArrayList<String> notEnoughModsValue;
	private ArrayList<String> curseValue;
	private ArrayList<String> localUserValue;

	public StringDataElement() {
		unknownValue = new ArrayList<>();
		localHardValue = new ArrayList<>();
		ftbSiteValue = new ArrayList<>();
		notEnoughModsValue = new ArrayList<>();
		curseValue = new ArrayList<>();
		localUserValue = new ArrayList<>();
	}

	public void addValue(String value, DataSource source) {
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
}
