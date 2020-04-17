package me.gravitinos.minigame.gamecore.util.NeuralNetwork.Layers.Dense;

import java.util.ArrayList;
import java.util.Random;

import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.ActivationFunction;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.Matrix;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.Regularization;

public class Node {
	
	//Variables
	
	private double state = 0;
	private Matrix inputWeights;
	private ActivationFunction af;
	private double accInputDer = 0;
	private int numAccDers = 0;
	private double totalInput = 0;
	private double bias = 0.02d;
	public Matrix accWeightDers;
	private Random rand;
	private Matrix prevInput;
	private double dropOutProbability = 0f;
	private boolean regularizationL2 = false;
	private double regularizationRate = 0.0003d;
	private boolean droppedOut = false;
	private ArrayList<Node> sources = new ArrayList<>();

	//RProp
	private Matrix lastAdded;
	private Matrix lr;
	
	//Adaptive Moment Estimation ( ADAM )
	
	double beta_1 = 0.9f;
	double beta_2 = 0.999f;
	
	double iteration = 1;
	
	double nonZero = 0.00000001f;
	
	Matrix m;
	Matrix v;
	
	//Constructors
	
	public Node(int inputs, ActivationFunction acfun, Random rand) {
		this.af = acfun;
		this.rand = rand;
		inputWeights = new Matrix(inputs, 1);

		lastAdded = new Matrix(inputs, 1);
		lr = new Matrix(inputs, 1);
		
		accWeightDers = new Matrix(inputs, 1);
		
		m = new Matrix(inputs, 1);
		v = new Matrix(inputs, 1);
		
		this.bias = (rand.nextGaussian() / inputs * (inputs / 5d));
		
		for(int i = 0; i < inputs; i++) {
			
			inputWeights.set(i, 0, rand.nextGaussian() / inputs * (inputs / 5d));
			
		}
	}
	
	public void setup(int inputs, ActivationFunction acfun) {
		
		this.inputWeights.wipe();
		
		this.af = acfun;
		
		inputWeights = new Matrix(inputs, 1);
		
		accWeightDers = new Matrix(inputs, 1);
		
		m = new Matrix(inputs, 1);
		v = new Matrix(inputs, 1);
		
		this.bias = (rand.nextGaussian() / inputs * (inputs / 5d));
		
		for(int i = 0; i < inputs; i++) {
			
			inputWeights.set(i, 0,  rand.nextGaussian() / inputs * (inputs / 5d));
			
		}
		
	}

	public boolean isRegularizationL2() {
		return regularizationL2;
	}

	public void setRegularizationL2(boolean regularizationL2) {
		this.regularizationL2 = regularizationL2;
	}

	public boolean getDroppedOut() {
		return this.droppedOut;
	}
	public double getDropOutProbability() {
		return this.dropOutProbability;
	}
	public void setDropOutProbability(double prob) {
		this.dropOutProbability = prob;
	}
	public Matrix getWeights() {
		return this.inputWeights;
	}
	public double getState() {
		return this.state;
	}

    public void setBias(double bias) {
        this.bias = bias;
    }

    public void setState(double f) {
		this.state = f;
	}
	
	public void updateState(Matrix input) {
		
		if(rand.nextDouble() < this.dropOutProbability) {
			this.droppedOut = true;
			this.state = 0;
			return;
		} else {
			this.droppedOut = false;
		}
		
		//Get Weight Matrix
		this.prevInput = input.getCopy();
		Matrix weights = inputWeights;
		
		//Update totalInput
		this.totalInput = weights.vMultiply(input).getTotal() + bias;
		
		//Add Activation Function and Update state
		this.state = af.get(totalInput);
	}
	public void setPrevInputs(double inputs[]){
		Matrix prev = new Matrix(inputs.length, 1);
		for(int i = 0; i < inputs.length; i++){
			prev.set(i, 0, inputs[i]);
		}
		this.prevInput = prev;
	}
	public void setTotalInput(double b){
		this.totalInput = b;
	}
	public Matrix propBack(double der) {
		
		if(droppedOut) {
			return new Matrix(this.accWeightDers.rowCount(), 1);
		}
		
		//Increment counter variable
		numAccDers++;
		
		//Propagate Backwards through the Activation Function
		double d = der * af.der(this.totalInput);
		this.totalInput = 0;
		//Add to accumulated input derivative -> This also counts as computing the bias derivative
		this.accInputDer += d;
		
		
		//Compute weight derivatives -> der * sourceState
		//Only do above if this node has input nodes
			
		Matrix wDers = new Matrix(this.accWeightDers.rowCount(), 1);
			
		for(int i = 0; i < wDers.rowCount(); i++) {
			//System.out.println("prevInput: " + this.prevInput.get(i, 0) + " * " + d);
			wDers.set(i, 0, this.prevInput.get(i, 0) * d);
		}
		this.prevInput.wipe();
		this.accWeightDers.add(wDers);
		
		//Compute source partial derivatives -> partial because this is only 1 node in a layer
		//This is der * weights
		Matrix weights = inputWeights;
		Matrix sourcePartialDers = weights.vMultiplyAll(d);
		
		return sourcePartialDers;
	}
	public double getBias(){
		return this.bias;
	}
	public void updateParams(double lr) {
		
		if(numAccDers <= 0) {
			return;
		}
		
		//Update Weights with accumulated weight derivatives
		m = m.vMultiplyAll(beta_1).vAdd(accWeightDers.vMultiplyAll(1f - beta_1));
		Matrix g2 = accWeightDers.getCopy();
		g2.updateAll((t) -> Math.pow(t, 2));
		v = v.vMultiplyAll(beta_2).vAdd(g2.vMultiplyAll(1f - beta_2));
		Matrix m_hat = m.vDivideAll(1f - Math.pow(beta_1, iteration)); // Corrected versions of v and m
		Matrix v_hat = v.vDivideAll(1f - Math.pow(beta_2, iteration)); // ^
		//inputWeights.subtract(accWeightDers.vMultiplyAll(lr / numAccDers));//
		v_hat.updateAll(Math::sqrt);
		v_hat = v_hat.vAddAll(nonZero);
		Matrix toSub = m_hat.vDivide(v_hat).vMultiplyAll(lr / numAccDers); // updated with alr
		inputWeights.subtract(toSub);
		iteration++;
		//Update bias with accumulated bias derivatives
		bias -= accInputDer * lr / numAccDers;
		//System.out.println("Updating with " + accWeightDers.get(0, 0));

		//Further update weights based on regularization
		if(regularizationL2){

			double a = lr * regularizationRate;
			Matrix m = this.inputWeights.getCopy();
			m.updateAll(n -> Regularization.L2.der(n) * a);

			inputWeights.subtract(m);
		}

		//Cleanup
		accInputDer = 0;
		accWeightDers.wipe();
		numAccDers = 0;
		this.state = 0;
	}
}
