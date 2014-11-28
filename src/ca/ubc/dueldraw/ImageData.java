package ca.ubc.dueldraw;

import android.util.Log;

public class ImageData {
	private boolean[][] pixelData;
	private boolean[][] croppedPixelData = new boolean[20][20];
	private boolean isEmpty;
	private boolean isOutline;

	private int size;

	// distance measurements from center
	private int center;
	private double left = 0;
	private double right = 0;
	private double up = 0;
	private double down = 0;
	private double SW = 0;
	private double NW = 0;
	private double NE = 0;
	private double SE = 0;

	private boolean VERBOSE = true;

	public ImageData(boolean[][] source, boolean isOutline) {
		setImage(source);
		this.isOutline = isOutline;
		crop();
		if (size <= 2) {
			return; //no points for 1 pixel
		}
		center = size / 2;

		if (VERBOSE) {
			Log.i("Size = ", Integer.toString(size));
			Log.i("Center = " + center, Integer.toString(center));
			Log.i("isOutline", Boolean.toString(this.isOutline));
		}
		measureDistances();
		if(isOutline && croppedPixelData[center][center]) {
			if(left == 1 && right == 1 && up == 1 && down == 1) {
				zeroValues();
			}
		}

		if (VERBOSE) {
			Log.i("Left/Right = ", Double.toString(leftRightRatio()));
			Log.i("Up/Down = ", Double.toString(upDownRatio()));
			Log.i("SW/NE = ", Double.toString(SWNERatio()));
			Log.i("NW/SE = ", Double.toString(NWSERatio()));
			Log.i("Height/Width = ", Double.toString(heightWidthRatio()));
		}
	}
	
	private void zeroValues() {
		left = 0;
		right = 0;
		up = 0;
		down = 0;
		SW = 0;
		NW = 0;
		NE = 0;
		SE = 0;
	}

	public boolean[][] getPixelData() {
		return pixelData;
	}

	public boolean[][] getCroppedPixelData() {
		return croppedPixelData;
	}

	public int getSize() {
		return size;
	}

	public void setImage(boolean[][] source) {
		this.pixelData = source;
		this.size = source.length;
		this.isEmpty = arrayIsEmpty();
	}

