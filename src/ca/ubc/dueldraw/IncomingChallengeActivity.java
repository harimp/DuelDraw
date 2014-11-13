package ca.ubc.dueldraw;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class IncomingChallengeActivity extends Activity {
	ProgressDialog pd;
	SocketApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (SocketApp) getApplicationContext();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_incoming_challenge);
		TextView tv = (TextView) findViewById(R.id.opponentID);
		tv.setText(app.opponentID);
	}

	public void acceptChallenge(View view) {
		app.sendMessage("I1");	//accept challenge
		
		pd = ProgressDialog.show(IncomingChallengeActivity.this, "Challenge Accepted",
     			  "Waiting for Game to Begin", true);
     	
         	new Thread(new Runnable() {
         		  @Override
         		  public void run()
         		  {
         		    // wait for game start ping from DE2
         			while(!app.startGame);
         			app.startGame = false;
         		    runOnUiThread(new Runnable() {
         		      @Override
         		      public void run()
         		      {
         		        pd.dismiss();
         		      }
         		    });
         		  }
         		}).start();
	}

	public void rejectChallenge(View view) {
		app.sendMessage("I0");	//reject challenge
		Intent intent = new Intent(this, MenuActivity.class);
		startActivity(intent);
		this.finish();
	}
}
