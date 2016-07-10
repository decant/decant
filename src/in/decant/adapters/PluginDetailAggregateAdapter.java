package in.decant.adapters;

import in.decant.decant.R;
import in.decant.models.PluginDetailAggregateModel;
import in.decant.models.SMSMessage;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PluginDetailAggregateAdapter extends BaseAdapter {

	private Context context;
	private String key;
	private List<PluginDetailAggregateModel> pluginDetailAggregateModel;
	private final int[] columnIds = { R.id.column1, R.id.column2, R.id.column3 };

	public static class ViewHolder {
		public TextView[] columnText;

		public ViewHolder(int columns) {
			columnText = new TextView[columns];
		}
	}

	public PluginDetailAggregateAdapter(Context context,
			List<PluginDetailAggregateModel> pluginListModel) {
		super();
		this.context = context;
		this.pluginDetailAggregateModel = pluginListModel;
		this.key = "";
	}

	@Override
	public int getCount() {
		return pluginDetailAggregateModel.size();
	}

	@Override
	public Object getItem(int arg0) {
		return pluginDetailAggregateModel.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View filteredPnrRow = convertView;
		int totalColumns = pluginDetailAggregateModel.get(position)
				.getTotalColumns() + 1; // One additional column is for the
										// label

		if (filteredPnrRow == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			filteredPnrRow = inflater.inflate(
					R.layout.filter_aggregate_single_row, parent, false);

			ViewHolder viewHolder = new ViewHolder(totalColumns);

			for (int i = 0; i < totalColumns; i++) {
				viewHolder.columnText[i] = (TextView) filteredPnrRow
						.findViewById(columnIds[i]);
			}

			filteredPnrRow.setTag(viewHolder);

			for (int i = 0; i < totalColumns; i++) {
				switch (i) {
				case 0:
					viewHolder.columnText[i].setText(SMSMessage
							.getDisplayName(key));
					break;

				case 1:
					viewHolder.columnText[i].setText(pluginDetailAggregateModel
							.get(position).getColumnN(i - 1));
					break;

				case 2:
					if (Integer.parseInt(pluginDetailAggregateModel.get(
							position).getColumnN(i - 1)) > 0) {
						viewHolder.columnText[i]
								.setText(pluginDetailAggregateModel.get(
										position).getColumnN(i - 1));
					} else {
						viewHolder.columnText[i].setText("");
					}
					break;
				}
			}
		} else {
			ViewHolder viewHolder = (ViewHolder) filteredPnrRow.getTag();

			for (int i = 0; i < totalColumns; i++) {
				switch (i) {
				case 0:
					viewHolder.columnText[i].setText(SMSMessage
							.getDisplayName(key));
					break;

				case 1:
					viewHolder.columnText[i].setText(pluginDetailAggregateModel
							.get(position).getColumnN(i - 1));
					break;

				case 2:
					if (Integer.parseInt(pluginDetailAggregateModel.get(
							position).getColumnN(i - 1)) > 0) {
						viewHolder.columnText[i]
								.setText(pluginDetailAggregateModel.get(
										position).getColumnN(i - 1));
					} else {
						viewHolder.columnText[i].setText("");
					}
					break;
				}
			}
		}

		return filteredPnrRow;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
