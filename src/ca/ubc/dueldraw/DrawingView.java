package ca.ubc.dueldraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
	private int numColumns, numRows;
	private int cellWidth, cellHeight;
	private Paint blackPaint = new Paint();
	private Paint whitePaint = new Paint();
	private boolean[][] cellChecked;
	private boolean verbose = false;
	
	public boolean[][] getCellChecked() {
		return cellChecked;
	}

	public void setCellChecked(boolean[][] cellChecked) {
		this.cellChecked = cellChecked;
		invalidate();
	}

	private boolean isErase;
	private boolean canDraw;

	public DrawingView(Context context) {
		this(context, null);
	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		whitePaint.setColor(Color.WHITE);
	}

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
		calculateDimensions();
	}

	public int getNumColumns() {
		return numColumns;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
		calculateDimensions();
	}

	public int getNumRows() {
		return numRows;
	}

	public void setErase(boolean bool) {
		isErase = bool;
	}

	public void startDrawing() {
		canDraw = true;
	}

	public void stopDrawing() {
		canDraw = false;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		calculateDimensions();
	}

	private void calculateDimensions() {
		if (numColumns == 0 || numRows == 0)
			return;

		cellWidth = getWidth() / numColumns;
		cellHeight = getHeight() / numRows;

		cellChecked = new boolean[numColumns][numRows];

		invalidate();
	}

	@Override
	/*
	 * Draws the canvas when it needs to be updated. TODO: Optimize refresh
	 * without going through each cell in the grid
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(Color.WHITE);

		if (numColumns == 0 || numRows == 0)
			return;

		int width = getWidth();
		int height = getHeight();

		for (int i = 0; i < numColumns; i++) {
			for (int j = 0; j < numRows; j++) {
				if (cellChecked[i][j]) {
					canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1)
							* cellWidth, (j + 1) * cellHeight, blackPaint);
				} else {
					canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1)
							* cellWidth, (j + 1) * cellHeight, whitePaint);
				}
			}
		}

		// Draw the Grid lines to the canvas
		for (int i = 1; i < numColumns; i++) {
			canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
		}

		for (int i = 1; i < numRows; i++) {
			canvas.drawLine(0, i * cellHeight, width, i * cellHeight,
					blackPaint);
		}

	}

	@Override
	/* Maps touch events to cells in the grid to be erased/drawn */
	public boolean onTouchEvent(MotionEvent event) {

		if (!canDraw) {
			return true; // don't do anything if drawing is not enabled
		}

		// Get touch event coordinates
		int column = (int) (event.getX() / cellWidth);
		int row = (int) (event.getY() / cellHeight);

		if(verbose){
			Log.i("X", Integer.toString(column));
			Log.i("Y", Integer.toString(row));
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// drawPath.moveTo(touchX, touchY);
			break;
		case MotionEvent.ACTION_MOVE:
			// drawPath.lineTo(touchX, touchY);
			break;
		case MotionEvent.ACTION_UP:
			// drawCanvas.drawPath(drawPath, drawPaint);
			// drawPath.reset();
			break;
		default:
			return true;
		}

		// When a cell is touched, set its value to true to draw it in onDraw,
		// or false to erase
		if (column >= 0 && column < numColumns && row >= 0 && row < numRows) {
			cellChecked[column][row] = (isErase ? false : true);
			invalidate();
		}

		return true;
	}
}