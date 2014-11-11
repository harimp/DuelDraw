package ca.ubc.dueldraw;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class DrawActivity extends Activity {

	private DrawingView pixelGrid;
	private boolean timerRunning, refTimerRunning;
	private int rows, columns;
	protected TextView timerTextView;
	private int timeLimit = 15000; // drawing time limit in milliseconds
	private int refTimeLimit = 5000; // reference image display time limit in milliseconds
	private boolean[][] pixels;
	//private int refImageID;
	//private HashMap<Integer, boolean[][]> refImageMapping;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw);
		createPixelGrid(20, 20);
		timerRunning = false;
		refTimerRunning = false;
		timerTextView = (TextView) findViewById(R.id.timerTextView);
		imageToArray();
	}

	/* initializes a pixelGrid with given dimensions */
	private void createPixelGrid(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		pixelGrid = (DrawingView) findViewById(R.id.pixelGridView1);
		pixelGrid.setNumColumns(columns);
		pixelGrid.setNumRows(rows);
	}
	
	private void imageToArray() {

		Bitmap img = null;
		File imgFile = new  File("/drawable/brush.png");
		if(imgFile.exists()){
			Log.i("imageToArray()", "Image exists");
			img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			Log.i("imageToArray()", "File opened");
			pixels = new boolean[columns][rows];
			for( int i = 0; i < columns; i++ ){
			    for( int j = 0; j < rows; j++ )
			        if(img.getPixel(i,j) == 0){
			        	pixels[i][j] = true;
			        }
			        else{
			        	pixels[i][j] = false;
			        }
			}
		}
		else{
			Log.i("imageToArray()", "File was not opened");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* Set the input mode to Erasing if timer is started */
	public void setErase(View view) {
		if (timerRunning) {
			pixelGrid.setErase(true);
			Toast.makeText(getApplicationContext(), "Erase", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/* Set the input mode to Drawing if timer is started */
	public void setDraw(View view) {
		if (timerRunning) {
			pixelGrid.setErase(false);
			Toast.makeText(getApplicationContext(), "Draw", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void stopDrawing() {
		pixelGrid.stopDrawing();
	}

	public void startDrawing() {
		pixelGrid.startDrawing();
	}

	/* TODO: Save the pixelGrid to a file */
	public void saveImage(View view) {
		Toast.makeText(getApplicationContext(), "Image Saved to Gallery",
				Toast.LENGTH_SHORT).show();
		pixelGrid.getCellChecked();
	}

	/* Clears the grid */
	public void clearGrid(View view) {
		if (timerRunning) {
			createPixelGrid(rows, columns);
			pixelGrid.setErase(false);
		}
	}

	/* Starts the countdown timer to start drawing input */
	public void startDrawingTimer(View view) {
		if (timerRunning) {
			return;
		} else {
			timerRunning = true;
			startDrawing();
			clearGrid(view);
			Toast.makeText(getApplicationContext(), "Started Timer",
					Toast.LENGTH_SHORT).show();
			new CountDownTimer(timeLimit, 1000) {

				public void onTick(long millisUntilFinished) {
					timerTextView.setText("seconds remaining: "
							+ millisUntilFinished / 1000);
				}

				public void onFinish() {
					timerTextView.setText("Done! Click start to draw again.");
					Toast.makeText(getApplicationContext(), "Time's Up!",
							Toast.LENGTH_SHORT).show();
					stopDrawing();
					timerRunning = false;
					refTimerRunning = false;
				}
			}.start();
		}
	}
	
	/* Starts the countdown timer to display the reference image */
	public void startDisplayImageTimer(View view) {
		if (refTimerRunning) {
			return;
		} else{
			refTimerRunning = true;
			
			boolean[][] temp = new boolean[rows][columns];
			//Arrays.fill(temp, true);
			//temp[1][1] = true;
			
			for (int i = 0; i < columns; i++) {
				for (int j = 0; j < rows; j++) {
						temp[i][j] = true;
				}
			}
			pixelGrid.setCellChecked( temp );
			
			new CountDownTimer(refTimeLimit, 1000) {
	
				public void onTick(long millisUntilFinished) {
					timerTextView.setText("Seconds remaining: "
							+ millisUntilFinished / 1000);
				}
	
				public void onFinish() {
					timerTextView.setText("Start drawing!");
					Toast.makeText(getApplicationContext(), "Begin!",
							Toast.LENGTH_SHORT).show();
					startDrawingTimer(getWindow().getDecorView().findViewById(android.R.id.content));
				}
			}.start();
		}
		
	}
}
