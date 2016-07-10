package in.decant.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import android.os.Environment;

public class ScriptExecuterHelper {
	String script;
	static String TAG = "ScriptExecuter";

	public void readScriptFromExternal(String scriptName) {
		File pluginDir = new File(Environment.getExternalStorageDirectory()
				+ "/decant/plugins");
		pluginDir.mkdirs();
		File fl = new File(pluginDir, scriptName);
		FileInputStream fin;
		StringBuilder sb = new StringBuilder();

		try {
			fin = new FileInputStream(fl);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fin));

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		script = sb.toString();
	}

	public String executeFunction(String functionName) {
		return executeFunction(functionName, "");
	}

	public String executeFunction(String functionName, String functionParams) {
		String result = "";

		// Every Rhino VM begins with the enter()
		// This Context is not Android's Context
		Context rhino = Context.enter();

		// Turn off optimization to make Rhino Android compatible
		rhino.setOptimizationLevel(-1);
		try {
			Scriptable scope = rhino.initStandardObjects();

			rhino.evaluateString(scope, script, "JavaScript", 1, null);

			// Get the name of the plugin defined in JavaScriptCode
			Object name = scope.get(functionName, scope);

			if (name instanceof Function) {
				Object[] params = new Object[] { functionParams };
				Function jsFunction = (Function) name;

				// Call the function with params
				Object jsResult = jsFunction.call(rhino, scope, scope, params);

				// Parse the jsResult object to a String
				result = Context.toString(jsResult);
			}

		} catch (Exception e) {
			DebugHelper.ShowMessage.d(TAG, e.getMessage());
		} finally {
			Context.exit();
		}

		return result;
	}

	public Boolean executeBooleanFunction(String functionName,
			String functionParams) {
		Boolean result = false;

		// Every Rhino VM begins with the enter()
		// This Context is not Android's Context
		Context rhino = Context.enter();

		// Turn off optimization to make Rhino Android compatible
		rhino.setOptimizationLevel(-1);
		try {
			Scriptable scope = rhino.initStandardObjects();

			rhino.evaluateString(scope, script, "JavaScript", 1, null);

			// Get the name of the plugin defined in JavaScriptCode
			Object name = scope.get(functionName, scope);

			if (name instanceof Function) {
				Object[] params = new Object[] { functionParams };
				Function jsFunction = (Function) name;

				// Call the function with params
				Object jsResult = jsFunction.call(rhino, scope, scope, params);

				// Parse the jsResult object to a String
				result = Context.toBoolean(jsResult);
			}

		} finally {
			Context.exit();
		}

		return result;
	}

	public String getScript() {
		return script;
	}

	public static List<String> getAllPluginFiles() {
		List<String> pluginFiles = new ArrayList<String>();

		File pluginDir = new File(Environment.getExternalStorageDirectory()
				+ "/decant/plugins");
		for (File pluginFile : pluginDir.listFiles()) {
			if (pluginFile.isFile()) {
				pluginFiles.add(pluginFile.getName());
			}
		}

		return pluginFiles;
	}
}
