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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class DrawActivity extends Activity {

	private DrawingView pixelGrid;
	private boolean timerRunning, refTimerRunning;
	private int rows, columns;
	protected TextView timerTextView;
	private int timeLimit = 30000; // drawing time limit in milliseconds
	private int refTimeLimit = 8000; // reference image display time limit in
										// milliseconds
	private final int numberOfImages = 7; // number of reference images
	private ArrayList<Integer> refImagesList;
	private boolean[][] refImage;
	private boolean TESTING = false; // USE 3x3 for testing
	private int backpress = 0;
	private boolean verbose = true;

	SocketApp app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().setTitle("Duel Draw");
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_draw);
		if (TESTING) {
			createPixelGrid(3, 3);
			timeLimit = 3000;
			refTimeLimit = 3000;
		} else {
			createPixelGrid(20, 20);
		}
		timerRunning = false;
		refTimerRunning = false;
		timerTextView = (TextView) findViewById(R.id.timerTextView);
		initializeRefImages();
		startDisplayImageTimer(getWindow().getDecorView().findViewById(android.R.id.content)); //display the image when the activity starts
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
//		Random rand = new Random();
//		int max = numberOfImages - 1;
//		int min = 0;
//		int randomNum = rand.nextInt((max - min) + 1) + min;
		
		int refImageID = getIntent().getIntExtra("refImageID", 0); //get refImageID from SocketApp

//		int refImageID = refImagesList.get(randomNum);
		imageToArray(refImageID);
	}

	private void imageToArray(int refImageIndex) {
		InputStream is = this.getResources().openRawResource(refImageIndex);
		Bitmap img = BitmapFactory.decodeStream(is);
		refImage = new boolean[columns][rows];

		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				if (img.getPixel(i, j) == Color.BLACK) {
					refImage[i][j] = true;
				} else {
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

	/* Set the input mode to Erasing if timer is started */
	public void setErase(View view) {
		if (timerRunning) {
			pixelGrid.setErase(true);
			if (verbose)
				Toast.makeText(getApplicationContext(), "Erase",
						Toast.LENGTH_SHORT).show();
		}
	}

	/* Set the input mode to Drawing if timer is started */
	public void setDraw(View view) {
		if (timerRunning) {
			pixelGrid.setErase(false);
			if (verbose)
				Toast.makeText(getApplicationContext(), "Draw",
						Toast.LENGTH_SHORT).show();
		}
	}

	public void stopDrawing() {
		pixelGrid.stopDrawing();
	}

	public void startDrawing() {
		pixelGrid.startDrawing();
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
			if (verbose)
				Toast.makeText(getApplicationContext(), "Started Timer",
						Toast.LENGTH_SHORT).show();
			new CountDownTimer(timeLimit, 1000) {

				public void onTick(long millisUntilFinished) {
					long durationSeconds = millisUntilFinished/1000;
					String time = String.format("Time Remaining : %02d:%02d", durationSeconds / 60, (durationSeconds % 60));
					timerTextView.setText(time);
				}

				public void onFinish() {
					timerTextView.setText("Done!");
					int score = calculateScore();
					if (verbose)
						Toast.makeText(getApplicationContext(),
								"Time's Up! Your score = " + score,
								Toast.LENGTH_SHORT).show();
					stopDrawing();
					sendPlayerScore_ProtocolK(score);
					timerRunning = false;
					refTimerRunning = false;
				}
			}.start();
		}
	}

	public int calculateScore() {
		boolean[][] drawnImage = pixelGrid.getCellChecked();
		double matchingCellCount = 0;
		double refCheckedCells = 0;
		double penalty = 1 / (double) (rows * columns);
		double incorrect = 0;
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				if (refImage[i][j]) {
					refCheckedCells++;
				}
				if ((drawnImage[i][j] == refImage[i][j])
						&& drawnImage[i][j] == true) {
					matchingCellCount++;
				}
				if (drawnImage[i][j] == true && refImage[i][j] == false) {
					incorrect++;
				}
			}
		}
		double score = (matchingCellCount - (incorrect * penalty))
				/ refCheckedCells;
		if (TESTING) {
			Log.i("MATCHING:", Double.toString(matchingCellCount));
			Log.i("INCORRECT:", Double.toString(incorrect));
			Log.i("PENALTY:", Double.toString(incorrect * penalty));
			Log.i("SCORE:", Double.toString(score));
		}
		if (score < 0) {
			return 0;
		}
		return (int) (score * 100);
	}

	/* Starts the countdown timer to display the reference image */
	public void startDisplayImageTimer(View view) {
		if (refTimerRunning) {
			return;
		} else {
			app = (SocketApp) getApplicationContext();
			if (app.startGame) {
				refTimerRunning = true;

				if (TESTING) {
					refImage = new boolean[3][3];
					for (int i = 0; i < 3; i++) {
						refImage[i][i] = true;
					}
				} else {
					getRefImage();
				}

				pixelGrid.setCellChecked(refImage);

				new CountDownTimer(refTimeLimit, 1000) {

					public void onTick(long millisUntilFinished) {
						long durationSeconds = millisUntilFinished/1000;
						String time = String.format("Time Remaining : %02d:%02d", durationSeconds / 60, (durationSeconds % 60));
						timerTextView.setText(time);
					}

					public void onFinish() {
						timerTextView.setText("Start drawing!");
						if (verbose)
							Toast.makeText(getApplicationContext(), "Begin!",
									Toast.LENGTH_SHORT).show();
						startDrawingTimer(getWindow().getDecorView()
								.findViewById(android.R.id.content));
						refTimerRunning = false;
					}
				}.start();
			}
		}

	}

	private void sendPlayerScore_ProtocolK(int score) {
		app = (SocketApp) getApplicationContext();
		app.sendMessage("K");
		app.sendMessage(Integer.toString(score));
	}

	@Override
	public void onBackPressed() {
		backpress = (backpress + 1);
		Toast.makeText(getApplicationContext(), "Press Back again to Exit",
				Toast.LENGTH_SHORT).show();

		if (backpress > 1) {
			super.onBackPressed();
		}

	}
}