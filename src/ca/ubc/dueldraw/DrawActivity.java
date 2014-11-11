package ca.ubc.dueldraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DrawActivity extends Activity {

	private DrawingView pixelGrid;
	private boolean timerRunning, refTimerRunning;
	private int rows, columns;
	protected TextView timerTextView;
	private int timeLimit = 15000; // drawing time limit in milliseconds
	private int refTimeLimit = 5000; // reference image display time limit in milliseconds
	private ArrayList<boolean[][]> refImagesList;
	private int refImageIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw);
		initializeRefImages();
		createPixelGrid(20, 20);
		timerRunning = false;
		refTimerRunning = false;
		timerTextView = (TextView) findViewById(R.id.timerTextView);
	}

	private void initializeRefImages() {
		// TODO Auto-generated method stub
		
		//initialize images
		// generate random image list index
		
//		Random r = new Random();
//		refImageIndex = r.nextInt(refImagesList.size());
	}

	/* initializes a pixelGrid with given dimensions */
	private void createPixelGrid(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		pixelGrid = (DrawingView) findViewById(R.id.pixelGridView1);
		pixelGrid.setNumColumns(columns);
		pixelGrid.setNumRows(rows);
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
					Toast.makeText(getApplicationContext(), "Time's Up! Your score = "+calculateScore(),
							Toast.LENGTH_SHORT).show();
					stopDrawing();
					timerRunning = false;
					refTimerRunning = false;
				}
			}.start();
		}
	}
	
	public int calculateScore(){
		boolean[][] drawnImage = pixelGrid.getCellChecked( );
		int matchingCellCount = 0, cellCheckCountRefImage = 0;
		boolean[][] refImage = refImagesList.get(refImageIndex);
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				if(refImage[i][j]){
					cellCheckCountRefImage++;
				}
				if(refImage[i][j] == drawnImage[i][j]) {
					matchingCellCount++;
				}
			}
		}
		return (100*matchingCellCount)/cellCheckCountRefImage;
	}
	
	/* Starts the countdown timer to display the reference image */
	public void startDisplayImageTimer(View view) {
		if (refTimerRunning) {
			return;
		} else{
			refTimerRunning = true;
			
			//display reference image
			boolean[][] refImage = refImagesList.get(refImageIndex);
			pixelGrid.setCellChecked( refImage );
			
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
