public abstract class Parse<T> {
	public double[][] outputCodes;
	String[] names;

	public int numOutputs;
	public int lengthOfInput;

	Parse(int lengthInput,int numOutputs) {
		lengthOfInput = lengthInput;
		this.numOutputs = numOutputs;
	}

	/**
	 * @param t object of type T to convert to string
	 * @return return String representing given T
	 */
	public String unparseUserInput(double[][] x) {
		String str = "";
		for(double[] y: x)
			str+= " " + unparseData(y);
		str = str.substring(1);
		return str;
	}

	/**
	 * converts the given outputs of type T into a String
	 * @param t object of type T to convert to string
	 * @return return String representing given T
	 */
	public abstract String outputTToString(T t);

	/**
	 * @param  data	 all data expecting the given output code as a result
	 * @return returns double[][] representing given data in binary in the format of {{1st},{2nd},...,{nth}}
	 * <P>Example: parseOne("001 010 100") returns {{0,0,1},{0,1,0},{1,0,0}}
	 */	
	public abstract double[][] parseOne(T data);

	/**
	 * @param  data	 all data expecting the given output code as a result
	 * @param  outputCode  expected outputCode for given input
	 * @return returns double[][][] representing given data and outputCode in the format of {{1st,outputCode},{2nd,outputCode},...,{nth,outputCode}}
	 * <P>Example: parse("001 010 100", {0,1}) returns {{{0,0,1},{0,1}},{{0,1,0},{0,1}},{{1,0,0},{0,1}}}
	 */	
	public double[][][] parse(T data, double[] outputCode) {
		return addOutputCode(parseOne(data),outputCode);
	}

	/**
	 * @param  input input given by user
	 * @return returns double[][] representing the users given input
	 */
	public abstract double[][] parseUserInput(String input);

	/**
	 * @param  data data to unparse
	 * @return returns String containing unparsed data
	 */	
	public abstract String unparseData(double[] data);

	/**
	 * @param  numOutputs total number of different possible outputs
	 * @return returns double[][] containing generated output codes
	 */	
	public double[][] genOutputs(int numOutputs)
	{
		double[][] generatedOutputs = new double[numOutputs][numOutputs];
		for(int i = 0; i < numOutputs; i++)
		{	
			for(int j = 0; j < numOutputs; j++)
			{
				if (j != i)
					generatedOutputs[i][j] = 0;
				else 
					generatedOutputs[i][j] = 1;
			}
		}
		outputCodes = generatedOutputs;
		return generatedOutputs;
	}

	/**
	 * @param  array1 	array to be merged
	 * @param  array2 	array to be merged
	 * @return returns double[][][] containing all of the elements of the given arrays
	 */	
	public double[][][] concat(double[][][] array1, double[][][] array2)
	{
		if(array1 == null)
			return array2;
		if(array2 == null)
			return array2;
		double[][][] accumulator = array1;
		for(double[][] x: array2)
			accumulator = add(accumulator,x);
		return accumulator;
	}

	/**
	 * @param  array1 	array to be added to
	 * @param  array2 	array to be added
	 * @return returns double[][][] with addition double[][] at the end of the array
	 */
	public double[][][] add(double[][][] array1, double[][] additionalArray)
	{
		double[][][] concatArray = new double[array1.length+1][][];
		int count = 0;
		for(double[][] x: array1)
		{
			concatArray[count] = x;
			count++;
		}
		concatArray[array1.length] = additionalArray;
		return concatArray;
	}

	/**
	 * @param  dataIntArray  collection of data all with the same expected result
	 * @param  outputCode  the code representing the intended output
	 * @return double[][][] of format {{data[0],languageCode},{data[1],languageCode},...,{data[n],languageCode}
	 */
	public double[][][] addOutputCode(double[][] dataIntArray, double[] outputCode) {
		double[][][] combined = {{dataIntArray[0],outputCode}};
		for(int i = 1; i<dataIntArray.length; i++)
		{
			double[][] toConcat = {dataIntArray[i],outputCode};
			combined = add(combined, toConcat);
		}
		return combined;
	}
}
