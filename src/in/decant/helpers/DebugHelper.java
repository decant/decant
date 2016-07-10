package in.decant.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public final class DebugHelper {
	public static class ShowMessage {
		public static void d(String message) {
			Log.d("decant", message);
		}

		public static void d(String tag, String message) {
			Log.d("decant " + tag, message);
		}

		public static void t(Context context, String message) {
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, message, duration);
			toast.show();
		}
	}
}
