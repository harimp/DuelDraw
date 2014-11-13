package ca.ubc.dueldraw;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ActivePlayersActivity extends Activity {
	ListView listView ;
	String[] values;
	SocketApp app;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_active_players);		 
		// Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        

        app = (SocketApp) getApplicationContext();
        if(app.playerListReady){
        	values = app.getPlayerList();
        }else {
        	values = new String[] { "Player 1", 
                    "Player 2",
                    "Player 3",
                    "Player 4", 
                    "Player 5"
                   };
        }
        
        // Setup adapter for ListViews
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter); 
        listView.setOnItemClickListener(new OnItemClickListener()
        {
           @Override
           public void onItemClick(AdapterView<?> adapter, View v, int position,
                 long arg3) 
           {
                String opponentSelected = (String)adapter.getItemAtPosition(position); 
                app.opponentID = opponentSelected; //set opponentID in application class
                 
             	
             	
             	app.sendMessage("G"+app.opponentID);	// send challenge
             	
             	pd = ProgressDialog.show(ActivePlayersActivity.this, "Challenge Sent",
             			  "Waiting for Opponent to Accept", true);
             	
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
        });
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
}
