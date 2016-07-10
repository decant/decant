package in.decant.receivers;

import in.decant.helpers.DebugHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MessageReceiver extends BroadcastReceiver {
	private IOnMessageReceivedListener listener;
	static String TAG = "MessageReceiver";

	public MessageReceiver(Context context) {
		listener = (IOnMessageReceivedListener) context;
		DebugHelper.ShowMessage.d(TAG,
				"new instance of MessageReceiver created");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm'Z'"); // ISO 8601, Local time zone.
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String date = dateFormat.format(new Date()); // Current time in UTC.

		DebugHelper.ShowMessage.d(TAG, "Received intent "
				+ intent.getAction().toString());

		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			DebugHelper.ShowMessage.d(TAG, "Intercepting an incoming SMS");

			Bundle bundle = intent.getExtras();

			// Get SMS objects.
			Object[] pdus = (Object[]) bundle.get("pdus");
			if (pdus.length == 0) {
				return;
			}

			// Large message might be broken into many.
			SmsMessage[] rawMessages = new SmsMessage[pdus.length];
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < pdus.length; i++) {
				rawMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				sb.append(rawMessages[i].getMessageBody());
			}

			String sender = rawMessages[0].getOriginatingAddress();

			String rawMessageText = sb.toString();

			DebugHelper.ShowMessage.d(TAG, "Filtered Message from " + sender
					+ " at " + date);
			listener.onSmsReceived(date, sender, rawMessageText);
		}
	}
}
