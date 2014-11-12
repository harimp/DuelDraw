package ca.ubc.dueldraw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Application;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.widget.Toast;

public class SocketApp extends Application {
	private String ipAddress = "206.87.135.154";
	private Integer port = 50002;
	Socket sock = null;
	private boolean verbose = true;

	public void onCreate() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		super.onCreate();
		/**
		 * Code to convert URL into IP
		 */
//		 try {
//			 InetAddress address =
//			 InetAddress.getByName("middleman.redirectme.net");
//			 String temp = address.toString();
//			 ipAddress = temp.substring(temp.indexOf("/")+1,temp.length());
//			 Toast.makeText(getApplicationContext(), ipAddress,
//			 Toast.LENGTH_SHORT).show();
//		 }catch (UnknownHostException e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//		 }
		openSocket();
	}

	public void onDestroy() {
		closeSocket();
	}

	public void openSocket() {
		if(verbose){ Toast.makeText(getApplicationContext(), "Info: Opening Socket...",
				Toast.LENGTH_LONG).show(); }
		
		// Make sure the socket is not already opened
		if (sock != null && sock.isConnected() && !sock.isClosed()) {
			if(verbose){ Toast.makeText(getApplicationContext(), "Info: Socket already open",
					Toast.LENGTH_SHORT).show();	}
			return;
		}
		new SocketConnect().execute((Void) null);
	}

	public void closeSocket() {
		Socket s = sock;
		try {
			s.getOutputStream().close();
			s.close();
			if(verbose){ Toast.makeText(getApplicationContext(), "Info: Socket closed",
					Toast.LENGTH_SHORT).show();	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String str) {
		// Create an array of bytes. First byte will be the
		// message length, and the next ones will be the message
		byte buf[] = new byte[str.length() + 1];
		buf[0] = (byte) str.length();
		System.arraycopy(str.getBytes(), 0, buf, 1, str.length());

		// Now send through the output stream of the socket

		OutputStream out;
		try {
			out = sock.getOutputStream();
			try {
				out.write(buf, 0, str.length() + 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(verbose){ Toast.makeText(getApplicationContext(), "Sent: " + str,
				Toast.LENGTH_SHORT).show();	}
	}

	public void recvMessage() {
		new RecvTask().execute((Void) null);
	}

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {

		// The main parcel of work for this thread. Opens a socket
		// to connect to the specified IP.

		protected Socket doInBackground(Void... voids) {
			Socket s = null;

			try {
				s = new Socket();
				s.bind(null);
				s.connect((new InetSocketAddress(ipAddress, port)), 1000);
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
			sock = s;

			String msg;
			if (sock.isConnected()) {
				msg = "Info: Connection opened successfully";
				setupUserData();
			} else {
				msg = "Info: Connection could not be opened";
			}
			if(verbose){ Toast.makeText(getApplicationContext(), msg,
					Toast.LENGTH_SHORT).show();	}
		}

	}

	class RecvTask extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... arg0) {
			String str = null;
			try {
				InputStream in = sock.getInputStream();
				// See if any bytes are available from the Middleman
				int bytes_avail = in.available();
				if (bytes_avail > 0) {
					// If so, read them in and create a sring
					byte buf[] = new byte[bytes_avail];
					in.read(buf);
					str = new String(buf, 0, bytes_avail, "US-ASCII");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return str;
		}

		protected void onPostExecute(String str) {
			if(verbose){ Toast.makeText(getApplicationContext(), "Received: " + str,
					Toast.LENGTH_SHORT).show();	}
		}
	}
	
	private void setupUserData() {
		sendMessage("0");
		sendMessage(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
		recvMessage();
	}

	
//	public class TCPReadTimerTask extends TimerTask {
//		public void run() {
//             	new Thread(new Runnable(){
//             	    @Override
//             	    public void run() {
//             	        try {
//             	        	SocketApp app = (SocketApp) getApplicationContext();
//             				if (app.sock != null && app.sock.isConnected()
//             						&& !app.sock.isClosed()) {
//             				}
//             	        } catch (Exception ex) {
//             	            ex.printStackTrace();
//             	        }
//             	    }
//             	}).start();
//			}
//		}
}
