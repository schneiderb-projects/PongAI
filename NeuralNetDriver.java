import java.util.ArrayList;
import java.util.Arrays;

public class NeuralNetDriver<T> {
	private int nodes;
	private T[][] rawData;

	private double[][][] parsedData;

	private double[][] dataTestInput;
	private double[][] dataTestOutput;

	private double[][] dataTrainInput;
	private double[][] dataTrainOutput;

	private NeuralNet nn;

	private Parse<T> p;

	private IOManager io;

	private FileManager fm;

	private double learningRate;

	private int batchSize;

	/**
	 * @param trainingData data to be used in training 
	 * <p>formated as {{all inputs of expected output 1, expected output 1},...,{all inputs of expected output n, expected output n}}
	 * @param parser an object extending Parse, used to parse the input data into a binary format <p>
	 * @param io object extending IOManager used to manage any user interface with the neural net
	 * @param fm FileManager object to retrieve or save NeuralNet to desired location
	 * @param numHiddenNodes the size of the hidden layer of the net<p>
	 * @param learningRate the learning rate of the net with range from 0 - 1
	 * <p> Higher training rates will increase speed of training but decrease accuracy. Lower training rates will decrease the speed of training but increase accuracy.
	 * @param batchSize The amount of data used for one round of training. Larger batches will decrease the speed of each epoch, but increase the accuracy of the 
	 * training per epoch.<p>
	 * @param testOnlySubset The percentage (0-1 exclusive) of data to be excluded from training data, to be used only when calculating the final accuracy. 
	 * Prevents overtraining from rendering overly high accuracies.<p>
	 * set to any number not 0-1 exclusive to include all data in both training and testing
	 */
	NeuralNetDriver(Parse<T> parser, IOManager io, FileManager fm, int numHiddenNodes, 
			double learningRate) {
		initializeDriver(parser, io, fm, numHiddenNodes, 
				learningRate);
	}

	//	/**
	//	 * overload constructor that doesn't take an IOManager, will use default IOManager instead<p>
	//	 * WARNING: will not load data using given FileManager
	//	 * @param trainingData data to be used in training 
	//	 * <p>formated as {{all inputs of expected output 1, expected output 1},...,{all inputs of expected output n, expected output n}}
	//	 * @param parser an object extending Parse, used to parse the input data into a binary format <p>
	//	 * @param fm FileManager object to retrieve or save NeuralNet to desired location
	//	 * @param numHiddenNodes the size of the hidden layer of the net<p>
	//	 * @param learningRate the learning rate of the net with range from 0 - 1
	//	 * <p> Higher training rates will increase speed of training but decrease accuracy. Lower training rates will decrease the speed of training but increase accuracy.
	//	 * 
	//	 * @param batchSize The amount of data used for one round of training. Larger batches will decrease the speed of each epoch, but increase the accuracy of the training per epoch.<p>
	//	 * @param testOnlySubset The percentage (0-1 exclusive) of data to be excluded from training data, to be used only when calculating the final accuracy. Prevents overtraining from rendering overly high accuracies.<p>
	//	 * set to any number not 0-1 exclusive to include all data in both training and testing
	//	 */
	//	NeuralNetDriver(T[][] trainingData,Parse<T> parser, FileManager fm, int numHiddenNodes, 
	//			double learningRate, int batchSize, double testOnlySubset) {
	//		initializeDriver(trainingData, parser, new IOManager.defaultIOManager(), fm, numHiddenNodes, 
	//				learningRate, batchSize, testOnlySubset);
	//	}

	private void initializeDriver(Parse<T> parser, IOManager io, FileManager fm, 
			int numHiddenNodes, double learningRate) {
		nodes = numHiddenNodes; 
		this.learningRate = learningRate;

		p = parser;

		this.io = io;
		io.setParent(this);

		this.fm = fm;

		p.genOutputs(p.numOutputs);

		nn = buildNet();
	}

	void parseData(T[][] trainingData) {
		rawData = trainingData;

		parsedData = parseTrainingData(rawData);

		//subtract data for testing from data for training to make the accuracy
		//calculations more accurate
		setTrainAndTestData(-1);
	}

	NeuralNet runDriver() {
		while(io.nextStep());
		return nn;
	}

