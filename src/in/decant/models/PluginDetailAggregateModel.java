package in.decant.models;

import java.util.ArrayList;
import java.util.List;

public class PluginDetailAggregateModel {
	private List<String> values;

	public PluginDetailAggregateModel(String name, String count) {
		super();
		values = new ArrayList<String>();

		values.add(name);
		values.add(count);
	}

	public int getTotalColumns() {
		return values.size();
	}

	public String getColumnN(int n) {
		return values.get(n);
	}
}
