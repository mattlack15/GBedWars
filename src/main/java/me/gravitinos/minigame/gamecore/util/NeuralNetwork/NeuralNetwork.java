package me.gravitinos.minigame.gamecore.util.NeuralNetwork;

import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.LossFunction;

import java.util.ArrayList;

import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Layers.Layer;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.Matrix;


public class NeuralNetwork {

	//Kernels
	private static String kernelDotProductUnderMax = "__kernel void feedForwardKernel(__global double *input, __global double *out, __global double *weights, __global double *bias, __global int *acFunc, __local double *temp, __global double *totalInputs){\n" +
			"\n" +
			"int gid = get_global_id(0);\n" +
			"int workgroup = get_group_id(0);\n" +
			"int localSize = get_local_size(0);\n" +
			"int lid = get_local_id(0);\n" +
			"\n" +
			"\n" +
			"\n" +
		//	"input[lid] = 1;\n" +
			"temp[lid] = weights[workgroup * localSize + lid] * input[lid];\n" + //workgroup * localSize + lid if not gid
			"\n" +
			"barrier(CLK_LOCAL_MEM_FENCE); // Wait for the other work-items to catch up\n" +
			"\n" +
			"if(lid == 0){ //If this is the first work-item in the work-group, add the values up\n" +
			"	double sum = 0;\n" +
			"  for(int i = 0; i < localSize; i++){\n" +
			"    sum = sum + temp[i];\n" +
			"  }\n" +
			"   sum += bias[workgroup];\n" +
			"   totalInputs[workgroup] = sum;\n" +
			"	if(sum < 0){\n" +
			"		sum = 0;\n" +
			"	}\n" +
			"	out[workgroup] = sum;\n" +
			"\n" +
			"}\n" +
			"\n" +
			"}";

	//TODO -> Remove this after you see it next time you decide to code -> Add in activation functions to kernels and add in dotproductOVERmax
	private static String kernelDotProductOverMax = "__kernel void feedForwardKernel(__global double *input, __global double *out, __global double *weights, __global double *bias,  __global int cols, __global double *totalInputs, __global int acFunc){\n" +
			"\n" +
			"int gid = get_global_id(0);\n" +
			"\n" +
			"double sum = 0;\n" +
			"for(int i = 0; i < cols; i++){\n" +
			"   sum += weights[gid * cols + i] * input[i];\n" +
			"}\n" +
			"if(sum < 0){\n" +
			"	sum = 0;\n" +
			"}\n" +
			"out[gid] = sum;\n" +
			"\n" +
			"}";

	private ArrayList<Layer> layers = new ArrayList<>();
	
	private LossFunction lossFunction = LossFunction.MeanSquared;

	public boolean isUseGPU() {
		return useGPU;
	}

	public void setUseGPU(boolean useGPU) {
		this.useGPU = useGPU;
	}

	private boolean useGPU = false;
	
	private double learningRate = 0.01f;



	public NeuralNetwork() {

	}
	
	public void addLayer(Layer l) {
		if(layers.size() != 0) {
			l.setup(layers.get(layers.size()-1));
		} else {
			System.out.println("Input layer added!");
		}
		this.layers.add(l);
	}
	
	public LossFunction getLossFunction() {
		return this.lossFunction;
	}
	
	public void setLossFunction(LossFunction f) {
		this.lossFunction = f;
	}


	public ArrayList<Matrix> propForward(ArrayList<Matrix> input) {
		
		//Error checking
		if(this.layers.size() == 0) {
			System.out.println("This network has no layers!\n");
			this.causeError();
		}
		
		if(input.size() == 0) {
			System.out.println("Input is empty!\n");
			this.causeError();
		}
		
		if(input.size() != this.layers.get(0).getDimensions().size() || !input.get(0).getShape().equals(this.layers.get(0).getDimensions().get(0).getShape())) {
			System.out.println("Input dimensions do not match network's input dimensions!\n");
			this.causeError();
		}
		
		//Propagate Forward


			ArrayList<Matrix> lastOutput = input;

			for (Layer layers : this.layers) {

				lastOutput = layers.feedForward(lastOutput);

			}
			return lastOutput;
	}

	public static Matrix normalize(Matrix m, double mean, double std){
		Matrix m1 = m.getCopy();
		m1.updateAll((d) -> (d-mean)/std);
		return m1;
	}

	public ArrayList<Layer> getLayers(){
		return this.layers;
	}
	public double train(ArrayList<ArrayList<Matrix>> input, ArrayList<Matrix> answer, int batchSize) {
		double loss = 0;
		for(int iter = 0; iter < batchSize; iter++) {
		
		Matrix guess = this.propForward(input.get(iter)).get(0);
		
		//System.out.println("Guess: " + guess.get(0, 0));
		//System.out.println("Answer: " + answer.get(0).get(0, 0));
		
		loss += this.lossFunction.getLoss(guess, answer.get(iter));
		
		Matrix der = this.lossFunction.getDers(guess, answer.get(iter));
		
		for(int i = this.layers.size()-1; i > -1; i--) {
			
			Layer currentLayer = this.layers.get(i);
			
			currentLayer.setLearningRate(this.learningRate);
			
			ArrayList<Matrix> derContainer = new ArrayList<>();
			derContainer.add(der);
			
			der = currentLayer.propBack(derContainer).get(0);
			
		}
		
		}
		this.layers.forEach(Layer::updateParams);
		return loss / batchSize;
		
	}
	
	public void setLearningRate(double lr) {
		this.learningRate = lr;
	}
	public double getLearningRate() {
		return this.learningRate;
	}

	//Weird Utility
	private void causeError() {
		ArrayList<Integer> a = new ArrayList<>();
		a.get(32);
	}

}