	private void setTrainAndTestData(double percentageForTest) {
		if(percentageForTest != -1) {
			dataTrainInput = new double[parsedData[0].length - (int)(parsedData[0].length*percentageForTest)][];
			dataTrainOutput = new double[parsedData[0].length - (int)(parsedData[0].length*percentageForTest)][];

			dataTestInput = new double[(int)(parsedData[0].length*percentageForTest)][];
			dataTestOutput = new double[(int)(parsedData[0].length*percentageForTest)][];

			int i;
			for(i = 0; i < dataTestInput.length; i++) {
				dataTestInput[i] = parsedData[0][i];
				dataTestOutput[i] = parsedData[1][i];
			}

			int j = i;
			for(; i < parsedData[0].length-1; i++) {
				dataTrainInput[i-j] = parsedData[0][i];
				dataTrainOutput[i-j] = parsedData[1][i];
			}
		}
		else {
			dataTrainInput = new double[parsedData[0].length][];
			dataTrainOutput = new double[parsedData[0].length][];

			for(int i = 0; i < dataTrainInput.length; i++) {
				dataTrainInput[i] = parsedData[0][i];
				dataTrainOutput[i] = parsedData[1][i];
			}

			dataTestInput = dataTrainInput;
			dataTestOutput = dataTrainOutput;
		}
	}

	private NeuralNet buildNet() {
		nn = new NeuralNet(p.lengthOfInput,p.numOutputs,nodes,learningRate);
		if(io.shouldLoadData())
			io.loadSuccess(load());
		return nn;
	}

	private double[][][] parseTrainingData(T[][] data) {
		double[][][] trainingData = null;
		for(int i = 0; i < data.length; i++) 
			trainingData = p.concat(trainingData, p.parse(data[i][0],p.outputCodes[Integer.parseInt(data[i][1].toString())]));
		double[][][] randomData = randomize(trainingData,trainingData.length);
		return randomData;
	}

	public double[] testInput(String input) {
		double[] odds = new double[p.numOutputs];
		double[][] dataArray = p.parseUserInput(input);
		if(dataArray[0].length == 0) {
			io.invalidInput();
			return odds;
		}

		odds = testPhrase(dataArray);
		String str = p.unparseUserInput(dataArray);
		io.dispResults(str, odds);

		return odds;
	}

	private double[] testPhrase(double[][] dataArray)
	{
		double[] odds = new double[p.numOutputs];
		double[] result;
		if(dataArray.length > 1) {
			int[] languageCounts = new int[p.numOutputs];
			Arrays.fill(languageCounts, 0);
			double maxAccuracy = 1;
			double max;
			int index = 0;
			boolean skipAdd;

			for(double[] x: dataArray)
			{
				skipAdd = false;
				max = 0;
				result = MatrixMath.transpose(nn.input(new double[][]{x}))[0];
				for(int i = 0; i < p.numOutputs; i++)
				{
					odds[i] += result[i];
					if(max == odds[i]) {
						skipAdd = true;
					}
					if(odds[i] > max) {
						skipAdd = false;
						max = odds[i];
						index = i;
					}
					if(1-result[i] < maxAccuracy)
						maxAccuracy = 1-result[i];
				}
				if(!skipAdd && max > 50)
					languageCounts[index]++;
			}

			for(int i = 0; i < p.numOutputs; i++)
				if(languageCounts[i]==0)
					languageCounts[i] = 1;

			for(int i = 0; i<odds.length;i++)
				odds[i] = languageCounts[i]*(odds[i]/dataArray.length);

			if(dataArray.length > 1)
				linearize(odds,maxAccuracy);
		}
		else
			odds = testData(dataArray, true);
		return odds;
	}

	private double[] linearize(double[] odds, double maxAccuracy) {
		double max = 0;
		for(double x: odds)
			if(x > max)
				max = x;
		for(int i = 0; i < odds.length; i++)
			odds[i] /= max+maxAccuracy;
		return odds;
	}

	public void train(int numTimes)
	{
		double[][] dataInput = null;
		double[][] dataOutput = null;
		double[][][] data;
		for(int i = 0; i<numTimes; i++)
		{
			data = selectSubset(dataTrainInput,dataTrainOutput);
			dataInput = data[0];
			dataOutput = data[1];
			nn.train(1, dataInput,dataOutput, true);
			dispEpochTrainByRound();
		}
		nn.train(1, dataTestInput, dataTestOutput, false);
		dispFinal();
	}

