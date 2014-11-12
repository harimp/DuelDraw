package ca.ubc.dueldraw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.widget.Toast;

public class SocketApp extends Application {
	private String ipAddress = "128.189.93.239";
	private Integer port = 50002;
	Socket sock = null;
	private boolean verbose = false;
	
	private ArrayList<String> playerList;
	private int numberOfActivePlayers;
	boolean playerListReady = false;
	
	boolean userWon = false;
	boolean userInitalConnectionAcknowledge = false;
	boolean startGame = false;
	
	String opponentID;

	public String[] getPlayerList() {
		return playerList.toArray(new String[playerList.size()]);
	}

	public void setPlayerList(String[] playerList) {
		this.playerList = new ArrayList(Arrays.asList(playerList));
	}

	public void onCreate() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		playerList = new ArrayList<String>();
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
		TCPReadTimerTask tcp_task = new TCPReadTimerTask();
        Timer tcp_timer = new Timer();
        tcp_timer.schedule(tcp_task, 0, 100);
	}

	public void onDestroy() {
		closeSocket();
	}

	public void openSocket() {
		if(verbose){ Toast.makeText(getApplicationContext(), "Info: Opening Socket...",
				Toast.LENGTH_SHORT).show(); }
		
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
		System.arraycopy(str.getBytes(), 0, buf, 0, str.length());
		buf[str.length()] = '\0';

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
//				setupUserData_ProtocolA();
				requestListOfActivePlayers_ProtocolC();
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
		if( str != null ) {
			if(verbose) Toast.makeText(getApplicationContext(), "Received: " + str,
					Toast.LENGTH_SHORT).show();
			
			/* Structure of message from DE2: [0] Protocol ID (A,B,P,etc)
			 * 								  [1..] Message
			 */
			switch(str.charAt(0)){
			case 'B': Toast.makeText(getApplicationContext(), "Protocol: Connection Acknoledged",
					Toast.LENGTH_SHORT).show();
					// check for boolean value whether user connection acknowledged
//					if((int) str.charAt(1) == 1) 	userInitalConnectionAcknowledge = true;
					//requestListOfActivePlayers_ProtocolC();
					break;
					
			case 'D': Toast.makeText(getApplicationContext(), "Protocol: Number of Players in List Received = " + str.charAt(1),
					Toast.LENGTH_SHORT).show();
					// initialize player list based on number of players that are active
					numberOfActivePlayers = (int) str.charAt(1);
					requestNextMessage();
					// listen for players list to be sent one by one
//					if(numberOfActivePlayers > 0)	recvMessage();
					break;	
					
			case 'E': Toast.makeText(getApplicationContext(), "Protocol: Player Name = " + str.substring(1),
					Toast.LENGTH_SHORT).show();
					// store player info in ArrayList of active players
					playerList.add(str.substring(1));
					System.out.println("Added to list: " + str.substring(1));
					requestNextMessage();
					// listen for more players to be sent
//					recvMessage();
					break;		
					
			case 'F': Toast.makeText(getApplicationContext(), "Protocol: End of Player List",
					Toast.LENGTH_SHORT).show();
					// player list is ready if all the active players have been received
					if(playerList.size() == numberOfActivePlayers) playerListReady = true;
					break;
					
			case 'H': Toast.makeText(getApplicationContext(), "Protocol: Accept Challenge From = " + str.substring(1),
					Toast.LENGTH_SHORT).show();
					opponentID = str.substring(1);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getApplicationContext());
					// set title
					alertDialogBuilder.setTitle("Challenge!");			 
					// set dialog message
					alertDialogBuilder
						.setMessage("You have been challenged by "+opponentID)
						.setCancelable(false)
						.setPositiveButton("Accept",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, open new activity
								userChallengeResponse_ProtocolI(true);
				             	Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
				             	startActivity(intent);
							}
						  })
						.setNegativeButton("Deny",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								userChallengeResponse_ProtocolI(false);
								dialog.cancel();
							}
						});
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
					// show dialog
					alertDialog.show();
					break;
			case 'J': Toast.makeText(getApplicationContext(), "Protocol: Ping to Begin Match",
					Toast.LENGTH_SHORT).show();
					startGame = true;
					break;
			case 'L': Toast.makeText(getApplicationContext(), "Protocol: Match Complete Result",
					Toast.LENGTH_SHORT).show();
					if((int) str.charAt(1) == 1) userWon = true;
					break;
			default: Toast.makeText(getApplicationContext(), "Protocol: Invalid!",
					Toast.LENGTH_SHORT).show();
					break;
				
			}
		}
		}


	}
	
	private void requestListOfActivePlayers_ProtocolC() {
		sendMessage("C");
	}
	
	private void requestNextMessage() {
		sendMessage("X");
	}
	
	private void setupUserData_ProtocolA() {
		sendMessage("A");
		sendMessage(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
//		recvMessage();
	}
	
	private void userChallengeResponse_ProtocolI(boolean accept) {
		sendMessage("I");
		if (accept)	{	sendMessage("1");	}
		else { sendMessage("0");	}
//		recvMessage();
	}

	
	public class TCPReadTimerTask extends TimerTask {
		public void run() {
             	new Thread(new Runnable(){
             	    @Override
             	    public void run() {
             	        try {
             	        	SocketApp app = (SocketApp) getApplicationContext();
             				if (app.sock != null && app.sock.isConnected()
             						&& !app.sock.isClosed()) {
             					recvMessage();
             				}
             	        } catch (Exception ex) {
             	            ex.printStackTrace();
             	        }
             	    }
             	}).start();
			}
		}
}
