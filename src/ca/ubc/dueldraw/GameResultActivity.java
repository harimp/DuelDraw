package ca.ubc.dueldraw;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game_result);

		TextView rs = (TextView) findViewById(R.id.ratioScoreTextView);
		rs.setText("Ratio Score = "+ Double.toString(getIntent().getDoubleExtra("Ratio Score",-1)));
		TextView ps = (TextView) findViewById(R.id.pixelScoreTextView);
		ps.setText("Pixel Score = "+ Double.toString(getIntent().getDoubleExtra("Pixel Score",-1)));
		TextView fs = (TextView) findViewById(R.id.finalScoreTextView);
		fs.setText("Final Score = "+ Double.toString(getIntent().getDoubleExtra("Final Score",-1)));
		
		ImageView refImage = (ImageView) findViewById(R.id.refImage); 
		refImage.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/refImage.png"));
		
		ImageView userImage = (ImageView) findViewById(R.id.userImage); 
		userImage.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/userImage.png"));
	}

	public void backToMainMenu(View view) {
		Intent backIntent = new Intent(getApplicationContext(), ImagesListActivity.class);
		backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		backIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		getApplicationContext().startActivity(backIntent);
	}
}
