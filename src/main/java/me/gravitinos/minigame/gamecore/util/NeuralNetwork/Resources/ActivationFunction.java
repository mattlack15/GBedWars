package me.gravitinos.minigame.gamecore.util.NeuralNetwork.Resources;

public interface ActivationFunction {
	public double get(double x);
	public double der(double x);
	
	//Static Finals
	public static final ActivationFunction Sigmoid = new Sigmoid();
	public static final ActivationFunction ReLU = new ReLU();
	public static final ActivationFunction Linear = new Linear();
	
	
	//Nested Classes
	class Sigmoid implements ActivationFunction{

		@Override
		public double get(double x) {
			double out = (1.0d / (1.0d + Math.exp(-x)));
			if(Math.abs(x) > 10000000){
				System.out.println("Approaching extremely large number " + x);
			}
			return out;
		}

		@Override
		public double der(double x) {
			if(Math.abs(x) > 10000000){
				System.out.println("(D) Approaching extremely large number " + x);
			}
			double y = this.get(x);
			return (y * (1.0d - y));
		}
		
	}
	class ReLU implements ActivationFunction{

		@Override
		public double get(double x) {
			double out = Math.max(0, x);
			return out;
		}

		@Override
		public double der(double x) {
			if(Math.abs(x) > 10000000){
				System.out.println("(D) Approaching extremely large number " + x);
			}
			double out = 1;
			if(x <= 0){
				return 0;
			}
			return out;
		}
		
	}
	class Linear implements ActivationFunction{

		@Override
		public double get(double x) {
			return x; 
		}

		@Override
		public double der(double x) {
			return 1;
		}
		
	}
}
