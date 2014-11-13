package ca.ubc.dueldraw;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MenuActivity extends Activity {
	private int ActivePlayers = 0;
	private ArrayList<String> playerList = new ArrayList<String>();
	int count = 0;
	private static Context appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SocketApp app = (SocketApp) getApplicationContext();
		app.recvMessage(this);
		super.onCreate(savedInstanceState);
		appContext = this;
		ActivePlayers = getIntent().getIntExtra("numberOfActivePlayers", 0);
		for (int i = 0; i < ActivePlayers; i++)
			requestPlayerName();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_menu);
		TCPReadTimerTask tcp_task = new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task, 0, 10);
	}

	public ArrayList<String> getPlayerList() {
		return playerList;
	}
	public void setActivePlayers(int activePlayers) {
		this.ActivePlayers = activePlayers;
	}
	/** Called when the user clicks the Send button */
	public void viewActivePlayers(View view) {
		// Do something in response to button
		Intent intent = new Intent(this, ActivePlayersActivity.class);
		intent.putExtra("playerNames", playerList.toArray());
		startActivity(intent);
	}

	public void viewLeaderboard(View view) {
		Toast.makeText(getApplicationContext(), "This isn't implemented yet.",
				Toast.LENGTH_SHORT).show();
	}

	private void requestPlayerName() {
		SocketApp app = (SocketApp) getApplicationContext();
		app.sendMessage("D" + count);
		app.recvMessage(this);
		count++;
	}

	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						SocketApp app = (SocketApp) getApplicationContext();
						if (app.sock != null && app.sock.isConnected()
								&& !app.sock.isClosed()) {
							app.recvMessage(appContext);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}).start();
		}
	}
}
