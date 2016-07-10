package in.decant.models;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PluginDetailModel {
	private List<String> values;
	private String id;

	public PluginDetailModel(List<String> header) {
		this.values = new ArrayList<String>(header);
		this.id = "header";
	}

	public PluginDetailModel(Map<String, Object> values, String id) {
		super();
		this.values = new ArrayList<String>();
		for (Object value : values.values()) {
			if (value instanceof String) {
				this.values.add((String) value);
			}
		}
		this.id = id;
	}

	public String getColumnN(int n) {
		String value = "";
		if (this.id == "header") {
			value = SMSMessage.getDisplayName(values.get(n));
		} else {
			value = values.get(n);
		}
		return value;
	}

	public int getTotalColumns() {
		return values.size();
	}

	public String getId() {
		return id;
	}

}
