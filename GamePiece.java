import java.util.Arrays;

public class GamePiece {
	final int length, height, color, pixelsPerSecond;
	int yPos, xPos;

	int prevX = 0;
	int prevY = 0;

	GamePiece(int xPos, int initY, int length, int height,int color, int pixelsPerSecond) {
		this.xPos = xPos;
		this.length = length;
		this.height = height;
		yPos = initY;
		this.color = color;
		this.pixelsPerSecond = pixelsPerSecond;
	}

	int[][] renderPiece(int[][] arr) {
		int[][] changed = new int[height*length*2][3];
		int index = 0;

		for(int x = prevX; x < prevX + length;x++)
			for(int y = prevY ; y < prevY + height; y++)
			{
				changed[index++] = new int[] {x,y,0};
			}

		boolean found = false;
		int overlap = 0;
		for(int x = xPos; x < xPos + length;x++)
			for(int y = yPos ; y < yPos + height; y++)
			{
				for(int[] set: changed)
					if(changed[index][0] == x && changed[index][1] == y) {
						changed[index] = null;
						overlap++;
						found = true;
					}
				if(!found) 
					changed[index++] = new int[] {x, y, color};
				else
					found = false;
			}

		int[][] resize = new int[index-overlap][3];
		int index2 = 0;
		for(int i = 0; i < index; i++)
			if(changed[i] != null)
				resize[index2++] = changed[i];

		return resize;
	}
}

class Ball extends GamePiece {
	boolean p1Wins = false;
	boolean p2Wins = false;

	int[] vector;

	Pong parent;
	boolean firstRound = true;

	Ball(int xPos, int yPos, int size, int color, int velocity, Pong parent) {
		super(xPos, yPos, size, size, color, velocity);
		this.parent = parent;
		double theta = ((Math.random() * 90) - 45);
		theta *= Math.PI / 180;
		vector = new int[]{-(int)(Math.abs(Math.cos(theta)*velocity)), (int)(Math.sin(theta)*velocity)};
	}

	int[] move(Paddle p1, Paddle p2, int maxY, int minY) {
		int[] toReturn = null;
		if(xPos + vector[0] > p2.xPos - p2.length)
		{
			toReturn = new int[] {yPos};
			if(!(p2.yPos < yPos + height && p2.yPos + p2.height > yPos)) {
				parent.shouldReset = Pong.P1WINS;
				xPos -= vector[0];
				yPos -= vector[1];
			}
			else {
				double theta = p2.calcDeflection(yPos);
				vector = new int[] {-1*(int)(Math.abs(Math.round(Math.cos(theta)*pixelsPerSecond))), (int)(Math.round(Math.sin(theta)*pixelsPerSecond))};
			}
		}

		if (xPos + vector[0] < p1.xPos + p1.length) {
			if(!(p1.yPos < yPos + height && p1.yPos + p1.height > yPos)) {
				parent.shouldReset = Pong.P2WINS;
				xPos -= vector[0];
				yPos -= vector[1];
			}
			else {
				double theta = p1.calcDeflection(yPos);
				toReturn = new int[] {yPos,p1.yPos};
				vector = new int[] {(int)(Math.abs(Math.round(Math.cos(theta)*pixelsPerSecond))), (int)(Math.round(Math.sin(theta)*pixelsPerSecond))};
			}
		}

		if(yPos + vector[1] + height > maxY || yPos + vector[1] < minY) {
			vector[1] *= -1;
		}

		prevY = yPos;
		prevX = xPos;

		xPos += vector[0];
		yPos += vector[1];

		return toReturn;
	}
}

class Paddle extends GamePiece {
	Paddle(int xPos, int yPos, int height, int length, int color, int velocity) {
		super(xPos, yPos, height, length, color, velocity);
		prevX = xPos;
	}

	public double calcDeflection(int yPos) {
		double dif = (yPos-this.yPos);
		double theta = ((dif / height) * 90) - 45;
		theta *= Math.PI / 180;
		return theta;
	}

	void moveUp() {
		if(yPos > pixelsPerSecond) {
			prevY = yPos;
			yPos -= pixelsPerSecond;
		}
		else {
			prevY = yPos;
			yPos = 0;
		}
	}

	void moveDown(int max) {
		if(yPos + height + pixelsPerSecond < max) {
			prevY = yPos;
			yPos += pixelsPerSecond;
		}
		else {
			prevY = yPos;
			yPos = max - height;
		}
	}
}