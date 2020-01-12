import java.util.Arrays;
import java.util.Random;

public class MatrixMath {
	//arrays go by row and then columns
	public static double[][] dot(double[][] x, double[][] y)
	{
		int heightX = x.length;
		int widthX = x[0].length;
		int widthY = y[0].length;
		double[][] temp= new double[heightX][widthY];
		for(int i = 0; i < heightX; i++)
			for(int j = 0; j < widthY; j++)
				for(int k = 0; k < widthX; k++)
					temp[i][j] += x[i][k] * y[k][j];
		return temp;
	}
	public static double[][] transpose(double[][] x)
	{
		double[][] temp = new double[x[0].length][x.length];
		for(int i = 0; i < x.length; i++)
			for(int j = 0; j < x[0].length; j++)
				temp[j][i] = x[i][j];
		return temp;
	}

	public static String print(double[][] x)
	{
		String output = "";
		for(double[] row: x)
			output+=(Arrays.toString(row)+"\n");
		return output;
	}

	public static double[][] rowAdd(double[][] matrix, double[][] vector) {
		int m = matrix.length;
		int n = matrix[0].length;
		double[][] c = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				c[i][j] = matrix[i][j] + vector[i][0];
			}
		}
		return c;
	}

	public static double[][] elementWiseMult(double[][] x, double[][] y)
	{
		int width = x[0].length;
		int height = y.length;
		double[][] temp = new double[height][width];
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++)
				temp[i][j] = x[i][j]*y[i][j];
		return temp;		
	}

	public static double[][] scalarMult(double[][] x, double scalar)
	{
		double[][] temp = new double[x.length][x[0].length];
		for(int i = 0; i < temp.length; i++)
			for(int j = 0; j < temp[0].length; j++)
				temp[i][j] = x[i][j]*scalar;
		return temp;
	}

	public static double[][] scalarDivide(double[][] x, double scalar)
	{
		double[][] temp = new double[x.length][x[0].length];
		for(int i = 0; i < temp.length; i++)
			for(int j = 0; j < temp[0].length; j++)
				temp[i][j] = x[i][j]/scalar;
		return temp;
	}

	public static double[][] power(double[][] x, double power)
	{
		double[][] temp = new double[x.length][x[0].length];
		for(int i = 0; i < temp.length; i++)
			for(int j = 0; j < temp[0].length; j++)
				temp[i][j] = Math.pow(x[i][j],power);
		return temp;
	}
	public static String dimensions(double[][] a) {
		int height = a.length;
		int length = a[0].length;
		String Vshape = "(" + height + "," + length + ")";
		return Vshape;
	}
	public static double[][] sigmoid(double[][] a) {
		int height = a.length;
		int length = a[0].length;
		double[][] temp = new double[height][length];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < length; j++) {
				temp[i][j] = (1.0 / (1 + Math.exp(-a[i][j])));
			}
		}
		return temp;
	}
	public static double cross_entropy(int batch_size, double[][] Y, double[][] A) {
		int height = A.length;
		int width = A[0].length;
		double[][] temp = new double[height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				temp[i][j] = (Y[i][j] * Math.log(A[i][j])) + ((1 - Y[i][j]) * Math.log(1 - A[i][j]));
			}
		}

		double sum = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				sum += temp[i][j];
			}
		}
		return -sum / batch_size;
	}
	public static double[][] add(double[][] x, double[][] y)
	{
		int width = x[0].length;
		int height = y.length;
		double[][] temp = new double[height][width];
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++)
				temp[i][j] = x[i][j] + y[i][j];
		return temp;
	}
	public static double[][] vectorAdd(double[][] x, double[][] vector)
	{
		int width = x[0].length;
		int height = x.length;
		double[][] temp = new double[height][width];
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++)
				temp[i][j] = x[i][j] + vector[i][0];
		return temp;
	}
	public static double[][] subtract(double[][] x, double[][] y)
	{
		int height = x.length;
		int width = x[0].length;
		double[][] c = new double[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				c[i][j] = x[i][j] - y[i][j];
			}
		}
		return c;
	}
	public static double[][] scalarSubtract(double[][] x, double scalar)
	{
		double[][] temp = new double[x.length][x[0].length];
		for(int i = 0; i < temp.length; i++)
			for(int j = 0; j < temp[0].length; j++)
				temp[i][j] = scalar - x[i][j];
		return temp;
	}

	public static double[][] roundAll(double[][] toRound) {
		double[][] rounded = new double[toRound.length][toRound[0].length];
		for(int k = 0; k < toRound.length; k++)
			for(int j = 0; j < toRound[0].length; j++)
				rounded[k][j] = Math.round(toRound[k][j]);
		return rounded;
	}

	public static double[][] random(int x, int y) {
		double[][] matrix = new double[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				matrix[i][j] = Math.random();
			}
		}
		return matrix;
	}

}