package in.decant.decant;

import in.decant.adapters.PluginDetailAdapter;
import in.decant.adapters.PluginDetailAggregateAdapter;
import in.decant.helpers.DebugHelper;
import in.decant.helpers.JsonDatabaseHelper;
import in.decant.helpers.ScriptExecuterHelper;
import in.decant.models.PluginDetailAggregateModel;
import in.decant.models.PluginDetailModel;
import in.decant.models.SMSMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PluginDetailActivity extends ActionBarActivity implements
		OnItemSelectedListener, OnItemClickListener {
	ScriptExecuterHelper script;
	String pluginName;
	List<SMSMessage> messages;
	ListView messageAggregateList;
	ListView messageList;
	Spinner filterOptions;
	ArrayAdapter<String> filterKeysAdapter;
	JsonDatabaseHelper jsondb;
	List<PluginDetailAggregateModel> pluginDetailAggregateModels;
	PluginDetailAggregateAdapter pluginDetailAggregateAdapter;
	List<PluginDetailModel> pluginDetailModels;
	PluginDetailAdapter pluginDetailAdapter;

	static String TAG = "PluginDetailActivty";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_detail);

		Gson gson = new Gson();

		Intent intent = getIntent();

		SharedPreferences sp = this.getSharedPreferences("messageList",
				Context.MODE_PRIVATE);
		String rawMessages = sp.getString("SMSMessages", null);

		pluginName = intent.getStringExtra("pluginName");

		messages = gson.fromJson(rawMessages,
				new TypeToken<List<SMSMessage>>() {
				}.getType());

		DebugHelper.ShowMessage.d(TAG, "Total messages: " + messages.size());

		if (messages.size() > 0) {
			script = new ScriptExecuterHelper();

			messageAggregateList = (ListView) findViewById(R.id.messageAggregateList);
			messageList = (ListView) findViewById(R.id.messageList);
			filterOptions = (Spinner) findViewById(R.id.filterOptions);

			messageAggregateList.setVisibility(ListView.INVISIBLE);
			messageList.setVisibility(ListView.VISIBLE);

			parseStoredData();

			generateDatabase();

			if (jsondb.getIsDatabaseEmpty() == false) {
				initialize();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plugin_detail, menu);
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

	/**
	 * This function parses the SMS messages one by one for a given script name.
	 * Based on the logic inside the script it executes various Javascript
	 * functions and then stores the parsed data in the SMSMessages object
	 */
	private void parseStoredData() {
		// Loads the executable Javascript code
		script.readScriptFromExternal(pluginName);

		// Executes the function name() and shows a debug output
		DebugHelper.ShowMessage.d(TAG,
				"Executing script: " + script.executeFunction("name"));

		// Iterate through each message, and filter out the messages that we
		// need.
		// This process is two step
		// 1. Run the filter() function filter only messages the plugin is
		// interested in
		// 2. Run the extractor() function over the messages to extract relevant
		// data
		for (int i = 0; i < messages.size(); i++) {
			if (script.executeBooleanFunction("filter", messages.get(i)
					.getIdentificationHeader()) == true) {
				// Get the tokenized message
				String tokenizedJSONMessage = script.executeFunction(
						"extractor", messages.get(i).getRawMessage());
				messages.get(i).setTokenizedJSONMessage(tokenizedJSONMessage);
				messages.get(i).setParsedPlugin(pluginName);
			}
		}

		// Store the set of Filter Keys on which we can sort / search the parsed
		// data set, the data is got by the execution of filterkeys() function.
		SMSMessage.clearFilterKeys();

		JSONObject filterKeysJSON;
		try {
			filterKeysJSON = new JSONObject(
					script.executeFunction("filterKeys"));
			Iterator<String> keys = filterKeysJSON.keys();

			while (keys.hasNext()) {
				SMSMessage
						.addFilterKey((String) filterKeysJSON.get(keys.next()));
			}
			Collections.reverse(SMSMessage.getFilterKeys());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

		// Finally we get the various settings like the friendly name of columns
		// to be displayed or which columns should be displayed and which all
		// should remain hidden. This is done by executing the settings()
		// function.
		SMSMessage.clearSettings();

		JSONObject settingsJSON;
		try {
			settingsJSON = new JSONObject(script.executeFunction("settings"));

			Iterator<String> keys = settingsJSON.keys();

			while (keys.hasNext()) {
				String settingName = (String) keys.next();
				JSONObject settingValuesJSON = settingsJSON
						.getJSONObject(settingName);
				Iterator<String> settingValuesKeys = settingValuesJSON.keys();
				Map<String, String> settingValues = new HashMap<String, String>();

				DebugHelper.ShowMessage.d(TAG, settingName);
				while (settingValuesKeys.hasNext()) {
					String settingValueKey = settingValuesKeys.next();

					settingValues.put(settingValueKey,
							settingValuesJSON.getString(settingValueKey));
					DebugHelper.ShowMessage.d(TAG, settingValueKey + " "
							+ settingValuesJSON.getString(settingValueKey));
				}

				SMSMessage.addSettings(settingName, settingValues);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * This function uses the parsed JSON data from the parseStoredData() to
	 * make a queryable JSON database, which is used for sorting, filtering and
	 * pulling relevant information. The JSON database is store in a
	 * JsonDatabase Object, which has abstracted many of the querying and
	 * filtering calls.
	 */
	private void generateDatabase() {
		jsondb = new JsonDatabaseHelper();

		jsondb.initializeDatabase(messages, pluginName);
	}

	/**
	 * Initializes the various UI and display elements, bindings and callbacks
	 * when and where needed, this is the place that deals with information
	 * parsed and stored from messages being displayed to the Android screen.
	 */
	private void initialize() {
		// Initialize the various variables used for binding, models, adapters
		// layouts etc for the UI

		// Initialize the filterKeys
		// Bind it to the Drop down
		List<String> filterKeysList = new ArrayList<String>(
				SMSMessage.getDisplayNameFilterKeys());
		filterKeysList.add(0, "Select Filter");

		filterKeysAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, filterKeysList);

		filterKeysAdapter
				.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);

		filterOptions.setAdapter(filterKeysAdapter);

		// Initialize the Table of filtered data for display in UI
		// This involves the Data Models and their binding with Data Adapters
		pluginDetailAggregateModels = new ArrayList<PluginDetailAggregateModel>();
		pluginDetailModels = new ArrayList<PluginDetailModel>();

		List<Map<String, Object>> valuesList = jsondb.getAllValues(SMSMessage
				.getDisplayColumns().toArray(new String[0]));
		List<String> idList = jsondb.getAllIDs();

		PluginDetailModel pluginDetailHeader = new PluginDetailModel(
				SMSMessage.getDisplayColumns());
		pluginDetailModels.add(pluginDetailHeader);
		for (int i = 0; i < valuesList.size(); i++) {
			DebugHelper.ShowMessage.d(idList.get(i));
			PluginDetailModel pluginDetailModel = new PluginDetailModel(
					valuesList.get(i), idList.get(i));
			for (String key : valuesList.get(i).keySet()) {
				DebugHelper.ShowMessage.d(TAG, key + " : "
						+ valuesList.get(i).get(key));
			}

			pluginDetailModels.add(pluginDetailModel);
		}

		pluginDetailAdapter = new PluginDetailAdapter(this, pluginDetailModels);

		messageList.setAdapter(pluginDetailAdapter);

		filterOptions.setOnItemSelectedListener(this);
		messageList.setOnItemClickListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == R.id.filterOptions) {
			if (parent.getItemAtPosition(position).toString()
					.equals("Select Filter") == false) {
				messageList.setVisibility(ListView.INVISIBLE);
				messageAggregateList.setVisibility(ListView.VISIBLE);
				applyFilter(parent.getItemAtPosition(position).toString());
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	private void applyFilter(String displayName) {
		String key = SMSMessage.getName(displayName);
		List<String> valuesList = jsondb.getUniqueValues(key);

		pluginDetailAggregateModels.clear();

		for (int i = 0; i < valuesList.size(); i++) {
			PluginDetailAggregateModel pluginDetailModel = new PluginDetailAggregateModel(
					valuesList.get(i), ""
							+ jsondb.getValueCount(key, valuesList.get(i)));
			pluginDetailAggregateModels.add(pluginDetailModel);
		}

		if (pluginDetailAggregateAdapter == null) {
			pluginDetailAggregateAdapter = new PluginDetailAggregateAdapter(
					this, pluginDetailAggregateModels);

			messageAggregateList.setAdapter(pluginDetailAggregateAdapter);
		}

		pluginDetailAggregateAdapter.setKey(key);
		pluginDetailAggregateAdapter.notifyDataSetChanged();
	}

	public void resetList(View view) {
		messageList.setVisibility(ListView.VISIBLE);
		messageAggregateList.setVisibility(ListView.GONE);
		DebugHelper.ShowMessage.t(this, "pressed reset");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == R.id.messageList) {
			PluginDetailModel pda = (PluginDetailModel) parent.getAdapter()
					.getItem(position);

			int thread_id = 0;
			String selection = "_id = " + pda.getId();
			Uri uri = Uri.parse("content://sms");
			ContentResolver contentResolver = getContentResolver();
			Cursor cursor = contentResolver.query(uri, new String[] { "_id",
					"thread_id", "address", "person", "date", "body", "type" },
					selection, null, null);

			startManagingCursor(cursor);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					thread_id = cursor.getInt(1);
				}
			}

			Intent defineIntent = new Intent(Intent.ACTION_VIEW);
			defineIntent.setData(Uri.parse("content://mms-sms/conversations/"
					+ thread_id));
			startActivity(defineIntent);
		}

	}
}
