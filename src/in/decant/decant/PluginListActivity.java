package in.decant.decant;

import in.decant.helpers.DebugHelper;
import in.decant.helpers.ScriptExecuterHelper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PluginListActivity extends ActionBarActivity implements
		OnItemClickListener {
	ArrayAdapter<String> pluginNamesAdapter;
	ListView pluginNamesList;
	static String TAG = "PluginListActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_list);

		pluginNamesList = (ListView) findViewById(R.id.pluginNamesList);

		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plugin_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialize() {

		pluginNamesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				ScriptExecuterHelper.getAllPluginFiles());

		pluginNamesList.setAdapter(pluginNamesAdapter);
		pluginNamesList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == R.id.pluginNamesList) {
			switch (parent.getItemAtPosition(position).toString()) {
			case "irctc.js":
			case "amazon.js":
			case "dominos.js":
				Intent intent = new Intent(this, PluginDetailActivity.class);
				intent.putExtra("pluginName", parent
						.getItemAtPosition(position).toString());
				startActivity(intent);
				break;

			default:
				DebugHelper.ShowMessage.t(this,
						parent.getItemAtPosition(position).toString());
				break;

			}
		}
	}
}
