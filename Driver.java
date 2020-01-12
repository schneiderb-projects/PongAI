import java.awt.Color;

public class Driver {
	public static void main(String args[]) throws InterruptedException 
	{
		int boardX = 700;
		int boardY = 400;
		int[] c = {Color.darkGray.getRGB(),Color.GREEN.getRGB(),
				Color.YELLOW.getRGB(),Color.RED.getRGB()};
		NeuralNetDriver<String> d = new NeuralNetDriver<String>(
				new ParseData(boardY*2, boardY),
				new Pong(boardX,boardY,c),
				new FileManager("PongNet"),
				600, .5);
				
		d.runDriver();
		System.exit(0);
	}
}
