package me.gravitinos.minigame.gamecore.util.NeuralNetwork.Layers;

import java.util.ArrayList;

import me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources.Matrix;

public abstract class Layer {
	private double learningrate = 0.03f;
	public abstract ArrayList<Matrix> feedForward(ArrayList<Matrix> input);
	public abstract ArrayList<Matrix> propBack(ArrayList<Matrix> inputDer);
	public abstract void updateParams();
	public abstract ArrayList<Matrix> getDimensions();
	public abstract void setup(Layer prevLayer);
	public void setLearningRate(double lr) {
		this.learningrate = lr;
	}
	public double getLearningRate() {
		return this.learningrate;
	}
	public abstract int getNumLearnableParams();
}
