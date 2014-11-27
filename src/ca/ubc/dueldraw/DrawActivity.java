package ca.ubc.dueldraw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class DrawActivity extends Activity {

	private DrawingView pixelGrid;
	private int rows, columns;
	private ArrayList<List<Object>> refImagesList;
	private boolean TESTING = false;
	private int backpress = 0;
	private boolean verbose = true;
	private boolean isSinglePlayer = false;

	private ImageData refImage;
	private ImageData drawnImage;
	private boolean isOutline;
	private boolean timerRunning, refTimerRunning;
	private double pixelScore, ratioScore, finalScore;
	protected TextView timerTextView;
	private int timeLimit = 10000; // drawing time limit in milliseconds
	private int refTimeLimit = 5000; // reference image display time limit in

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_draw);
		createPixelGrid(20, 20);
		isSinglePlayer = getIntent().getExtras().getBoolean("singlePlayer");
		initializeRefImages();
		timerRunning = false;
		refTimerRunning = false;
		startDisplayImageTimer(getWindow().getDecorView().findViewById(
				android.R.id.content));
		timerTextView = (TextView) findViewById(R.id.timerTextView);
	}

	@SuppressWarnings("unchecked")
	private void initializeRefImages() {
		// get image id's from R.java
		refImagesList = new ArrayList<List<Object>>();
		refImagesList.add(new ArrayList<Object>(Arrays.asList(
				R.drawable.androidhead, false)));
		refImagesList.add(new ArrayList<Object>(Arrays.asList(
				R.drawable.circle, true)));
		refImagesList.add(new ArrayList<Object>(Arrays.asList(
				R.drawable.heart, true)));
		refImagesList.add(new ArrayList<Object>(Arrays.asList(
				R.drawable.square, true)));
		refImagesList.add(new ArrayList<Object>(Arrays.asList(
				R.drawable.star, true)));
		refImagesList.add(new ArrayList<Object>(Arrays.asList(
				R.drawable.triangle, true)));

		if (verbose)
			Toast.makeText(getApplicationContext(),
					"RefImageList size = " + refImagesList.size(),
					Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 */
	private void getRefImage() {
		List<Object> dataList = refImagesList.get(getIntent()
				.getIntExtra("refImage", 2));
		int id = (Integer) dataList.get(0);
		boolean outline = (Boolean) dataList.get(1);
		boolean[][] refImageData = imageToArray(id);

		refImage = new ImageData(refImageData, outline);
		isOutline = refImage.isOutline();

		pixelGrid.setCellChecked(refImage.getPixelData());
	}

	/**
	 * 
	 * @param refImageResource
	 * @return
	 */
	private boolean[][] imageToArray(int refImageResource) {
		InputStream is = this.getResources().openRawResource(refImageResource);
		Bitmap img = BitmapFactory.decodeStream(is);
		boolean[][] imageGrid = new boolean[columns][rows];

		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				if (img.getPixel(i, j) == Color.BLACK) {
					imageGrid[i][j] = true;
				} else {
					imageGrid[i][j] = false;
				}
			}
		}
		return imageGrid;
	}

	/**
	 * initializes a pixelGrid with given dimensions
	 * @param rows
	 * @param columns
	 */
	private void createPixelGrid(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		pixelGrid = (DrawingView) findViewById(R.id.pixelGridView1);
		pixelGrid.setNumColumns(columns);
		pixelGrid.setNumRows(rows);
	}

	/**
	 * Set the input mode to Erasing if timer is started
	 * @param view
	 */
	public void setErase(View view) {
		pixelGrid.setErase(true);
		if (verbose)
			Toast.makeText(getApplicationContext(), "Erase", Toast.LENGTH_SHORT)
					.show();
	}

	/**
	 * Set the input mode to Drawing if timer is started
	 * @param view
	 */
	public void setDraw(View view) {
		pixelGrid.setErase(false);
		if (verbose)
			Toast.makeText(getApplicationContext(), "Draw", Toast.LENGTH_SHORT)
					.show();
	}

	public void stopDrawing() {
		pixelGrid.stopDrawing();
	}

	public void startDrawing() {
		pixelGrid.startDrawing();
	}

	/**
	 * Clears the grid
	 * @param view
	 */
	public void clearGrid(View view) {
		createPixelGrid(rows, columns);
		pixelGrid.setErase(false);
	}