	public void trainByAccuracy(double targetAccuracy)
	{
		double[][] dataInput = null;
		double[][] dataOutput = null;
		double[][][] data;
		double curAccuracy = nn.getAccuracy();
		while(curAccuracy < targetAccuracy) {
			while(curAccuracy < targetAccuracy) {
				data = selectSubset(dataTrainInput,dataTrainOutput);
				dataInput = data[0];
				dataOutput = data[1];
				nn.train(1, dataInput,dataOutput, true);
				curAccuracy = nn.getAccuracy();
				dispEpochTrainByAccuracy();
			}
			nn.train(1, dataTestInput, dataTestOutput, false);
			curAccuracy = nn.getAccuracy();
			dispEpochTrainByAccuracy();
		}
		dispFinal();
	}


	private double[][][] selectSubset(double[][] dataInput,double[][] dataOutput) {
		double[][][] returnArray = new double[2][dataInput.length][];

		for(int i = 0; i < dataInput.length; i++) {
			if(dataInput[i] == null || dataOutput[i] == null) {
				continue;
			}
			returnArray[0][i] = dataInput[i];
			returnArray[1][i] = dataOutput[i];
		}
		return returnArray;
	}

	public boolean load()
	{
		if(fm.load(nn))
			return true;
		else
			return false;
	}

	public boolean save()
	{
		return fm.save(nn.getW1(), nn.getW2(), nn.getB1(), nn.getB2());
	}

	private double[] testData(double[][] myInput, boolean suppressOutput)
	{
		double[][] result = MatrixMath.transpose(nn.input(myInput));
		return result[0];
	}

	private static double[][][] randomize(double[][][] data, int size)
	{
		int index;

		double[][][] randomizedOrder = new double[data.length][data[0].length][data[0][0].length];
		ArrayList<Integer> available = new ArrayList<Integer>();

		for(int k = 0; k<randomizedOrder.length; k++)
			available.add(k);

		for(double[][] set: data)
		{
			index = (int)(Math.random()*available.size());
			randomizedOrder[available.remove(index)] = set;
		}

		double[][] xRandomized = new double[randomizedOrder.length][randomizedOrder[0][0].length];
		double[][] yRandomized = new double[randomizedOrder.length][randomizedOrder[0][1].length];

		for(int j = 0; j<size; j++)
		{
			xRandomized[j]=randomizedOrder[j][0];
			yRandomized[j]=randomizedOrder[j][1];
		}

		double[][][] orderedData = {xRandomized,yRandomized};
		double[][][] orderedBatch = new double[2][size][];

		for(int i = 0; i<size; i++) {
			orderedBatch[0][i] = 
					orderedData[0][i];
			orderedBatch[1][i] = 
					orderedData[1][i];
		}

		return orderedBatch;
	}


	//access methods in IOManager object through NeuralNetDriverObject
	protected void dispEpochTrainByRound()
	{
		io.dispEpochTrainByRound(nn.getTrainingRound(), nn.getA1(), nn.getA2(), nn.getW1(), 
				nn.getW2(), nn.getB1(), nn.getB2(), nn.getExpected(), nn.getInput());
	}

	protected void dispEpochTrainByAccuracy()
	{
		io.dispEpochTrainByAccuracy(nn.getTrainingRound(), nn.getA1(), nn.getA2(), nn.getW1(), 
				nn.getW2(), nn.getB1(), nn.getB2(), nn.getExpected(), nn.getInput());
	}

	protected void dispFinal()
	{
		io.dispFinal(nn.getTrainingRound(), nn.getA1(), nn.getA2(), nn.getW1(), 
				nn.getW2(), nn.getB1(), nn.getB2(), nn.getExpected(), nn.getInput());
	}

	//access methods in NeuralNet object through NeuralNetDriverObject
	protected double getAccuracy() {
		return nn.getAccuracy();
	}

	//access methods in Parse object through NeuralNetDriverObject
	protected String unparseData(double[] data) {
		return p.unparseData(data);
	}

	protected String[] getNames() {
		return p.names;
	}

	protected double[][] parseUserInput(String s) {
		return p.parseUserInput(s);
	}

	public NeuralNet getNeuralNet() {
		return nn;
	}
}