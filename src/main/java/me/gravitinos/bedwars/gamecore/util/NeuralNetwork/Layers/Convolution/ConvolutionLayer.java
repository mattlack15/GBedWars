package me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Layers.Convolution;

import me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Layers.Layer;
import me.gravitinos.bedwars.gamecore.util.NeuralNetwork.Resources.Matrix;

import java.util.ArrayList;

public class ConvolutionLayer extends Layer {

    private boolean usingGPU = false;
    private Matrix.Shape inputShape = null;
    private Matrix.Shape outputShape = null;

    private int stride = 1;
    private int filtersize = 3;

    //Padding / Shape optimization
    private int padding = 0;

    ArrayList<Filter> filters = new ArrayList<>();

    public static ArrayList<Matrix> cpuPropback(ArrayList<Matrix> input, ArrayList<Filter> filters, int stride, Matrix.Shape shape){
        //Propagate Backwards to prev layer
        ArrayList<Matrix> output = new ArrayList<>();
        ArrayList<ArrayList<Matrix>> filterMatrices = new ArrayList<>();
        for(Filter f : filters){
            filterMatrices.add(f.getFilter());
        }

        for(int depth = 0; depth < input.size(); depth++){
            Matrix partial = new Matrix(shape);
            ArrayList<Matrix> currentFilter = filterMatrices.get(depth);
            for(int filterDepth = 0; filterDepth < currentFilter.size(); filterDepth++){
                partial.add(cpuBasicConvolute(input.get(depth), stride, currentFilter.get(filterDepth)));
            }
            output.add(partial);
        }



        return output;
    }

    public static Matrix cpuBasicConvolute(Matrix input, int stride, Matrix filter){
        Matrix.Shape outputshape = new Matrix.Shape((input.getShape().getRows()-filter.getShape()
                .getRows()) / stride + 1, (input.getShape().getCols()-filter.getShape()
                .getCols()) / stride + 1);
        Matrix oMatrix = new Matrix(outputshape);
        for(int orows = 0; orows < oMatrix.getShape().getRows(); orows++){
            for(int ocols = 0; ocols < oMatrix.getShape().getCols(); ocols++){
                int irow = orows * stride;
                int icol = ocols * stride;
                double total = 0;
                for(int frow = 0; frow < filter.getShape().getRows(); frow++){
                    for(int fcol = 0; fcol < filter.getShape().getCols(); fcol++){
                        total+=input.get(irow+frow, icol+fcol) * filter.get(frow, fcol);
                    }
                }
                oMatrix.set(orows, ocols, total);
            }
        }
        return oMatrix;
    }

    public static ArrayList<Matrix> cpuConvolute(ArrayList<Matrix> input, int stride, ArrayList<Filter> filters){
        ArrayList<Matrix> output = new ArrayList<>();

        Matrix.Shape outputshape = new Matrix.Shape((input.get(0).getShape().getRows()-filters.get(0).getFilter().get(0).getShape()
        .getRows()) / stride + 1, (input.get(0).getShape().getCols()-filters.get(0).getFilter().get(0).getShape()
                .getCols()) / stride + 1); //Hopefully math is right

        for(Filter filter : filters){
            if(input.size() != filter.getFilter().size()){
                System.out.println("Size mismatch between filter and input!\n");
                input.get(input.size());
            }
            Matrix oMatrix = new Matrix(outputshape.getRows(), outputshape.getCols());
            for(int depth = 0; depth < filter.getFilter().size(); depth++){
                Matrix filterlayer = filter.getFilter().get(depth);
                Matrix toConvolute = input.get(depth);
                Matrix depthSum = new Matrix(outputshape.getRows(), outputshape.getCols());
                for(int orows = 0; orows < oMatrix.getShape().getRows(); orows++){
                    for(int ocols = 0; ocols < oMatrix.getShape().getCols(); ocols++){
                        int irow = orows * stride;
                        int icol = ocols * stride;
                        double total = 0;
                        for(int frow = 0; frow < filterlayer.getShape().getRows(); frow++){
                            for(int fcol = 0; fcol < filterlayer.getShape().getCols(); fcol++){
                                total+=toConvolute.get(irow+frow, icol+fcol) * filterlayer.get(frow, fcol);
                            }
                        }
                        depthSum.set(orows, ocols, total);
                    }
                }
                oMatrix.add(depthSum);
            }
            output.add(oMatrix);
        }
        return output;
    }

    public ConvolutionLayer(Matrix.Shape inputShape, int inputDepth, int numFilters, int filtersize, int stride, boolean usingGPU){
        this.usingGPU = usingGPU;
        this.inputShape = inputShape;
        this.stride = stride;
        this.filtersize = filtersize;

        if(this.stride == 1){
            this.padding = (filtersize-1)/2; // Determine padding
        }
        //Output size -> ( ( ( shape + padding * 2 ) - filter size ) / stride ) + 1
        //Optimize it for bigger than 1 stride later

        Matrix.Shape outputShape = new Matrix.Shape(
                (this.inputShape.getRows() + (this.padding*2) - filtersize) / this.stride + 1,
                (this.inputShape.getCols() + (this.padding*2) - filtersize) / this.stride + 1);
        this.outputShape = outputShape;

        for(int i = 0; i < numFilters; i++){
            filters.add(new Filter(filtersize, inputDepth, usingGPU));
        }



    }

    @Override
    public ArrayList<Matrix> feedForward(ArrayList<Matrix> input) {

        //TODO process

        return ConvolutionLayer.cpuConvolute(input, this.stride, this.filters);
    }

    @Override
    public ArrayList<Matrix> propBack(ArrayList<Matrix> inputDer) {
        return null;
    }

    @Override
    public void updateParams() {

    }

    @Override
    public ArrayList<Matrix> getDimensions() {
        return null;
    }

    public ArrayList<Filter> getFilters(){
        return this.filters;
    }
    @Override
    public void setup(Layer prevLayer) {

    }

    @Override
    public int getNumLearnableParams() {
        return 0;
    }
}
