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

	SocketApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (SocketApp) getApplicationContext();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game_result);
		TextView tv = (TextView) findViewById(R.id.result);
		if(app.userWon){
			tv.setText("You won champ!");
		}else{
			tv.setText("You have lost. Better luck next time!");
		}
		
		ImageView refImage = (ImageView) findViewById(R.id.refImage); 
		refImage.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/refImage.png"));
		
		ImageView userImage = (ImageView) findViewById(R.id.userImage); 
		refImage.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/userImage.png"));
	}

	public void backToMainMenu(View view) {
		Intent backIntent = new Intent(getApplicationContext(), MenuActivity.class);
		backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		backIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		getApplicationContext().startActivity(backIntent);
	}
}