	public boolean arrayIsEmpty() {
		for (int y = 0; y < pixelData.length; y++) {
			for (int x = 0; x < pixelData.length; x++) {
				if (pixelData[y][x]) {
					return isEmpty = false;
				}
			}
		}
		return isEmpty = true;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public boolean isOutline() {
		return isOutline;
	}

	public void measureDistances() {
		if (isEmpty) {
			return;
		} else {
			measureRight();
			measureLeft();
			measureUp();
			measureDown();
			measureSW();
			measureNE();
			measureNW();
			measureSE();
		}
	}

	/*
	 * @param cell, the cell to check
	 * @returns true if isOutline and cell is checked
	 * @returns false if !isOutline and cell is unchecked
	 */
	private boolean endOfImage(boolean cell) {
		if (isOutline) {
			return cell;
		} else {
			return !cell;
		}
	}

	private void measureRight() {
		int i;
		for (i = 1; center + i < size; i++) {
			if (endOfImage(getCroppedPixelData()[center][center + i])) {
				break;
			}
		}
		right = (size % 2 == 0) ? i+1 : i; // add 1 if size is even
		if (VERBOSE) {
			Log.i("Right = ", Double.toString(right));
		}
	}

	private void measureLeft() {
		int i;
		for (i = 1; center - i >= 0; i++) {
			if (endOfImage(getCroppedPixelData()[center][center - i])) {
				break;
			}
		}
		left = i;
		if (VERBOSE) {
			Log.i("Left = ", Double.toString(left));
		}
	}

	private void measureUp() {
		int i;
		for (i = 1; center - i >= 0; i++) {
			if (endOfImage(getCroppedPixelData()[center - i][center])) {
				break;
			}
		}
		up = i;
		if (VERBOSE) {
			Log.i("Up = ", Double.toString(up));
		}
	}

	private void measureDown() {
		int i;
		for (i = 1; center + i < size; i++) {
			if (endOfImage(getCroppedPixelData()[center + i][center])) {
				break;
			}
		}
		down = (size % 2 == 0) ? i+1 : i; // add 1 if size is even
		if (VERBOSE) {
			Log.i("Down = ", Double.toString(down));
		}
	}

	private void measureSW() {
		int i;
		for (i = 1; (center - i) >= 0 && (center + i < size); i++) {
			if (endOfImage(getCroppedPixelData()[center + i][center - i])) {
				break;
			}
		}
		SW = (size % 2 == 0) ? i+1 : i; // add 1 if size is even
		if (VERBOSE) {
			Log.i("SW = ", Double.toString(SW));
		}
	}

	private void measureNE() {
		int i;
		for (i = 1; (center - i) >= 0 && (center + i < size); i++) {
			if (endOfImage(getCroppedPixelData()[center - i][center + i])) {
				break;
			}
		}
		NE = (size % 2 == 0) ? i+1 : i; // add 1 if size is even
		if (VERBOSE) {
			Log.i("NE = ", Double.toString(NE));
		}
	}

	private void measureNW() {
		int i;
		for (i = 1; center - i >= 0; i++) {
			if (endOfImage(getCroppedPixelData()[center - i][center - i])) {
				
				break;
			}
		}
		NW = i;
		if (VERBOSE) {
			Log.i("NW = ", Double.toString(NW));
		}
	}

	private void measureSE() {
		int i;
		for (i = 1; center + i < size; i++) {
			if (endOfImage(getCroppedPixelData()[center + i][center + i])) {
				break;
			}
		}
		SE = (size % 2 == 0) ? i+1 : i; // add 1 if size is even
		if (VERBOSE) {
			Log.i("SE = ", Double.toString(SE));
		}
	}

	public double leftRightRatio() {
		if (left == 0 || right == 0) {
			return 0; // to avoid divide by 0 errors
		} else {
			return left / right;
		}
	}

	public double upDownRatio() {
		if (up == 0 || down == 0) {
			return 0;
		} else {
			return up / down;
		}
	}

	// DIAGONAL: 1
	public double SWNERatio() {
		if (SW == 0 || NE == 0) {
			return 0;
		} else {
			return SW / NE;
		}
	}

	// DIAGONAL: 2
	public double NWSERatio() {
		if (NW == 0 || SE == 0) {
			return 0;
		} else {
			return NW / SE;
		}
	}

	public double heightWidthRatio() {
		if (up + down == 0 || left + right == 0) {
			return 0;
		} else {
			return (up + down) / (left + right);
		}
	}

	public boolean[][] crop() {
		if (isEmpty) {
			return pixelData;
		} else {
			croppedPixelData = crop2(crop1(pixelData));
			size = croppedPixelData.length;
			return croppedPixelData;
		}
	}

	public boolean[][] crop1(boolean[][] source) {
		int n = source.length;
		int topLeftCorner = 0, bottomRightCorner = n - 1;
		boolean rowEmpty = true, colEmpty = true;

		// start at (0,0) and go diagonally down if current row and column are
		// empty
		int i = 0;
		do {
			// check current row
			for (int x = i; x < n; x++) {
				if (source[i][x]) {
					rowEmpty = false;
					break;
				}
			}
			// check current column
			for (int y = i; y < n; y++) {
				if (source[y][i]) {
					colEmpty = false;
					break;
				}
			}
			if (rowEmpty && colEmpty) {
				topLeftCorner++;
			}
			i++;

		} while ((rowEmpty && colEmpty) && (i < n));

		rowEmpty = true;
		colEmpty = true;

		// start at bottom right corner and go up diagonally if current row and
		// column are empty
		int j = n - 1;
		do {
			// check current row
			for (int x = j; x >= topLeftCorner; x--) {
				if (source[j][x]) {
					rowEmpty = false;
					break;
				}
			}
			// check current column
			for (int y = j; y >= topLeftCorner; y--) {
				if (source[y][j]) {
					colEmpty = false;
					break;
				}
			}

			if (rowEmpty && colEmpty) {
				bottomRightCorner--;
			}
			j--;

		} while ((rowEmpty && colEmpty) && (j >= 0));

		int croppedSize = (bottomRightCorner - topLeftCorner) + 1;
		boolean[][] croppedImage = new boolean[croppedSize][croppedSize];

		// fill the new grid
		int xSource = topLeftCorner;
		int ySource = topLeftCorner;
		for (int y = 0; y < croppedSize; y++) {
			xSource = topLeftCorner;
			for (int x = 0; x < croppedSize; x++) {
				croppedImage[y][x] = source[ySource][xSource];
				xSource++;
			}
			ySource++;
		}

		return croppedImage;
	}

	public boolean[][] crop2(boolean[][] source) {
		int n = source.length;
		int bottomLeftCornerX = 0, bottomLeftCornerY = n - 1;
		int topRightCornerX = n - 1, topRightCornerY = 0;
		boolean rowEmpty = true, colEmpty = true;

		// start at (0,N-1) and go diagonally up if current row and column are
		// empty
		int i = 0;
		do {
			// check current row
			for (int x = i; x <= n - 1; x++) {
				if (source[(n - 1) - i][x]) {
					rowEmpty = false;
					break;
				}
			}
			// check current column
			for (int y = (n - 1) - i; y >= 0; y--) {
				if (source[y][i]) {
					colEmpty = false;
					break;
				}
			}
			if (rowEmpty && colEmpty) {
				bottomLeftCornerX++;
				bottomLeftCornerY--;
			}
			i++;

		} while ((rowEmpty && colEmpty) && (i < n));

		rowEmpty = true;
		colEmpty = true;

		// start at bottom right corner and go up diagonally if current row and
		// column are empty
		int j = n - 1;
		do {
			// check current row
			for (int x = j; x >= bottomLeftCornerX; x--) {
				if (source[(n - 1) - j][x]) {
					rowEmpty = false;
					break;
				}
			}
			// check current column
			for (int y = (n - 1) - j; y <= bottomLeftCornerY; y++) {
				if (source[y][j]) {
					colEmpty = false;
					break;
				}
			}

			if (rowEmpty && colEmpty) {
				topRightCornerX--;
				topRightCornerY++;
			}
			j--;

		} while ((rowEmpty && colEmpty) && (j >= 0));

		int croppedSize = (bottomLeftCornerY - topRightCornerY) + 1;
		boolean[][] croppedImage = new boolean[croppedSize][croppedSize];

		// fill the new grid
		int xSource = bottomLeftCornerX;
		int ySource = topRightCornerY;
		for (int y = 0; y < croppedSize; y++) {
			xSource = bottomLeftCornerX;
			for (int x = 0; x < croppedSize; x++) {
				croppedImage[y][x] = source[ySource][xSource];
				xSource++;
			}
			ySource++;
		}

		return croppedImage;
	}
}