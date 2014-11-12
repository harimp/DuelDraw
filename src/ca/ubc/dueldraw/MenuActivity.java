package ca.ubc.dueldraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().setTitle("Duel Draw");
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_menu);
	}

	/** Called when the user clicks the Send button */
	public void viewActivePlayers(View view) {
		// Do something in response to button
		Intent intent = new Intent(this, ActivePlayersActivity.class);
		startActivity(intent);
	}
	
	public void viewLeaderboard(View view) {
		Toast.makeText(getApplicationContext(), "This isn't implemented yet." , Toast.LENGTH_SHORT).show();
	}

	/*
	 * Don't need this
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 * action bar item clicks here. The action bar will // automatically handle
	 * clicks on the Home/Up button, so long // as you specify a parent activity
	 * in AndroidManifest.xml. int id = item.getItemId(); if (id ==
	 * R.id.action_settings) { return true; } return
	 * super.onOptionsItemSelected(item); }
	 */
}
