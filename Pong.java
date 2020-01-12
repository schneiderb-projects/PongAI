import java.awt.Color;
import java.awt.event.KeyListener;
import java.io.Reader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Pong extends IOManager {
	int[] colors;
	int boardX;
	int boardY;
	int[][] gameBoard;

	int numEpochs = 0;

	int epoch = 0;

	int time = 1;

	int round = 0;

	int numRounds = 0;

	int batchSize = 50;

	int shouldReset = 0;

	static int DONTRESET = 0;
	static int P1WINS = 1;
	static int P2WINS = 2;

	String[][] data = new String[batchSize][2];

	int targetY = 100;

	int inputOutputIndex;

	int yPosP1 = 185;
	int yPosP2 = 185;

	Paddle p1;
	Paddle p2;

	Ball ball;

	ArrayToImage display;

	KeyListener listener;

	boolean doTrain = true;

	Pong(int boardX, int boardY, int[] colors) throws InterruptedException {
		this.boardX = boardX;
		this.boardY = boardY;
		this.colors = colors;

		gameBoard = initBoard(boardX, boardY);

		display = new ArrayToImage(this, gameBoard, colors);

		listener = display.getListener();

		ball = new Ball((int)(boardX * .9),boardY/2, boardX*boardY*8/(boardX*boardY), 3, (int)Math.round((boardX*boardY*20/(boardX*boardY))), this);
		p1 = new Paddle(10,boardY/2,boardX*8/boardX,boardX*50/boardX,1,boardX*boardY*10/(boardX*boardY));
		p2 = new Paddle(boardX-ball.length-p1.length,p1.yPos,p1.length,p1.height,2,p1.pixelsPerSecond);
	}

	void initGame(boolean isTraining,boolean displayOn) {
		shouldReset = Pong.DONTRESET;

		gameBoard = initBoard(boardX, boardY);

		ball = new Ball((int)(boardX * .9),boardY/2, ball.height, 3, ball.pixelsPerSecond, this);

		if(displayOn) {
			display.refreshImage(gameBoard);
			int[][] changed = renderAll(gameBoard);
			display.refreshChanged(changed);
		}
	}

	void runGame(boolean isTraining) throws InterruptedException {
		if(isTraining) {
			while(this.inputOutputIndex < this.batchSize) {
				initGame(isTraining,false);
				beginTrainingGame();
			}
			//n2.train(10, input, output, true);
			inputOutputIndex = 0;
		}
		else
		{
			initGame(isTraining,true);
			beginUserGame();
		}
	}

	void gameOver(boolean winner) {
		display.gameOver(winner ? "Player 1 wins! " : "Player 2 wins! ");
	}

	int[] beginUserGame() throws InterruptedException {
		while(!display.f.hasFocus());
		int[] toReturn = ball.move(p1, p2, boardY - 1, 0);

		if(this.shouldReset == Pong.P1WINS) {
			System.out.println("\n+--------+\n|You Win!|\n+--------+\n");
			//System.out.println("Target Y: " + targetY + " Paddle Y: " + (p2.yPos + p2.height / 2));
			return null;
		}

		if(this.shouldReset == Pong.P2WINS) {
			System.out.println("+------------------+\n|The Computer Wins!|\n+------------------+\n");
			//System.out.println("Target Y: " + targetY + " Paddle Y: " + (p2.yPos + p2.height / 2));
			return null;
		}

		if(display.isDown) {
			p1.moveDown(boardY - 1);
		}

		if(display.isUp) {
			p1.moveUp();
		}

		if(targetY > (p2.yPos + (p2.height / 2))) {
			//System.out.println("Moving Down - Target Y: " + targetY + " Paddle Y: " + (p2.yPos + p2.height / 2));
			p2.moveDown(boardY - 1);
		}

		if(targetY < (p2.yPos + (p2.height / 2))) {
			//System.out.println("Moving Up - Target Y: " + targetY + " Paddle Y: " + (p2.yPos + p2.height / 2));
			p2.moveUp();
		}

		if(shouldReset == Pong.DONTRESET) {
			display.refreshChanged(renderAll(gameBoard));
		}

		TimeUnit.MILLISECONDS.sleep(50);
		return toReturn;
	}

	int[] beginTrainingGame() throws InterruptedException {
		int[] output = ball.move(p1, p2, boardY, 0);

		if(p1.yPos + (Math.random() * (p1.height -1)) > ball.yPos) {
			p1.moveUp();
		}

		if(p1.yPos + p1.height - (Math.random() * (p1.height - 1)) < ball.yPos)
		{
			p1.moveDown(boardY - 1);
		}

		if(targetY > p2.yPos + p2.height - 1) {
			p2.moveDown(boardY - 1);
		}

		if(targetY < p2.yPos) {
			p2.moveUp();
		}
		if(time != 0) {
			display.refreshChanged(renderAll(gameBoard));

			try {
				TimeUnit.NANOSECONDS.sleep(time);
			} catch(Exception e) {
				System.out.print(e.toString());
			}
		}

		if(shouldReset != 0)
			this.initGame(true, time > 0);

		return output;
	}

	int[][] renderAll(int[][] arr) {
		return concat(ball.renderPiece(arr),
				p1.renderPiece(arr),
				p2.renderPiece(arr));
	}

	private int[][] concat(int[][] a, int[][] b, int[][] c) {
		int[][] result = new int[a.length + b.length + c.length][];
		int i;
		for(i = 0; i < a.length; i++)
			result[i] = a[i];

		for(; i < a.length + b.length; i++)
			result[i] = b[i - a.length];

		for(; i < a.length + b.length + c.length; i++)
			result[i] =  c[i - a.length - b.length];

		return result;
	}

	int[][] initBoard(int xSize, int ySize) {
		int[][] arr = new int[xSize][ySize];
		for(int x = 0; x < xSize; x++)
			for(int y = 0; y < ySize; y++)
				arr[x][y] = 0;
		return arr;
	}

	@Override
	public boolean nextStep() { 
		epoch = 0;
		System.out.print("  1) train \n  2) play\n  3) save net\n  4) exit\nEnter input: ");
		String input = getInput();

		if(input.equals("1")) {
			System.out.print("Enter number of training rounds: ");
			input = getInput();
			System.out.print("Enter desired nS delay (game speed = 50,000,000): ");
			String delay = getInput();
			System.out.print("Enter number of rounds per each train: ");
			String trainInput = getInput();

			try {
				time = Integer.parseInt(delay);

				int parsed = Integer.parseInt(input);

				int numTrain = Integer.parseInt(trainInput);
				train(parsed,numTrain);
			}
			catch (Exception e) {
				System.out.println("invalid input: " + input + "\n");
			}
		}

		else if(input.equals("2")) {
			this.initGame(false, true);
			this.play();
		}

		else if(input.equals("3")) {
			System.out.println(save() ? "Save successfull" : "Save failed");
		}

		else if(input.equals("4")) {
			return false;
		}

		else {
			System.out.println("invalid input");
		}
		return true;
	}

	void train(int numEpochs, int numRound) {
		this.numEpochs = numEpochs;
		this.numRounds = numRound;
		while(epoch < numEpochs) {
			if(this.inputOutputIndex >= batchSize) {
				inputOutputIndex = 0;
				parent.parseData(data);
				trainByRounds(numRound);
			}
			int[] output = null;
			try {
				output = beginTrainingGame();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(output != null) {
				if(output.length == 2) {
					//System.out.println("Round: " + (round % 50 + 1));
					data[this.inputOutputIndex][0] = "" + output[0] + " " + output[1];
					double[] result = testInput("" + output[0] + " " + output[1]);
					//System.out.println(Arrays.toString(result));
					//targetY = unparseResult(result);
				}
				else {
					round++;
					data[this.inputOutputIndex++][1] = "" + output[0];
					//System.out.println("data: " + Arrays.deepToString(data[inputOutputIndex-1]));
				}
			}
		}
	}

	void play() {
		while(this.shouldReset == Pong.DONTRESET) {
			int[] output = null;
			try {
				output = beginUserGame();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(output != null && output.length == 2) {
				double[] result = testInput("" + output[0] + " " + output[1]);
				targetY = unparseResult(result);
			}
		}
	}

	@Override
	public boolean shouldLoadData() {
		System.out.print("Load data(y/n): ");
		String input = getInput();
		return input.equals("y") ? true : false;
	}

	@Override
	public void dispEpochTrainByRound(int trainingRound, double[][] a1, double[][] a2, double[][] w1, double[][] w2,
			double[][] b1, double[][] b2, double[][] expected, double[][] input) {
		double[][] d = MatrixMath.transpose(expected);
		System.out.println("Epoch: " + (epoch + 1) + "/" + numEpochs + "  Round: " + ((trainingRound % numRounds) + 1) + "/" + numRounds + " Trained: " + trainingRound + " times");
	}

	@Override
	public void dispEpochTrainByAccuracy(int trainingRound, double[][] a1, double[][] a2, double[][] w1, double[][] w2,
			double[][] b1, double[][] b2, double[][] expected, double[][] input) {

	}

	@Override
	public void dispFinal(int trainingRound, double[][] a1, double[][] a2, double[][] w1, double[][] w2, double[][] b1,
			double[][] b2, double[][] expected, double[][] input) {
		epoch++;
	}

	public void invalidInput() {
		System.out.println("ERROR: input incorrectly formated");
	}

	public void dispResults(String str, double[] odds) {
		double max = 0;
		int index = 0;
		for(int i = 0; i < odds.length; i++) 
			if(odds[i] > max) {
				max = odds[i];
				index = i;
			}
		targetY = index;
	}

	public void loadSuccess(boolean success) {
		System.out.println("Neural Net successfully loaded");
	}

	public int unparseResult(double[] data) {
		double max = -100;
		int index = 0;
		for(int i = 0; i < data.length; i++)
			if (data[i] > max) {
				max = data[i];
				index = i;
			}

		return index;
	}

	@SuppressWarnings("unused")
	private String getInput()
	{
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);
		reader.useDelimiter("\n");
		String input = "";
		try {
			input += reader.next();
		}
		catch (Exception e)	{
			System.out.println(e);
		}
		return input;
	}
}