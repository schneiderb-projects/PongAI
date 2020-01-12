//import java.util.Arrays;

public class NeuralNet {
	/*
	 * key:
	 * 	W = weight matrix
	 * 	B = bias matrix
	 * 	A = actual result at current layer
	 * 	Input = user given list of inputs
	 * 	Expected = user given list of corresponding outputs
	 * 	Costs = summed error
	 */


	//inputs and expected outputs
	double[][] mInput, mExpected;

	//number nodes at each layer
	int numHiddenNodes, numInputNodes, numOutputNodes;

	//number of expected results 
	int numExpected;


	//current weights
	double[][] w1, w2;

	//current biases
	double[][] b1, b2;

	//actual
	double[][] a1, a2;
	double cost;

	//training rate
	double mTrainingRate;

	int trainingRound = 0;

	public NeuralNet(int numInputNodes, int numOutputNodes, int numHiddenNodes, double trainingRate)
	{
		this.numInputNodes = numInputNodes;
		this.numOutputNodes = numOutputNodes;
		this.numHiddenNodes = numHiddenNodes;
		this.mTrainingRate = trainingRate;
		buildNet();
	}

	public void buildNet() {

		//mNumExpect is the amount of training data given

		//initialize layer 1 weights and biases as matrices
		w1 = MatrixMath.random(numHiddenNodes, numInputNodes);
		b1 = MatrixMath.random(numHiddenNodes,1);

		//initialize layer 2 weights and biases as matrices
		w2 = MatrixMath.random(numOutputNodes, numHiddenNodes);
		b2 = MatrixMath.random(numOutputNodes,1);
	}

	public void train(int rounds, double[][] input, double[][] expected, boolean isBackProp)
	{
		mInput = input;
		mExpected = expected;
		numExpected = expected.length;

		//transpose given input and expected output (cuts out some steps later)
		mInput = MatrixMath.transpose(mInput);
		mExpected = MatrixMath.transpose(mExpected);

		//train the neural net against all given inputs and expected outputs
		for(int i = 0; i < rounds; i++)
		{
			fowardPropogate();
			if(isBackProp) {
				backPropogate();
				++trainingRound;
			}
			//System.out.print(("\n======Epoch " + trainingRound + "========\n"));
		}
	}

	public void fowardPropogate()
	{
		//input layer to layer 2
		double[][] z1 = MatrixMath.rowAdd(MatrixMath.dot(w1,mInput), b1);
		a1 = MatrixMath.sigmoid(z1);

		//layer 2 to output layer
		double[][] z2 = MatrixMath.rowAdd(MatrixMath.dot(w2,a1), b2);
		a2 = MatrixMath.sigmoid(z2);

		//use activation function to calculate cost
		cost = MatrixMath.cross_entropy(numExpected, mExpected, a2);
	}

	public String printMatrix(double[][] matrix)
	{
		return MatrixMath.print(matrix);
	}

	public double[][] input(double[][] userInput)
	{
		double[][] tX = MatrixMath.transpose(userInput);

		double[][] tZ1 = MatrixMath.rowAdd(MatrixMath.dot(w1, tX), b1);
		double[][] tA1 = MatrixMath.sigmoid(tZ1);

		double[][] tZ2 = MatrixMath.rowAdd(MatrixMath.dot(w2, tA1), b2);
		double[][] tA2 = MatrixMath.sigmoid(tZ2); // Prediction (Get Output here)

		return tA2;
	}
	public void backPropogate()
	{
		//layer 2
		double[][] dZ2 = MatrixMath.subtract(a2, mExpected);
		double[][] dW2 = MatrixMath.scalarDivide(MatrixMath.dot(dZ2, MatrixMath.transpose(a1)), numExpected);
		double[][] dB2 = MatrixMath.scalarDivide(dZ2, numExpected);

		//layer 1
		double[][] dZ1 = MatrixMath.elementWiseMult(MatrixMath.dot(MatrixMath.transpose(w2), dZ2), 
				MatrixMath.scalarSubtract(MatrixMath.power(a1, 2), 1.0));
		double[][] dW1 = MatrixMath.scalarDivide(MatrixMath.dot(dZ1, MatrixMath.transpose(mInput)), numExpected);
		double[][] dB1 = MatrixMath.scalarDivide(dZ1,numExpected);

		//adjustments layers 1-2
		w1 = MatrixMath.subtract(w1, MatrixMath.scalarMult(dW1, mTrainingRate));
		b1 = MatrixMath.subtract(b1, MatrixMath.scalarMult(dB1, mTrainingRate));

		//adjustments layers 1-output
		w2 = MatrixMath.subtract(w2, MatrixMath.scalarMult(dW2, mTrainingRate));
		b2 = MatrixMath.subtract(b2, MatrixMath.scalarMult(dB2, mTrainingRate));
	}

	double getAccuracy() {
		double[][] transposeA = MatrixMath.transpose(a2);
		double[][] transposeExpected = MatrixMath.transpose(mExpected);
		double[][] roundedA = new double[transposeA.length][transposeA[0].length];
		int incorrect = 0;
		for(int k = 0; k < transposeA.length; k++)
			for(int j = 0; j < transposeA[0].length; j++)
			{
				roundedA[k][j] = Math.round(transposeA[k][j]);
				if (roundedA[k][j] != transposeExpected[k][j])
				{
					incorrect++;
					break;
				}
			}
		return (1.0-(incorrect/(double)(transposeA.length)))*100.0;
	}

	//getters
	public double[][] getInput() {
		return mInput;
	}

	public double[][] getExpected() {
		return mExpected;
	}

	public double[][] getW1() {
		return w1;
	}

	public double[][] getW2() {
		return w2;
	}

	public double[][] getB1() {
		return b1;
	}

	public double[][] getB2() {
		return b2;
	}

	public double[][] getA1() {
		return a1;
	}

	public double[][] getA2() {
		return a2;
	}

	public int getTrainingRound() {
		return trainingRound;
	}
}
