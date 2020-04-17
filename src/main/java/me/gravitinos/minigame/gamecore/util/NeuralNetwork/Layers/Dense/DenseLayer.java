package me.gravitinos.minigame.gamecore.util.NeuralNetwork.Layers.Dense;

import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Layers.Layer;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.ActivationFunction;
import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.Matrix;

import java.util.ArrayList;
import java.util.Random;

public class DenseLayer extends Layer {
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Node> sources = new ArrayList<>();
	private boolean inputLayer = true;
	private Matrix latestInput = new Matrix(1,1);

	private boolean regularizationL2;
	
	private int inputSize;
	
	private ActivationFunction activationFunction;
	//Constructors
	
	public DenseLayer(int nodes, int inputs, ActivationFunction acfunc, boolean regularizationL2, Random rand) {
		this.inputSize = inputs;
		this.regularizationL2 = regularizationL2;
		this.activationFunction = acfunc;
		for(int i = 0; i < nodes; i++) {
			Node node = new Node(inputs, acfunc, rand);
			node.setRegularizationL2(regularizationL2);
			this.nodes.add(node);
		}
	}
	
	//
	
	@Override
	public ArrayList<Matrix> feedForward(ArrayList<Matrix> input) {
		Matrix in = input.get(0);
		this.latestInput = in;

		Matrix out = new Matrix(this.nodes.size(), 1);
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).updateState(in);
			out.set(i, 0, nodes.get(i).getState());
		}
		ArrayList<Matrix> o = new ArrayList<>();
		o.add(out);
		return o;
	}
	public ArrayList<Node> getNodes(){
		return this.nodes;
	}
	public DenseLayer setDropOut(double prob) {
		
		for(Node nodes : this.nodes) {
			nodes.setDropOutProbability(prob);
		}
		
		return this;
	}
	@Override
	public ArrayList<Matrix> propBack(ArrayList<Matrix> inputDer) {
		
		Matrix der = inputDer.get(0);
		
		Matrix outDers = new Matrix((sources.size() != 0 ? sources.size() : 1), 1);
		
		for(int i = 0; i < der.rowCount(); i++) {
			double nodeDer = der.get(i, 0);
			Matrix newDer = this.nodes.get(i).propBack(nodeDer);
			if(!this.inputLayer) {
		//		System.out.println(newDer);
			}
			outDers.add(newDer);
		}
		if(!this.inputLayer) {
		//	System.out.println("Equals: " + outDers);
		}
		if(this.inputLayer) {
		//	System.out.println("Weights first nodes: ");
			for(Node nodes : this.nodes) {
			//	System.out.println("--");
			//	System.out.println(nodes.getWeights());
			}
		}
		ArrayList<Matrix> outDersContainer = new ArrayList<>();
		outDersContainer.add(outDers);
		return outDersContainer;
	}

	@Override
	public void updateParams() {
		//System.out.println("Updatein Layer -> Layer size: " + this.nodes.size());
		if(this.inputLayer) {
		//	System.out.println("updating input layer");
		}
		nodes.forEach(n -> n.updateParams(this.getLearningRate()));
	}
	
	@Override
	public void setup(Layer prevLayer) {
		if(!(prevLayer instanceof DenseLayer)) {
			return;
		}
		this.inputLayer = false;
		this.sources = ((DenseLayer)prevLayer).getNodes();
		for(Node nodes : this.nodes) {
			nodes.setup(this.sources.size(), this.activationFunction);
		}
	}

	@Override
	public int getNumLearnableParams() {
		return this.nodes.size() * this.getInputSize() + this.getInputSize();
	}

	public int getInputSize() {
		return inputSize;
	}



	@Override
	public ArrayList<Matrix> getDimensions() {
		ArrayList<Matrix> out = new ArrayList<>();
		out.add(new Matrix(this.inputSize, 1));
		return out;
	}

}
