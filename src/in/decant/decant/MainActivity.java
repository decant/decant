package in.decant.decant;

import in.decant.helpers.DebugHelper;
import in.decant.models.SMSMessage;
import in.decant.receivers.IOnMessageReceivedListener;
import in.decant.receivers.MessageReceiver;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;

public class MainActivity extends ActionBarActivity implements
		IOnMessageReceivedListener, OnItemSelectedListener {
	Spinner smsGeneratorTypeSpinner;
	Button generateSMSButton;
	MessageReceiver receiver;
	int messageCount;
	List<SMSMessage> messages;
	static String TAG = "MainActivity";
	String defaultSmsApp;
	int currentSMSGenerator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		smsGeneratorTypeSpinner = (Spinner) findViewById(R.id.spinner1);
		generateSMSButton = (Button) findViewById(R.id.button3);
		defaultSmsApp = "com.android.mms";
		receiver = new MessageReceiver(this);

		messageCount = 0;
		currentSMSGenerator = 0;
		generateSMSButton.setEnabled(false);
		messages = new ArrayList<SMSMessage>();
		initialize();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	public void generateSMS(View view) {
		switch (currentSMSGenerator) {
		case 1:
			generateIrctcSMS();
			break;

		case 2:
			generateAmazonSMS();
			break;

		case 3:
			generateDominosSMS();
			break;

		default:
			DebugHelper.ShowMessage.t(this, "Please select a SMS Generator");
		}
	}

	private void generateIrctcSMS() {

		DebugHelper.ShowMessage.t(this, "Clicked generate IRCTC SMS");

		String pnr = "PNR:" + randInt(1038941616, 2138941616) + ",";
		String train = "TRAIN:" + randInt(10138, 52138) + ",";
		String date = "DOJ:" + randInt(1, 28) + "-" + randInt(1, 12) + "-2016"
				+ ",";
		String time = "TIME:" + randInt(0, 23) + ":" + randInt(0, 59) + ",";
		String type = "SL,";
		String journey = "NDLS TO FDB,";
		String name = "RAMESH KUMAR,";
		String status = "RLWL " + randInt(1, 500) + ",";
		String fare = "FARE:" + randInt(200, 1000) + ",";
		String extras;

		String[] sampleSMSMessages = new String[20];

		// Sample IRCTC SMS
		// "PNR:2438941616,TRAIN:12138,DOJ:25-08-2016,TIME:05:15,SL,NDLS TO FDB,RAMESH KUMAR,RLWL 103,FARE:140,SC:10+PG CHGS.";
		for (int i = 0; i < 5; i++) {
			if (randInt(0, 10) != 1) {
				pnr = "PNR:" + randInt(1038941616, 2138941616) + ",";

				train = "TRAIN:" + randInt(10138, 52138) + ",";
				date = "DOJ:" + randInt(1, 28) + "-" + randInt(1, 12) + "-2016"
						+ ",";
				time = "TIME:" + randInt(0, 23) + ":" + randInt(0, 59) + ",";

				switch (randInt(0, 4)) {
				case 0:
					type = "SL,";
					break;
				case 1:
					type = "2A,";
					break;
				case 2:
					type = "3A,";
					break;
				case 3:
					type = "2S,";
					break;
				case 4:
					type = "CC,";
					break;
				default:
					type = "CC,";
					break;
				}

				switch (randInt(0, 4)) {
				case 0:
					journey = "NDLS TO FDB,";
					break;
				case 1:
					journey = "CAN TO TVC,";
					break;
				case 2:
					journey = "MAS TO CAN,";
					break;
				case 3:
					journey = "FDB TO NDLS,";
					break;
				case 4:
					journey = "MAN TO TVC,";
					break;
				default:
					journey = "MAN TO TVC,";
					break;
				}

				switch (randInt(0, 4)) {
				case 0:
					name = "RAMESH KUMAR,";
					status = "RLWL " + randInt(1, 500) + ",";
					break;
				case 1:
					name = "RAMESH KUMAR+2,";
					status = "S7 49 S7 52 S7 55,";
					break;
				case 2:
					name = "RAM NARESH,";
					status = "S1 " + randInt(1, 60) + ",";
					break;
				case 3:
					name = "VIJAY MODI+1,";
					status = "S3 " + randInt(1, 60) + " S3 " + randInt(1, 60)
							+ ",";
					break;
				case 4:
					name = "JITHU KUMAR,";
					status = "RLWL " + randInt(1, 500) + ",";
					break;
				default:
					name = "JITHU KUMAR,";
					status = "RLWL " + randInt(1, 500) + ",";
					break;
				}

				fare = "FARE:" + randInt(200, 1000) + ",";
			} else {
				switch (randInt(0, 4)) {
				case 0:
					status = "RLWL " + randInt(1, 500) + ",";
					break;
				case 1:
					status = "S7 49 S7 52 S7 55,";
					break;
				case 2:
					status = "S1 " + randInt(1, 60) + ",";
					break;
				case 3:
					status = "S3 " + randInt(1, 60) + " S3 " + randInt(1, 60)
							+ ",";
					break;
				case 4:
					status = "RLWL " + randInt(1, 500) + ",";
					break;
				default:
					status = "RLWL " + randInt(1, 500) + ",";
					break;
				}
			}

			extras = "SC:10+PG CHGS.";

			sampleSMSMessages[i] = pnr + train + date + time + type + journey
					+ name + status + fare + extras;
		}

		for (int i = 0; i < 5; i++) {
			putSMSinInbox(this, "09995340000", sampleSMSMessages[i]);
		}

		for (int i = 0; i < 5; i++) {
			try {
				sendSms(this, "09995340000", sampleSMSMessages[i]);

				DebugHelper.ShowMessage.d(TAG, "Sent the test IRCTC sms: "
						+ sampleSMSMessages[i]);

			} catch (Exception e) {
				DebugHelper.ShowMessage.d(TAG, "Exception : " + e.getClass()
						+ " : " + e.getMessage());
			}
		}

	}

	private void generateDominosSMS() {
		DebugHelper.ShowMessage.t(this, "Clicked generate Dominos SMS");

		String[] sampleSMSMessages = new String[20];

		sampleSMSMessages[0] = "Cheer for India with Dominos Pizza;Get 30% OFF on 400 Today, Enjoy with friends & Family! Call@68886888 / Order Online /Mob@dominos.co.in cpn:CRIA61DC9D7D T&C";
		sampleSMSMessages[1] = "Dominos Super Value Friday;Buy 1 Get 1 Pizza Free Only TODAY; Walk-In/Call for Home Delivery @ 68886888 / Order Online /Mob@dominos.co.in MOB06 T&C";
		sampleSMSMessages[2] = "Enjoy Ur Evening with Friends, Family & Dominos; Get 20% Off on 350 &above. Call 68886888 / Order Online /Mob@dominos.co.in cpn:CRM90FB01431.Vld:22Mar.T&C";
		sampleSMSMessages[3] = "Dominos Online Super Value Wednesday; Buy 1 Pizza & Get 1 Pizza FREE TODAY (6pm-11pm); Valid Only on Online Orders@dominos.co.in or Dominos APP Cpn:MOBO6 T&C";
		sampleSMSMessages[4] = "Dominos is Missing U! Here's Special Offer For U; Get 101 off on 400 &above.Call 68886888/Order Online/Mobile@dominos.co.in CpnLCRMEDABAF4D0,Vld:02Apr.T&C";
		sampleSMSMessages[5] = "Been a while since you had a domino's pizza;Buy One Pizza & Get One Pizza Free. Call 68886888.Cpn:4181VYOS.Vld:28Apr.T&C";

		for (int i = 0; i < 6; i++) {
			putSMSinInbox(this, "09995342000", sampleSMSMessages[i]);
		}

		for (int i = 0; i < 6; i++) {
			try {
				sendSms(this, "09995342000", sampleSMSMessages[i]);

				DebugHelper.ShowMessage.d(TAG, "Sent the test Dominos sms: "
						+ sampleSMSMessages[i]);

			} catch (Exception e) {
				DebugHelper.ShowMessage.d(TAG, "Exception : " + e.getClass()
						+ " : " + e.getMessage());
			}
		}

	}

	private void generateAmazonSMS() {
		DebugHelper.ShowMessage.t(this, "Clicked generate Amazon SMS");

		String[] sampleSMSMessages = new String[20];

		sampleSMSMessages[0] = "Your order for Sony 16GB(... and 4 other item(s) has been successfully placed. Check email for estimated delivery dates. Thank you for shopping at Amazon.";
		sampleSMSMessages[1] = "Your order for Joby JB0123... and 1 other item(s) has been successfully placed. Check email for estimated delivery dates. Thank you for shopping at Amazon.";
		sampleSMSMessages[2] = "Your order for Transcend MP350 (TS8GMP350B) 8... has been successfully placed. It will be delivered by 31-Mar. Thank you for shipping at Amazon.";
		sampleSMSMessages[3] = "Dispatched: Your package with Sony 16GB Class 10 SDHC Memory (Upto 70MBps) will be delivered on or before 30 - Mar. Track at http://amazn.in/byWZuEI";
		sampleSMSMessages[4] = "Dispatched: Your package with Joby JB01238-CAM GorillaPod Original Tripod (Black/Fu... will be delivered on or before 26-Mar. Track at http://amazn.in/3qCLNb2";
		sampleSMSMessages[5] = "Dispatched: Your package with 15 Grids Multipurpose Transparent Storagebox... will be delivered on or before 28-Mar. Track at http://amazn.in/iUUviDW";
		sampleSMSMessages[6] = "Your package with 16 x 2 LCD display with Adapter... has been shipped via DTDC (R21474677). Delivery Estimate: 28 Mar-01 Apr";
		sampleSMSMessages[7] = "Your package with Bulfyss 36 Grid Cells Multipurp... has been dispatched by the seller. Expected delivery between: 05 Apr - 05 Apr. Check email for more details.";
		sampleSMSMessages[8] = "Your package with Tactile Switch micro - Push to... has been shipped via Fedex/AFL (782635055430) Delivery Estimate: 28 Mar - 01 Apr";
		sampleSMSMessages[9] = "Arriving Today: Your package with 15 Grids Multipurpose Transparent Plastic Storage Box with Removale Div ~ is out for delivery. Track http://amzn.in/jgJttVS";
		sampleSMSMessages[10] = "Arriving Today: Your package with Joby JB01238-CAM GorillaPod Original Tripod (Black / Fuchsia) is out for delivery. Track http://amzn.in/0CghFDd";
		sampleSMSMessages[11] = "Delivered: Your package with 15 Grids Multipurpose Transparent Plastic Storage Box with Re... was successfully delivered. More info at http://amzn.in/fb7fZhS";
		sampleSMSMessages[12] = "Delivered: Your package with Joby JB0138-CAM GorillaPod Original Tripod (Black/Fuchsia) was successfully delivered. More info at http://amzn.in/ddUGSUU";
		sampleSMSMessages[13] = "Arriving Today: Your package with Bulfyss 36 Grid Cells Multipurpose Clear Transpare ~ will be delivered by AmzAgent(8589028860). Track http://amzn.in/hntH4Aq";

		for (int i = 0; i < 13; i++) {
			putSMSinInbox(this, "09995341000", sampleSMSMessages[i]);
		}

		for (int i = 0; i < 13; i++) {
			try {
				sendSms(this, "09995341000", sampleSMSMessages[i]);

				DebugHelper.ShowMessage.d(TAG, "Sent the test Amazon sms: "
						+ sampleSMSMessages[i]);

			} catch (Exception e) {
				DebugHelper.ShowMessage.d(TAG, "Exception : " + e.getClass()
						+ " : " + e.getMessage());
			}
		}

	}

	public void pluginResults(View view) {
		Gson gson = new Gson();
		String messageList = gson.toJson(messages);

		SharedPreferences sp = getSharedPreferences("messageList",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		editor.putString("SMSMessages", messageList);
		editor.putString("MessageCount", "" + messages.size());
		editor.commit();

		Intent intent = new Intent(this, PluginListActivity.class);
		startActivity(intent);
	}

	public void startSmsReceiver(View view) {
		IntentFilter intentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(9999);
		registerReceiver(receiver, intentFilter);
		generateSMSButton.setEnabled(true);
	}

	public void stopSmsReceiver(View view) {
		unregisterReceiver(receiver);
	}

	/**
	 * Returns a pseudo-random number between min and max, inclusive. The
	 * difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 * 
	 * @param min
	 *            Minimum value
	 * @param max
	 *            Maximum value. Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	private int randInt(int min, int max) {
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	private void sendSms(Context context, String sender, String body)
			throws Exception {
		byte[] pdu = null;
		byte[] scBytes = PhoneNumberUtils
				.networkPortionToCalledPartyBCD("0999999999");
		byte[] senderBytes = PhoneNumberUtils
				.networkPortionToCalledPartyBCD(sender);
		int lsmcs = scBytes.length;
		byte[] dateBytes = new byte[7];
		Calendar calendar = new GregorianCalendar();
		dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
		dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
		dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
		dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
		dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
		dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
		dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar
				.get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		bo.write(lsmcs);
		bo.write(scBytes);
		bo.write(0x04);
		bo.write((byte) sender.length());
		bo.write(senderBytes);
		bo.write(0x00);
		bo.write(0x00);
		bo.write(dateBytes);

		String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
		Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
		Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod(
				"stringToGsm7BitPacked", new Class[] { String.class });
		stringToGsm7BitPacked.setAccessible(true);
		byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null, body);
		bo.write(bodybytes);
		pdu = bo.toByteArray();

		// broadcast the SMS_RECEIVED to registered receivers
		broadcastSmsReceived(context, pdu);

		// or, directly send the message into the inbox and let the usual SMS
		// handling happen - SMS appearing in Inbox, a notification with sound,
		// etc.
		// startSmsReceiverService(context, pdu);
	}

	private void broadcastSmsReceived(Context context, byte[] pdu) {
		Intent intent = new Intent();
		intent.setAction("android.provider.Telephony.SMS_RECEIVED");
		intent.putExtra("pdus", new Object[] { pdu });
		context.sendBroadcast(intent);
	}

	private void putSMSinInbox(Context context, String sender, String body) {
		ContentValues cv2 = new ContentValues();
		// values.put("address", sms.getOriginatingAddress());
		// values.put("body", sms.getMessageBody().toString());
		// // Convert message date to milliseconds
		// values.put("date", sms.getTimestampMillis());

		cv2.put("address", sender);
		cv2.put("date", Calendar.getInstance().getTimeInMillis());
		cv2.put("read", 1);
		cv2.put("type", 1);
		cv2.put("body", body);
		getContentResolver().insert(Uri.parse("content://sms/inbox"), cv2);

	}

	private byte reverseByte(byte b) {
		return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
	}

	@Override
	public void onSmsReceived(String timestamp, String sender,
			String rawTextMessage) {
		// TODO Auto-generated method stub
		DebugHelper.ShowMessage.d(TAG, "got a message from a " + sender);

		SMSMessage message = new SMSMessage(messageCount, sender,
				rawTextMessage, timestamp);

		getThreadID(message);
		messageCount++;

		messages.add(message);
	}

	public void setSMSApp(View view) {
		if (defaultSmsApp.compareTo(this.getPackageName()) != 0) {
			Intent intent = new Intent(
					Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
			intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
					this.getPackageName());
			this.startActivity(intent);
		}
	}

	public void resetSMSApp(View view) {
		DebugHelper.ShowMessage.d(defaultSmsApp);
		Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
		intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
		this.startActivity(intent);
	}

	private void getThreadID(SMSMessage message) {

		int req_thread_id = 0;

		Uri mSmsinboxQueryUri = Uri.parse("content://sms");
		Cursor cursor1 = getContentResolver().query(
				mSmsinboxQueryUri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body", "type" }, null, null, null);

		startManagingCursor(cursor1);
		if (cursor1.getCount() > 0) {
			while (cursor1.moveToNext()) {

				int id = cursor1.getInt(0);
				int thread_id = cursor1.getInt(1);
				String address = cursor1.getString(cursor1
						.getColumnIndex("address"));
				String body = cursor1.getString(cursor1.getColumnIndex("body"));
				String date = cursor1.getString(cursor1.getColumnIndex("date"));

				if (address.compareTo(message.getIdentificationHeader()) == 0
						&& body.compareTo(message.getRawMessage()) == 0) {
					DebugHelper.ShowMessage.d(id + " " + thread_id + " "
							+ address + " " + body + " " + date);
					message.setId(id);
				}

			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		currentSMSGenerator = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	private void initialize() {
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.sample_sms_generators,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		smsGeneratorTypeSpinner.setAdapter(adapter);
		smsGeneratorTypeSpinner.setOnItemSelectedListener(this);
	}
}
