package in.decant.receivers;

public interface IOnMessageReceivedListener {
	public void onSmsReceived(String timestamp, String sender, String message);
}
