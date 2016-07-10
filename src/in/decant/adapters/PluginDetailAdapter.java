package in.decant.adapters;

import in.decant.decant.R;
import in.decant.models.PluginDetailModel;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PluginDetailAdapter extends BaseAdapter {

	private Context context;
	private List<PluginDetailModel> pluginDetailModel;
	private final int[] columnIds = { R.id.column1, R.id.column2, R.id.column3 };

	public static class ViewHolder {
		public TextView[] columnText;

		public ViewHolder(int columns) {
			columnText = new TextView[columns];
		}
	}

	public PluginDetailAdapter(Context context,
			List<PluginDetailModel> pluginListModel) {
		super();
		this.context = context;
		this.pluginDetailModel = pluginListModel;
	}

	@Override
	public int getCount() {
		return pluginDetailModel.size();
	}

	@Override
	public PluginDetailModel getItem(int arg0) {
		return pluginDetailModel.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View filteredDataRow = convertView;
		int totalColumns = pluginDetailModel.get(position).getTotalColumns();

		if (filteredDataRow == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			filteredDataRow = inflater.inflate(R.layout.filter_single_row,
					parent, false);

			ViewHolder viewHolder = new ViewHolder(totalColumns);

			for (int i = 0; i < totalColumns; i++) {
				viewHolder.columnText[i] = (TextView) filteredDataRow
						.findViewById(columnIds[i]);
			}

			filteredDataRow.setTag(viewHolder);

			for (int i = 0; i < totalColumns; i++) {
				viewHolder.columnText[i].setText(pluginDetailModel
						.get(position).getColumnN(i));
			}
		} else {
			ViewHolder viewHolder = (ViewHolder) filteredDataRow.getTag();

			for (int i = 0; i < totalColumns; i++) {
				viewHolder.columnText[i].setText(pluginDetailModel
						.get(position).getColumnN(i));
			}
		}

		return filteredDataRow;
	}
}
