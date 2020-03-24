package me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Resources;

public interface LossFunction {
	public double getLoss(Matrix x, Matrix target);
	public Matrix getDers(Matrix x, Matrix target);
	
	//Static Finals
	
	public static final MeanSquared MeanSquared = new MeanSquared();
	
	//Nested Classes
	
	public class MeanSquared implements LossFunction{

		@Override
		public double getLoss(Matrix x, Matrix target) {
			Matrix m = x.vSubtract(target);
			m.updateAll((z) -> (double)Math.pow(z, 2));
			return (1f/(x.rowCount())) * m.getTotal();
		}

		@Override
		public Matrix getDers(Matrix x, Matrix target) {
			return x.vSubtract(target);
		}
		
	}
}
