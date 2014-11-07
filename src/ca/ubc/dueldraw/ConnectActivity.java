package ca.ubc.dueldraw;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConnectActivity extends Activity {

	private String ipAddress;
	private Integer port = 50002;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// This call will result in better error messages if you
		// try to do things in the wrong thread.

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		try {
			InetAddress address = InetAddress.getByName("middleman.redirectme.net");
			String temp = address.toString();
			ipAddress = temp.substring(temp.indexOf("/")+1,temp.length());
			Toast.makeText(getApplicationContext(), ipAddress, Toast.LENGTH_SHORT).show();
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Button connectButton = (Button) findViewById(R.id.connect_button);
		connectButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        openSocket(v);
             	Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            	startActivity(intent);
		    }
		});
		// Set up a timer task. We will use the timer to check the
		// input queue every 500 ms

		//TCPReadTimerTask tcp_task = new TCPReadTimerTask();
		//Timer tcp_timer = new Timer();
		//tcp_timer.schedule(tcp_task, 3000, 500);
	}

	public void openSocket(View view) {
		Toast.makeText(getApplicationContext(), "Opening Socket.", Toast.LENGTH_LONG).show();
		SocketApp app = (SocketApp) getApplication();
		// Make sure the socket is not already opened
		if (app.sock != null && app.sock.isConnected() && !app.sock.isClosed()) {
			Toast.makeText(getApplicationContext(), "Socket already open",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// open the socket. SocketConnect is a new subclass
		// (defined below). This creates an instance of the subclass
		// and executes the code in it.

		new SocketConnect().execute((Void) null);
	}

	public void closeSocket(View view) {
		SocketApp app = (SocketApp) getApplication();
		Socket s = app.sock;
		try {
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {

		// The main parcel of work for this thread. Opens a socket
		// to connect to the specified IP.

		protected Socket doInBackground(Void... voids) {
			Socket s = null;
			String ip = ipAddress;

			try {
				s = new Socket();
				s.bind(null);
				s.connect((new InetSocketAddress(ip, port)), 1000);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return s;
		}

		// After executing the doInBackground method, this is
		// automatically called, in the UI (main) thread to store
		// the socket in this app's persistent storage

		protected void onPostExecute(Socket s) {
			SocketApp myApp = (SocketApp) ConnectActivity.this.getApplication();
			myApp.sock = s;

			String msg;
			if (myApp.sock.isConnected()) {
				msg = "Connection opened successfully";
			} else {
				msg = "Connection could not be opened";
			}
			Toast t = Toast.makeText(getApplicationContext(), msg,
					Toast.LENGTH_LONG);
			t.show();
		}

	}

	// This is a timer Task. Be sure to work through the tutorials
	// on Timer Tasks before trying to understand this code.

	public class TCPReadTimerTask extends TimerTask {
		public void run() {
			SocketApp app = (SocketApp) getApplication();
			if (app.sock != null && app.sock.isConnected()
					&& !app.sock.isClosed()) {

				try {
					InputStream in = app.sock.getInputStream();

					// See if any bytes are available from the Middleman

					int bytes_avail = in.available();
					if (bytes_avail > 0) {

						// If so, read them in and create a sring

						byte buf[] = new byte[bytes_avail];
						in.read(buf);

						final String s = new String(buf, 0, bytes_avail,
								"US-ASCII");

						// As explained in the tutorials, the GUI can not be
						// updated in an asyncrhonous task. So, update the GUI
						// using the UI thread.

						runOnUiThread(new Runnable() {
							public void run() {
							}
						});

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
