package me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Resources;

import java.util.ArrayList;

public class Matrix {
	//Nested Classes
	public static interface mFunc{
		public double get(double input);
	}
	
	public static class Shape{
		private int rows;
		private int cols;
		public Shape(int rows, int cols) {
			this.rows = rows; this.cols = cols;
		}
		public int getRows() {
			return this.rows;
		}
		public int getCols() {
			return this.cols;
		}
		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(o instanceof Shape) {
				if(((Shape) o).getRows() == this.getRows() && ((Shape) o).getCols() == this.getCols()) {
					return true;
				}
			}
			return false;
		}
	}
	
	//Variables
	
	private ArrayList<Double> data = new ArrayList<>();
	
	private Shape shape;

	//Constructors
	
	public Matrix(int rows, int cols) {
		this.shape = new Shape(rows, cols);
		this.data.ensureCapacity(rows * cols);
		for(int i = 0; i < rows * cols; i++) {
			this.data.add(0d);
		}
	}

	public double getMean(){
		double sum = 0d;
		for(double d : this.data){
			sum += d;
		}
		return sum / this.data.size();
	}

	public double getStandardDeviation(){
		double sum = 0d;
		double mean = this.getMean();
		for(double d : this.data){
			sum += Math.pow(d - mean, 2);
		}
		sum /= this.data.size();
		return Math.sqrt(sum);
	}

	public Matrix(Shape shape){
		this(shape.getRows(), shape.getCols());
	}
	
	//Functions
	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < this.rowCount(); i++) {
        	for (int l = 0; l < this.colCount(); l++) {
        		str += this.get(i, l) + " ";
        	}
        	str += "\n";
    	}
		return str;
	}
	//Functions -- Management
	public void setData(ArrayList<Double> d) {
		this.data = d;
	}
	
	public ArrayList<Double> getData(){
		return this.data;
	}
	
	public double get(int row, int col) {
		return this.data.get(row * this.colCount() + col);
	}
	
	public void set(int row, int col, double val) {
		this.data.set(row * this.colCount() + col, val);
	}
	
	public int rowCount() {
		return this.shape.getRows();
	}
	public int colCount() {
		return this.shape.getCols();
	}
	public Shape getShape() {
		return this.shape;
	}
	
	//Functions -- Miscellaneous
	
	public double getTotal() {
		double total = 0f;
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				total += this.get(rows, cols);
			}
		}
		return total;
	}
	
	public void wipe() {
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				this.set(rows, cols, 0f);
			}
		}
	}
	
	//Functions -- Arithmetic
	
	public Matrix vMultiply(final Matrix m) {
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				output.set(rows, cols, this.get(rows, cols) * m.get(rows, cols));
			}
		}
		return output;
	}
	public Matrix vDivide(final Matrix m) {
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				output.set(rows, cols, this.get(rows, cols) / m.get(rows, cols));
			}
		}
		return output;
	}
	public Matrix vMultiplyAll(double m) {
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				output.set(rows, cols, this.get(rows, cols) * m);
			}
		}
		return output;
	}
	public Matrix vDivideAll(double m) {
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				output.set(rows, cols, this.get(rows, cols) / m);
			}
		}
		return output;
	}
	public Matrix vAddAll(double m) {
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				output.set(rows, cols, this.get(rows, cols) + m);
			}
		}
		return output;
	}
	public Matrix getCopy() {
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				output.set(rows, cols, this.get(rows, cols));
			}
		}
		return output;
	}
	public void multiply(final Matrix m) {
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				this.set(rows, cols, this.get(rows, cols) * m.get(rows, cols));
			}
		}
	}
	public Matrix vAdd(final Matrix m) {
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				output.set(rows, cols, this.get(rows, cols) + m.get(rows, cols));
			}
		}
		return output;
	}
	public void add(final Matrix m) {
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				this.set(rows, cols, this.get(rows, cols) + m.get(rows, cols));
			}
		}
	}
	public Matrix vSubtract(final Matrix m) {
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				output.set(rows, cols, this.get(rows, cols) - m.get(rows, cols));
			}
		}
		return output;
	}
	public void subtract(final Matrix m) {
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				this.set(rows, cols, this.get(rows, cols) - m.get(rows, cols));
			}
		}
	}
	public void updateAll(mFunc func) {
		for(int rows = 0; rows < this.rowCount(); rows++) {
			for(int cols = 0; cols < this.colCount(); cols++) {
				this.set(rows, cols, func.get(this.get(rows, cols)));
			}
		}
	}
	public Matrix vZeroPad(int amount){
		Matrix output = new Matrix(this.rowCount()+(amount*2), this.colCount()+(amount*2));
		for(int i = 0; i < this.rowCount(); i++){
			for(int i1 = 0; i1 < this.colCount(); i1++){
				output.set(i+amount,i1+amount, this.get(i,i1));
			}
		}
		return output;
	}
	public Matrix vFlip(){
		Matrix output = new Matrix(this.rowCount(), this.colCount());
		for(int i = 0; i < this.rowCount(); i++){
			for(int l = 0; l < this.colCount(); l++){
				output.set(this.rowCount()-(i+1), this.colCount()-(l+1), this.get(i, l));
			}
		}
		return output;
	}
}
