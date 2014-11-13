package ca.ubc.dueldraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class IncomingChallengeActivity extends Activity {
	String incomingID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		incomingID = getIntent().getStringExtra("opponentID");
		setContentView(R.layout.activity_incoming_challenge);
		Toast.makeText(getApplicationContext(), incomingID, Toast.LENGTH_SHORT)
				.show();
		TextView tv = (TextView) findViewById(R.id.opponentID);
		tv.setText(incomingID);
	}

	public void acceptChallenge(View view) {
		// Do something in response to button
		SocketApp app = (SocketApp) getApplicationContext();
		app.sendMessage("I1");
		Intent intent = new Intent(this, DrawActivity.class);
		this.finish();
		startActivity(intent);
	}

	public void rejectChallenge(View view) {
		SocketApp app = (SocketApp) getApplicationContext();
		app.sendMessage("I0");
		Intent intent = new Intent(this, MenuActivity.class);
		startActivity(intent);
		this.finish();
	}
}
