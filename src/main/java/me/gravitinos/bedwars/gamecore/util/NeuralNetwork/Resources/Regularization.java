package me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Resources;

public interface Regularization {
    public double get(double x);
    public double der(double x);

    Regularization L2 = new REG_L2();

    class REG_L2 implements Regularization{

        @Override
        public double get(double x) {
            return 0.5 * x * x;
        }

        @Override
        public double der(double x) {
            return x;
        }
    }
}