/**
 * Starts the countdown timer to start drawing input
 * @param view
 */
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
					long durationSeconds = millisUntilFinished / 1000;
					if (((timeLimit/1000) - durationSeconds) > 3) {
					String time = String.format("Time Remaining : %02d:%02d",
							durationSeconds / 60, (durationSeconds % 60));
					timerTextView.setText(time);
					}
				}

				public void onFinish() {
					timerTextView.setText("Done!");
					drawnImage = new ImageData(pixelGrid.getCellChecked(), isOutline);
					int score = calculateScore();
					if (verbose)
						Toast.makeText(getApplicationContext(),
								"Time's Up! Your score = " + score,
								Toast.LENGTH_SHORT).show();
					stopDrawing();
					timerRunning = false;
					refTimerRunning = false;
					
					if(isSinglePlayer){
						// open the results activity
						Intent resultIntent = new Intent(getApplicationContext(), GameResultActivity.class);
						resultIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						resultIntent.putExtra("Ratio Score", ratioScore);
						resultIntent.putExtra("Pixel Score", pixelScore);
						resultIntent.putExtra("Final Score", finalScore);
						resultIntent.putExtra("singlePlayer", true);
						startActivity(resultIntent);
					}else{
						SocketApp app = (SocketApp) getApplicationContext();
						app.sendMessage("K" + Double.toString(finalScore));
				}
				}
			}.start();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int calculateScore() {
		boolean[][] drawnImageData = drawnImage.getCroppedPixelData();
		boolean[][] refImageData = refImage.getCroppedPixelData();
		
		boolean[][] scaledDrawnImageData = scale(drawnImageData,refImageData.length);
		
		saveToExternal("userImage.png",scaledDrawnImageData);
		saveToExternal("refImage.png",refImageData);
		
		pixelGrid.setCroppedGrid(scaledDrawnImageData,
				refImageData.length);
		
		pixelScore = getPixelScore(refImageData, scaledDrawnImageData);
		ratioScore = getRatioScore();
		
		finalScore = pixelScore*0.6 + ratioScore*0.4;
		
		Log.i("OverallScore",Double.toString(finalScore));
		
		return (int) finalScore;
		
	}

	/**
	 * 
	 * @return
	 */
	private double getRatioScore() {
		
		
		double t1 = Math.abs(refImage.leftRightRatio() - drawnImage.leftRightRatio()) / refImage.leftRightRatio();
		double t2 = Math.abs(refImage.upDownRatio() - drawnImage.upDownRatio()) / refImage.upDownRatio();
		double t3 = Math.abs(refImage.SWNERatio() - drawnImage.SWNERatio()) / refImage.SWNERatio();
		double t4 = Math.abs(refImage.NWSERatio() - drawnImage.NWSERatio()) / refImage.NWSERatio();
		double t5 = Math.abs(refImage.heightWidthRatio() - drawnImage.heightWidthRatio()) / refImage.heightWidthRatio();
		
		double score = 100 - ((t1+t2+t3+t4+t5)*100/5);
		
		if (verbose)
			Toast.makeText(getApplicationContext(),
					"Your Ratio score = " + score,
					Toast.LENGTH_SHORT).show();
		
		Log.i("RatioScore",Double.toString(score));
		
		if(score < 0)	return 0;
		return score;
	}

	/**
	 * Saves the bitmap as a png file on external memory with specified fileName
	 * @param fileName
	 * @param source
	 * @return
	 */
	private boolean saveToExternal(String fileName, boolean[][] target) {
		
		Bitmap temp = Bitmap.createBitmap(target[0].length,target.length,Bitmap.Config.ARGB_8888 );
		for (int i = 0; i < temp.getHeight(); i++) {
			for (int j = 0; j < temp.getWidth(); j++) {
				if ( target[i][j] ) {
					temp.setPixel(i, j,Color.BLACK);
				} else {
					temp.setPixel(i, j,Color.WHITE);
				}
			}
		}
		FileOutputStream out = null;
		String path = Environment.getExternalStorageDirectory().toString();
		File file = new File(path, fileName);
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		temp.compress(Bitmap.CompressFormat.PNG, 100, out);
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param ref
	 * @param drawn
	 * @return
	 */
	private double getPixelScore(boolean[][] ref, boolean[][] drawn) {
		double matchingCellCount = 0;
		double refCheckedCells = 0;
		int size = ref.length;
		double penalty = 1 / (double) (size * size);
		double incorrect = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (ref[i][j]) {
					refCheckedCells++;
				}
				if ((drawn[i][j] == ref[i][j])
						&& drawn[i][j] == true) {
					matchingCellCount++;
				}
				if (drawn[i][j] == true
						&& ref[i][j] == false) {
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
		return (score * 100);
	}
	
	/**
	 * Scale 2D boolean origArray to specified size and return 2D boolean scaledArray
	 * @param origArray
	 * @param size
	 * @return
	 */
	private boolean[][] scale(boolean[][] origArray, int size) {
		
		Bitmap temp = Bitmap.createBitmap(origArray[0].length,origArray.length,Bitmap.Config.ARGB_8888 );
		for (int i = 0; i < temp.getHeight(); i++) {
			for (int j = 0; j < temp.getWidth(); j++) {
				if ( origArray[i][j] ) {
					temp.setPixel(i, j,Color.BLACK);
				} else {
					temp.setPixel(i, j,Color.WHITE);
				}
			}
		}

		Bitmap scaled = Bitmap.createScaledBitmap(temp, size, size, false);
		boolean[][] scaledArray = new boolean[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (scaled.getPixel(i, j) == Color.BLACK) {
					scaledArray[i][j] = true;
				} else {
					scaledArray[i][j] = false;
				}
			}
		}
		return scaledArray;
	}

	/**
	 * Starts the countdown timer to display the reference image
	 * @param view
	 */
	public void startDisplayImageTimer(View view) {
		if (refTimerRunning) {
			return;
		} else {
			refTimerRunning = true;
			getRefImage();

			new CountDownTimer(refTimeLimit, 1000) {

				public void onTick(long millisUntilFinished) {
					long durationSeconds = millisUntilFinished / 1000;
					if (durationSeconds == 7) {
						timerTextView.setText("Preparing Image");
					} else {
						String time = String.format(
								"Time Remaining : %02d:%02d",
								durationSeconds / 60, (durationSeconds % 60));
						timerTextView.setText(time);
					}
					pixelGrid.setCellChecked(refImage.getPixelData());

				}

				public void onFinish() {
					timerTextView.setText("Start drawing!");
					if (verbose)
						Toast.makeText(getApplicationContext(), "Begin!",
								Toast.LENGTH_SHORT).show();
					startDrawingTimer(getWindow().getDecorView().findViewById(
							android.R.id.content));
				}
			}.start();
		}

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