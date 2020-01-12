import java.util.Arrays;

public class ParseData extends Parse<String> {
	int outputLength;
	ParseData(int lengthInput, int numOutputs) {
		super(lengthInput, numOutputs);
	}

	@Override
	public String outputTToString(String t) {
		return "" + t;
	}

	@Override
	public double[][] parseOne(String data) {
		double[][] output = new double[1][numOutputs*2];
		Arrays.fill(output[0], 0.0);
		
		String[] s = data.split(" ");
		int[] i = new int[2];
		
		i[0] = Integer.parseInt(s[0]);
		i[1] = Integer.parseInt(s[1]);
		
		output[0][i[0]] = 1.0;
		output[0][numOutputs + i[1]] = 1.0;
		return output;
	}

	@Override
	public double[][] parseUserInput(String input) {
		return parseOne(input);
	}

	@Override
	public String unparseData(double[] data) {
		String toReturn;
		double max = -100;
		int index = 0;
		for(int i = 0; i < numOutputs; i++)
			if (data[i] > max) {
				max = data[i];
				index = i;
			}
		
		toReturn = "" + index;
		max = -100;
		index = 0;
		
		for(int i = numOutputs; i < data.length; i++)
			if (data[i] > max) {
				max = data[i];
				index = i - numOutputs;
			}
		
		return toReturn + " " + index;
	}
}
