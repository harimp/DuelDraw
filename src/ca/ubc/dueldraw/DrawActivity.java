package ca.ubc.dueldraw;

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
	private boolean timerRunning;
	private int rows, columns;
	protected TextView timerTextView;
	private int timeLimit = 15000; // drawing time limit in milliseconds

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw);
		createPixelGrid(20, 20);
		timerRunning = false;
		timerTextView = (TextView) findViewById(R.id.timerTextView);
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
	}

	/* Clears the grid */
	public void clearGrid(View view) {
		if (timerRunning) {
			createPixelGrid(rows, columns);
			pixelGrid.setErase(false);
		}
	}

	/* Starts the countdown timer to start drawing input */
	public void startTimer(View view) {
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
				}
			}.start();
		}
	}
}
