package ca.ubc.dueldraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ImagesListActivity extends Activity {
	ListView listView;
	String[] values = { "Android", "Circle", "Heart", "Square", "Star", "Triangle" };
	int refImageID = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_active_players);
		// Get ListView object from xml
		listView = (ListView) findViewById(R.id.list);
		// Setup adapter for ListViews
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				String imageSelected = (String) adapter
						.getItemAtPosition(position);
				Intent myIntent = new Intent(ImagesListActivity.this,
						DrawActivity.class);
				int i;
				for(i = 0; i < values.length; i++){
					if(values[i].compareTo(imageSelected) == 0)
						refImageID = i;
				}
				myIntent.putExtra("refImage", refImageID);
				startActivity(myIntent);
			}
		});
	}
}
