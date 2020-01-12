import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
//import java.util.Date;
import java.io.IOException;

public class FileManager {
	String filePath;

	FileManager() {
		filePath = "NeuralNet";
	}

	FileManager(String filePath) {
		this.filePath = filePath;
	}

	public boolean save(double[][] w1, double[][] w2, double[][] bias1, double[][] bias2)
	{
		if(saveIt(w1, "weight1") && saveIt(w2, "weight2") && saveIt(bias1, "bias1") && saveIt(bias2, "bias2"))
			return true;
		else 
			return false;
	}
	public boolean saveIt(double[][] data, String fileName)
	{
		createNew();
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < data.length; i++)//for each row
		{
			for(int j = 0; j < data[0].length; j++)//for each column
			{
				builder.append(data[i][j]+"");//append to the output string
				if(j < data[0].length - 1)//if this is not the last row element
					builder.append(",");//then add comma (if you don't like commas you can use spaces)
			}
			builder.append("\n");//append new line at the end of the row
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + "/"+fileName+".txt"));
			writer.write("");
			writer.write(builder.toString());//save the string representation of the board
			writer.close();
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}

	public boolean load(NeuralNet nn)
	{
		double[][] w1 = loadIt("weight1", nn.w1.length, nn.w1[0].length);
		double[][] w2 = loadIt("weight2", nn.w2.length, nn.w2[0].length);
		double[][] b1 = loadIt("bias1", nn.b1.length, nn.b1[0].length);
		double[][] b2 = loadIt("bias2", nn.b2.length, nn.b2[0].length);
		if(w1 != null && w2 != null && b1 != null && b2 != null)
		{
			nn.w1 = w1;
			nn.w2 = w2;
			nn.b1 = b1;
			nn.b2 = b2;
			return true;
		}
		return false;
	}

	public double[][] loadIt(String fileName, int height, int width)
	{
		double[][] data = new double[height][width];
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filePath+"/"+fileName+".txt"));
			String line = "";
			int row = 0;
			while((line = reader.readLine()) != null)
			{
				String[] cols = line.split(",");
				int col = 0;
				for(String  c : cols)
				{
					data[row][col] = Double.parseDouble(c);
					col++;
				}
				row++;
			}
			reader.close();
		}
		catch(Exception e)
		{
			return null;
		}
		return data;
	}
	public void createNew()
	{
		File file = new File(filePath);
		try {
			if (!file.exists())
				file.mkdir();

			File w1 = new File(filePath + "/weight1.txt");
			if (!w1.exists())
				w1.createNewFile();

			File w2 = new File(filePath + "/weight2.txt");
			if (!w2.exists())
				w2.createNewFile();

			File b1 = new File(filePath + "/bias1.txt");
			if (!b1.exists())
				b1.createNewFile();
			File b2 = new File(filePath + "/bias2.txt");
			if (!b2.exists())
				b2.createNewFile();
		} catch (IOException e) {}
	}
}
