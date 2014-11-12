package ca.ubc.dueldraw;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
	//private int timeLimit = 15000; // drawing time limit in milliseconds
	private int timeLimit = 3000;  private int refTimeLimit = 3000;
	//private int refTimeLimit = 5000; // reference image display time limit in milliseconds
	private final int numberOfImages = 7; //number of reference images
	private int refImageIndex;
	private ArrayList<Integer> refImagesList;
	private boolean[][] refImage;
	private boolean TESTING = true; //USE 3x3 for testing

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw);
		if(TESTING){
			createPixelGrid(3,3);
		}
		else{
			createPixelGrid(20, 20);	
		}
		timerRunning = false;
		refTimerRunning = false;
		timerTextView = (TextView) findViewById(R.id.timerTextView);
		initializeRefImages();
	}

	private void initializeRefImages() {
		// get image id's from R.java
		refImagesList = new ArrayList<Integer>();
		refImagesList.add(R.drawable.android);
		refImagesList.add(R.drawable.dog);
		refImagesList.add(R.drawable.flappybird);
		refImagesList.add(R.drawable.mushroom);
		refImagesList.add(R.drawable.smiley);
		refImagesList.add(R.drawable.squares);
		refImagesList.add(R.drawable.random);
	}
	
	private void getRefImage() {
		// generate a random number
		Random rand = new Random();
		int max = numberOfImages-1; int min = 0;
		int randomNum = rand.nextInt((max - min) + 1) + min;
		
		// access the image 
		int refImageIndex = refImagesList.get(randomNum);
		imageToArray(refImageIndex);
	}
	
	private void imageToArray(int refImageIndex) {
		InputStream is = this.getResources().openRawResource(refImageIndex);
		Bitmap img = BitmapFactory.decodeStream(is);  
		refImage = new boolean[columns][rows];
		
		for( int i = 0; i < columns; i++ ) {
			for( int j = 0; j < rows; j++ ) {
			    	if(img.getPixel(i,j) == Color.BLACK ){
			        	refImage[i][j] = true;
			        }
			        else{
			        	refImage[i][j] = false;
			        }
			}
		}
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
		double matchingCellCount = 0; double refCheckedCells = 0;
		double penalty = 1/(double)(rows*columns); double incorrect = 0;
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				if(refImage[i][j]){
					refCheckedCells++;
				}
				if((drawnImage[i][j] == refImage[i][j]) && drawnImage[i][j] == true) {
					matchingCellCount++;
				}
				if(drawnImage[i][j]==true && refImage[i][j]==false){
					incorrect++;
				}
			}
		}
		double score = (matchingCellCount/refCheckedCells) - (incorrect*penalty);
		if(TESTING){
			Log.i("MATCHING:", Double.toString(matchingCellCount));
			Log.i("INCORRECT:", Double.toString(incorrect));
			Log.i("PENALTY:", Double.toString(incorrect*penalty));
			Log.i("SCORE:", Double.toString(score));
		}
		return (int)(score*100);
	}
	
	/* Starts the countdown timer to display the reference image */
	public void startDisplayImageTimer(View view) {
		if (refTimerRunning) {
			return;
		} else{
			refTimerRunning = true;
			
			if(TESTING){
				refImage = new boolean[3][3];
				for(int i = 0; i<3; i++){
					refImage[i][i] = true;
				}
			}
			else{
				getRefImage();
			}
			
			pixelGrid.setCellChecked( refImage );
			
			new CountDownTimer(refTimeLimit, 1000) {
	
				public void onTick(long millisUntilFinished) {
					timerTextView.setText("Time remaining: "
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
