package me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Layers.Convolution;

import me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Resources.Matrix;

import java.util.ArrayList;

public class Filter {

    public ArrayList<Matrix> getFilter() {
        return filter;
    }

    private ArrayList<Matrix> filter = new ArrayList<>();

    private ArrayList<Matrix> accDer = new ArrayList<>();



    private int numAccDers = 0;

    private boolean usingGPU = false;

    public Filter(int size, int depth, boolean gpu){

        this.usingGPU = gpu;

        //Initialize filter and accDer
        for(int d = 0; d < depth; d++){
            Matrix m = new Matrix(size, size);
            m.updateAll(t -> (double)(Math.random() * 0.5f));
            filter.add(m);
            accDer.add(new Matrix(size,size));
        }

        //
    }

}
