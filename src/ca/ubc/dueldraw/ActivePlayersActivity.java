package ca.ubc.dueldraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ActivePlayersActivity extends Activity {
	ListView listView ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_active_players);
		
		// Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        
        // Defined Array values to show in ListView
        String[] values = new String[] { "Player 1", 
                                         "Player 2",
                                         "Player 3",
                                         "Player 4", 
                                         "Player 5"
                                        };
        
     // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter); 
        
        //ListView listView = getListView();
        listView.setOnItemClickListener(new OnItemClickListener()
        {
           @Override
           public void onItemClick(AdapterView<?> adapter, View v, int position,
                 long arg3) 
           {
                 String value = (String)adapter.getItemAtPosition(position); 
                 System.out.println(value);
                 // assuming string and if you want to get the value on click of list item
                 // do what you intend to do on click of listview row
                 
             	Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
             	intent.putExtra("playerNumber",value);
            	startActivity(intent);
           }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.active_players, menu);
		return true;
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
