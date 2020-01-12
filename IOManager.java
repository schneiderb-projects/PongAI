public abstract class IOManager {
	@SuppressWarnings("rawtypes")
	public NeuralNetDriver parent;

	@SuppressWarnings("rawtypes")
	public void setParent(NeuralNetDriver parent) {
		this.parent = parent;
	}

	protected boolean save() {
		return parent.save();
	}

	protected void trainByAccuracy(double accuracy) {
		parent.trainByAccuracy(accuracy);
	}

	protected void trainByRounds(int rounds) {
		parent.train(rounds);
	}

	protected double[] testInput(String s) {
		return parent.testInput(s);
	}

	protected double getAccuracy() {
		return parent.getAccuracy();
	}

	protected String unparseData(double[] data) {
		return parent.unparseData(data);
	}

	/**
	 * determine what to do when runDriver() is called, repeated until returns true <p>
	 * recommend getting user input
	 * @return return true to run nextStep() again or false to exit training loop
	 */
	public abstract boolean nextStep();
	
	/**
	 * ask user if the FileManager should load data
	 */
	public abstract boolean shouldLoadData();

	//different printing formats, some of these are used, but could be helpful ways to 
	//display information at some point
	/**
	 * @see IOManager#dispEpochTrainByRound(int, double[][], double[][], double[][], double[][], double[][], double[][], double[][], double[][])
	 * 
	 * decide what to display during each round of training. <p>
	 * Recommend using trainingRound parameter and getAccuracy() method.
	 * 
	 * @param trainingRound The number training rounds currently completed
	 * @param input given input values for each node
	 * @param a1 actual node values at hidden layer of NeuralNet
	 * @param a2 actual node values at output layer of NeuralNet
	 * @param expected expected output, expected values for a2.
	 * @param w1 weights for nodes in input layer
	 * @param w2 weights for nodes in hidden layer
	 * @param b1 biases from input to hidden layer
	 * @param b2 biases from hidden to output layer
	 */
	public abstract void dispEpochTrainByRound(int trainingRound, double[][] a1, double[][] a2, double[][] w1,
			double[][] w2, double[][] b1, double[][] b2, double[][] expected, double[][] input);

	/**
	 * @see IOManager#dispEpochTrainByAccuracy(int, double[][], double[][], double[][], double[][], double[][], double[][], double[][], double[][])
	 * 
	 * decide what to display during each round of training. <p>
	 * Recommend using trainingRound parameter and getAccuracy() method.
	 * 
	 * @param trainingRound The number training rounds currently completed
	 * @param input given input values for each node
	 * @param a1 actual node values at hidden layer of NeuralNet
	 * @param a2 actual node values at output layer of NeuralNet
	 * @param expected expected output, expected values for a2.
	 * @param w1 weights for nodes in input layer
	 * @param w2 weights for nodes in hidden layer
	 * @param b1 biases from input to hidden layer
	 * @param b2 biases from hidden to output layer
	 */
	public abstract void dispEpochTrainByAccuracy(int trainingRound, double[][] a1, double[][] a2, double[][] w1, 
			double[][] w2, double[][] b1, double[][] b2, double[][] expected, double[][] input);

	/**
	 * @see IOManager#dispFinal(int, double[][], double[][], double[][], double[][], double[][], double[][], double[][], double[][])
	 * 
	 * decide what to display during each round of training. <p>
	 * Recommend using trainingRound parameter and getAccuracy() method.
	 * 
	 * @param trainingRound The number training rounds currently completed
	 * @param input given input values for each node
	 * @param a1 actual node values at hidden layer of NeuralNet
	 * @param a2 actual node values at output layer of NeuralNet
	 * @param expected expected output, expected values for a2.
	 * @param w1 weights for nodes in input layer
	 * @param w2 weights for nodes in hidden layer
	 * @param b1 biases from input to hidden layer
	 * @param b2 biases from hidden to output layer
	 */
	public abstract void dispFinal(int trainingRound, double[][] a1, double[][] a2, double[][] w1, 
			double[][] w2, double[][] b1, double[][] b2, double[][] expected, double[][] input);

	/**
	 * what is displayed if an invalid input is given. Ignore if don't care.
	 */
	public abstract void invalidInput();

	/**
	 * what is displayed after a user input is processed
	 * @param data
	 */
	public abstract void dispResults(String str, double[] odds);
	
	/**
	 * what is displayed if load is successful or unsuccessful
	 * @param success true if data loaded successfully
	 */
	public abstract void loadSuccess(boolean success);

	//defaultIOManager
	static class defaultIOManager extends IOManager{
		public boolean nextStep() {
			return false;
		}

		public String getInput() {
			return null;
		}

		public void dispEpochTrainByRound(int i, double[][] a1, double[][] a2, double[][] w1, double[][] w2,
				double[][] b1, double[][] b2, double[][] expected, double[][] input) {}

		public void dispEpochTrainByAccuracy(int i, double[][] a1, double[][] a2, double[][] w1, double[][] w2,
				double[][] b1, double[][] b2, double[][] expected, double[][] input) {}

		public void dispFinal(int i, double[][] a1, double[][] a2, double[][] w1, double[][] w2, double[][] b1,
				double[][] b2, double[][] expected, double[][] input) {}
		
		public void invalidInput() {}

		public void dispResults(String input, double[] odds) {}

		public boolean shouldLoadData() {
			return false;
		}

		public void loadSuccess(boolean success) {}
	}
}