package in.decant.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMSMessage {
	private long id;
	private String identificationHeader;
	private String rawMessage;
	private String tokenizedJSONMessage;
	private String dateTime;
	private String parsedPlugin;
	private static List<String> filterKeys = new ArrayList<String>();
	private static Map<String, Map<String, String>> settings = new HashMap<String, Map<String, String>>();

	public SMSMessage(long id, String identificationHeader, String rawMessage,
			String dateTime) {
		this.id = id;
		this.identificationHeader = identificationHeader;
		this.rawMessage = rawMessage;
		this.dateTime = dateTime;
		this.tokenizedJSONMessage = "";
		this.parsedPlugin = "";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdentificationHeader() {
		return identificationHeader;
	}

	public void setIdentificationHeader(String identificationHeader) {
		this.identificationHeader = identificationHeader;
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}

	public String getTokenizedJSONMessage() {
		return tokenizedJSONMessage;
	}

	public void setTokenizedJSONMessage(String tokenizedJSONMessage) {
		this.tokenizedJSONMessage = tokenizedJSONMessage;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public static List<String> getFilterKeys() {
		return filterKeys;
	}

	public static void setFilterKeys(List<String> filterKeys) {
		SMSMessage.filterKeys = filterKeys;
	}

	public static void addFilterKey(String filterKey) {
		filterKeys.add(filterKey);
	}

	public static void clearFilterKeys() {
		filterKeys.clear();
	}

	public static void setSettings(Map<String, Map<String, String>> settings) {
		SMSMessage.settings = settings;
	}

	public static void addSettings(String settingName,
			Map<String, String> settingValues) {
		settings.put(settingName, settingValues);
	}

	public static Map<String, String> getSettings(String settingsName) {
		return settings.get(settingsName);
	}

	public static void clearSettings() {
		settings.clear();
	}

	public static String getDisplayName(String name) {
		Map<String, String> displayNameValueMapping = settings
				.get("display_name");

		return displayNameValueMapping.get(name);
	}

	public static String getName(String displayName) {
		Map<String, String> displayNameValueMapping = settings
				.get("display_name");

		String name = "";

		for (Map.Entry<String, String> displayNameValuePair : displayNameValueMapping
				.entrySet()) {
			String displayNameValue = displayNameValuePair.getValue();
			if (displayNameValue.equals(displayName)) {
				name = displayNameValuePair.getKey();
			}
		}

		return name;
	}

	public static List<String> getDisplayNameFilterKeys() {
		List<String> displayNameFilterKeys = new ArrayList<String>();

		for (int i = 0; i < filterKeys.size(); i++) {
			displayNameFilterKeys.add(getDisplayName(filterKeys.get(i)));
		}

		return displayNameFilterKeys;
	}

	public static List<String> getDisplayColumns() {
		List<String> displayColumnsList = new ArrayList<String>();

		Map<String, String> allowedDisplayColumns = settings
				.get("is_displayed");

		for (Map.Entry<String, String> displayColumn : allowedDisplayColumns
				.entrySet()) {
			String displayColumnValue = displayColumn.getValue();
			if (displayColumnValue.equals("true")) {
				displayColumnsList.add(displayColumn.getKey());
			}
		}

		return displayColumnsList;
	}

	public String getParsedPlugin() {
		return parsedPlugin;
	}

	public void setParsedPlugin(String parsedPlugin) {
		this.parsedPlugin = parsedPlugin;
	}
}
