package net.harrison.irremote;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnInitListener {

	// Viewable Fields
	EditText txt_ip;
	EditText txt_port;

	// Misc
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private static boolean ConfirmRequired = true;
	private int MY_DATA_CHECK_CODE = 0;
	String[] PreviousWords;
	int task; // used to keep track of task. currently the application only
				// supports changing channel but could be expanded to turning
				// off the
				// tv.. etc.

	// Interfaces
	private TextToSpeech myTTS;

	// Change and or add these to fit your market.
	String[][] channels = { { "USA", "42" }, { "FX", "41" }, { "TBS", "38" },
			{ "Life", "36" }, { "History", "34" } };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myTTS = new TextToSpeech(this, this);
		myTTS.setLanguage(Locale.US);

		// Intent checkTTSIntent = new Intent();
		// checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		// startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

		// Set up Viewable Interfaces
		txt_ip = (EditText) this.findViewById(R.id.editText1);
		txt_port = (EditText) this.findViewById(R.id.editText2);

		// !!!!!NOTE: In a real application this should not be used!
		// THIS IS BAD PROGRAMMING PRACTICE
		// AsyncTask should be used in a real application.
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			startVoiceRecognitionActivity();
			break;
		}
	}

	void startVoiceRecognitionActivity() {

		// Starts the voice regogniztion intent application.
		// You can read more about intents at
		// http://developer.android.com/guide/components/intents-filters.html
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String wordStr = null;
		String[] words = null;
		String firstWord = null;
		String secondWord = null;

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// If we have a voice to process.
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			wordStr = matches.get(0);
			words = wordStr.split(" ");

			// Checking to see if we are wanting to change channel.
			if (wordStr.contains("change channel")) {
				if (ConfirmRequired == true) {
					int len = words.length;

					String i = words[len - 1];
					PreviousWords = words;
					wordStr = "";
					task = 1; // change channel.
					speech("Are you sure you want to change the channel to "
							+ i, true); // using TTS
					startVoiceRecognitionActivity();
					return;
					// Start Intent
				} else {
					SendChannel(); // If ConfirmRequired == false just send the
									// channel
					return;
				}

			} else if (wordStr.contains("yes")) {

				if (task == 1) {
					SendChannel();
					return;
				}
			}

			// secondWord = words[1];
		} else if (requestCode == MY_DATA_CHECK_CODE) {
			// if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
			myTTS = new TextToSpeech(this, this);
			// }
			// else {
			// Intent installTTSIntent = new Intent();
			// installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			// startActivity(installTTSIntent);
			// }
		}
	}

	public void SendChannel() {

		int len = PreviousWords.length;

		String i = PreviousWords[len - 1];
		// We need to see if the last word is a channel name. If so change
		// string I to it.

		for (int x = 0; x < channels.length; x++) // Lets check to see if the
													// channel
													// is a channel name. If so
													// convert it
													// to its channel number
		{
			if (channels[x][0].toUpperCase().equals(i.toUpperCase())) {
				i = channels[x][1];
			}

		}

		PreviousWords = null; // clear out
		task = 0;

		speech("Changing channel to " + i, false);
		int len1 = i.length();
		for (int x = 0; x < len1; x++) { // Send one number at a time.
											// This task could be moved to the
											// microcontroller
											// to be process instead of sending
											// multiple here.
			send(Character.toString(i.charAt(x)));
		}
	}

	// Set the language locale
	public void onInit(int initStatus) {
		if (initStatus == TextToSpeech.SUCCESS) {
			myTTS.setLanguage(Locale.US);
		}
	}

	// /Plays the speech.
	// /String word - words to play
	// /hold - If true it will block the function out until done speaking
	// //should be
	// moved to its own thread
	void speech(String word, boolean hold) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, "true");
		myTTS.speak(word, TextToSpeech.QUEUE_ADD, hashMap);
		if (hold == true) {
			while (myTTS.isSpeaking()) {
			}
		}
	}

	// function to send the packet via UDP
	void send(String st) {
		try {
			InetAddress serverAddr = InetAddress.getByName(txt_ip.getText()
					.toString());

			DatagramSocket socket = new DatagramSocket();

			byte[] buf = st.getBytes();

			DatagramPacket packet = new DatagramPacket(buf, buf.length,
					serverAddr, Integer.parseInt(txt_port.getText().toString()));

			socket.send(packet);
		} catch (Exception e) {
			Log.e("UDP", "S:Error", e);
		}
	}

	// TODO: add menus.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			int i = 0;
			Toast.makeText(this, "Tapped Settings", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_search:
			Toast.makeText(this, "Tapped Search", Toast.LENGTH_SHORT).show();
			break;
		}
		return false;

	}

}
