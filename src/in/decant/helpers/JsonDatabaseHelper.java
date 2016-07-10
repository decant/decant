package in.decant.helpers;

import in.decant.models.SMSMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

public class JsonDatabaseHelper {
	private String jsonDatabase;
	private Boolean isDatabaseEmpty;
	private static String TAG = "JsonDatabase";

	public void initializeDatabase(List<SMSMessage> messages, String pluginName) {
		jsonDatabase = "{ \"messages\": { ";

		isDatabaseEmpty = true;
		for (int i = 0; i < messages.size(); i++) {
			if (messages.get(i).getParsedPlugin() == pluginName) {
				if (messages.get(i).getTokenizedJSONMessage().length() > 2) {
					jsonDatabase += "\"" + messages.get(i).getId() + "\": "
							+ messages.get(i).getTokenizedJSONMessage();
					if (i + 1 < messages.size()) {
						jsonDatabase += ",";
					}
					isDatabaseEmpty = false;
				}
			}
		}

		if (jsonDatabase.endsWith(",")) {
			jsonDatabase = jsonDatabase.substring(0, jsonDatabase.length() - 1);
		}

		jsonDatabase += "} }";

		DebugHelper.ShowMessage.d(TAG, jsonDatabase);
	}

	public List<String> getAllValues(String key) {
		List<String> valueList = JsonPath.parse(jsonDatabase).read(
				"$.messages.*." + key);

		return valueList;
	}

	public List<String> getUniqueValues(String key) {
		List<String> valueList = JsonPath.parse(jsonDatabase).read(
				"$.messages.*." + key);

		Set<String> valueUniqueList = new HashSet<String>();

		valueUniqueList.addAll(valueList);
		valueList.clear();
		valueList.addAll(valueUniqueList);

		return valueList;
	}

	public int getValueCount(String key, String value) {
		Configuration conf = Configuration.builder()
				.options(Option.AS_PATH_LIST).build();

		List<String> indexes = JsonPath.using(conf).parse(jsonDatabase)
				.read("$.messages.*.[?(@." + key + " == '" + value + "')]");
		return indexes.size();
	}

	public List<Map<String, Object>> getAllValues(String[] keys) {
		List<Map<String, Object>> valueList = JsonPath.parse(jsonDatabase)
				.read("$.messages.*.[" + implode(",", keys) + "]");

		return valueList;
	}

	public List<String> getAllIDs() {
		Configuration conf = Configuration.builder()
				.options(Option.AS_PATH_LIST).build();

		List<String> paths = JsonPath.using(conf).parse(jsonDatabase)
				.read("$.messages.*");

		List<String> indexes = new ArrayList<String>();

		for (int i = 0; i < paths.size(); i++) {
			indexes.add(paths.get(i).replaceAll("[^0-9]", ""));
		}

		return indexes;
	}

	private String implode(String separator, String[] data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length - 1; i++) {
			// data.length - 1 => to not add separator at the end
			if (!data[i].matches(" *")) {// empty string are ""; " "; "  "; and
											// so on
				sb.append("\"" + data[i] + "\"");
				sb.append(separator);
			}
		}
		sb.append("\"" + data[data.length - 1].trim() + "\"");
		return sb.toString();
	}

	public Boolean getIsDatabaseEmpty() {
		return isDatabaseEmpty;
	}
}
