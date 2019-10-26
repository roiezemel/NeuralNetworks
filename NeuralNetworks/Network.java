package NeuralNetworks;

import java.util.Arrays;
import java.util.LinkedList;

public class Network {

    public int[] NETWORK_LAYERS_SIZES;
    private int NETWORK_SIZE;
    private int INPUT_SIZE;
    private int OUTPUT_SIZE;
    private double WEIGHTS_LOWER_BOUND = -1;
    private double WEIGHTS_UPPER_BOUND = 1;

    private double[][] network;
    private double[][][] weights;
    private double[][] biases;

    private double[][] netDerivatives;
    private double[][] netErrors;

    LinkedList<NetworkListener> networkListeners = new LinkedList<>();

    public Network(){}

    public Network(int... network_layers_sizes) {
        NETWORK_LAYERS_SIZES = network_layers_sizes;
        INPUT_SIZE = NETWORK_LAYERS_SIZES[0];
        NETWORK_SIZE = NETWORK_LAYERS_SIZES.length;
        OUTPUT_SIZE = NETWORK_LAYERS_SIZES[NETWORK_SIZE - 1];

        weights = new double[NETWORK_SIZE][][];

        network = new double[NETWORK_SIZE][];
        biases = new double[NETWORK_SIZE][];
        netDerivatives = new double[NETWORK_SIZE][];
        netErrors = new double[NETWORK_SIZE][];

        for(int i = 0; i < NETWORK_SIZE; i++){
            network[i] = new double[NETWORK_LAYERS_SIZES[i]];
            netDerivatives[i] = new double[NETWORK_LAYERS_SIZES[i]];
            netErrors[i] = new double[NETWORK_LAYERS_SIZES[i]];

            if(i != 0)
                biases[i] = createRandomArray(NETWORK_LAYERS_SIZES[i], WEIGHTS_LOWER_BOUND, WEIGHTS_UPPER_BOUND);
        }

        for(int i = 1; i<NETWORK_SIZE; i++){
            weights[i] = new double[NETWORK_LAYERS_SIZES[i]][];
            for(int j = 0; j<NETWORK_LAYERS_SIZES[i]; j++){
                weights[i][j] = createRandomArray(NETWORK_LAYERS_SIZES[i-1], WEIGHTS_LOWER_BOUND, WEIGHTS_UPPER_BOUND);
            }
        }
    }

    public void addListener(NetworkListener networkListener){
        networkListeners.add(networkListener);
    }

    private double[] createRandomArray(int size, double lowerBound, double upperBound){
        double[] randomArray = new double[size];
        for(int i = 0; i<size; i++){
            randomArray[i] = Math.random() * (upperBound - lowerBound) + lowerBound;
        }
        return randomArray;
    }

    public void think(double... input){
        if(input.length != INPUT_SIZE){
            System.out.println("Input size doesn't match network size!");
            return;
        }

        network[0] = input;

        for(int i = 1; i<NETWORK_SIZE; i++){
            for(int j = 0; j<NETWORK_LAYERS_SIZES[i]; j++){

                double sum = biases[i][j];
                for(int p = 0; p<NETWORK_LAYERS_SIZES[i-1]; p++){
                    sum += network[i-1][p] * weights[i][j][p];
                }
                network[i][j] = sigmoid(sum);
                netDerivatives[i][j] = network[i][j] * (1 - network[i][j]);

            }

            for(NetworkListener net : networkListeners){
                net.onOutputReceived(getOutputLayer());
            }

        }

        for(NetworkListener net : networkListeners){
            net.onOutputReceived(getOutputLayer());
        }
    }

    public double sigmoid(double x){
        return 1d / (1 + Math.exp(-x));
    }

    public void train(double[] input, double[] targets, double learningRate){
        if(input.length != INPUT_SIZE || targets.length != OUTPUT_SIZE){
            System.out.println("Input or targets size doesn't match network size!");
            return;
        }
        think(input);
        backpropError(targets);
        updateWeights(learningRate);
    }

    public void train(DataSet dataset, int epochs, int loops, int batchSize, double learningRate){
        for(int i = 0; i<epochs; i++){
            double[][][] batch = dataset.createBatch(batchSize);
            for(int j = 0; j<loops; j++){
                for(int b = 0; b<batchSize; b++){
                    train(batch[b][0], batch[b][1], learningRate);
                }
            }
        }
    }

    public void train(DataSet dataset, int epochs, int loops, double learningRate){
        double[][][] batch = dataset.getWholeSet();
        for(int i = 0; i<epochs; i++){
            for(int j = 0; j<loops; j++){
                for(int b = 0; b<batch.length; b++){
                    train(batch[b][0], batch[b][1], learningRate);
                }
            }
        }
    }

    public void backpropError(double[] targets){
        if(targets.length != OUTPUT_SIZE){
            System.out.println("Targets size doesn't match output size!");
            return;
        }

        for(int i = 0; i<OUTPUT_SIZE; i++){
            netErrors[NETWORK_SIZE-1][i] = (network[NETWORK_SIZE-1][i] - targets[i])*netDerivatives[NETWORK_SIZE-1][i];
        }

        for(int i = NETWORK_SIZE-2; i > 0; i--){
            for(int j = 0; j<NETWORK_LAYERS_SIZES[i]; j++){
                double sum = 0;
                for(int n = 0; n<NETWORK_LAYERS_SIZES[i+1]; n++){
                    sum += netErrors[i+1][n] * weights[i+1][n][j];
                }
                netErrors[i][j] = sum * netDerivatives[i][j];
            }
        }
    }

    public void updateWeights(double learningRate){
        for(int i = 1; i<NETWORK_SIZE; i++){
            for(int j = 0; j<NETWORK_LAYERS_SIZES[i]; j++){
                double delta = -learningRate * netErrors[i][j];
                biases[i][j]  += delta;
                for(int p = 0; p<NETWORK_LAYERS_SIZES[i-1]; p++){
                    weights[i][j][p] += delta * network[i-1][p];
                }
            }
        }
    }

    public void save(String path){
        SaveAndLoad saveAndLoad = new SaveAndLoad();
        saveAndLoad.save(path, biases, weights, NETWORK_LAYERS_SIZES);
    }

    public void load(String path){
        SaveAndLoad saveAndLoad = new SaveAndLoad();
        saveAndLoad = saveAndLoad.load(path);
        this.biases = saveAndLoad.biases;
        this.weights = saveAndLoad.weights;
        this.NETWORK_LAYERS_SIZES = saveAndLoad.network_layers_sizes;

        INPUT_SIZE = NETWORK_LAYERS_SIZES[0];
        NETWORK_SIZE = NETWORK_LAYERS_SIZES.length;
        OUTPUT_SIZE = NETWORK_LAYERS_SIZES[NETWORK_SIZE - 1];

        network = new double[NETWORK_SIZE][];
        netDerivatives = new double[NETWORK_SIZE][];
        netErrors = new double[NETWORK_SIZE][];

        for(int i = 0; i < NETWORK_SIZE; i++){
            network[i] = new double[NETWORK_LAYERS_SIZES[i]];
            netDerivatives[i] = new double[NETWORK_LAYERS_SIZES[i]];
            netErrors[i] = new double[NETWORK_LAYERS_SIZES[i]];
        }
    }

    public void setNetwork(double[][] network) {
        this.network = network;
    }

    public void setWeights(double[][][] weights) {
        this.weights = weights;
    }

    public void setBiases(double[][] biases) {
        this.biases = biases;
    }

    public int getHighestNumberIndex(){
        double biggest = -1000000000;
        int index = -1;
        for(int i = 0; i<OUTPUT_SIZE; i++){
            if(network[NETWORK_SIZE -1 ][i] > biggest){
                biggest = network[NETWORK_SIZE -1 ][i];
                index = i;
            }
        }
        return index;
    }

    public String getOutput(){
        return Arrays.toString(network[NETWORK_SIZE-1]);
    }

    public double[] getOutputLayer(){
        return network[NETWORK_SIZE - 1];
    }

    public double[][] getNetwork(){
        return network;
    }

    public double[][][] getWeights(){
        return weights;
    }

    public double[][] getBiases(){
        return biases;
    }

    public int[] getNETWORK_LAYERS_SIZES() {
        return NETWORK_LAYERS_SIZES;
    }

    public int getNETWORK_SIZE() {
        return NETWORK_SIZE;
    }

    public int getINPUT_SIZE() {
        return INPUT_SIZE;
    }

    public int getOUTPUT_SIZE() {
        return OUTPUT_SIZE;
    }

    public double getWEIGHTS_LOWER_BOUND() {
        return WEIGHTS_LOWER_BOUND;
    }

    public double getWEIGHTS_UPPER_BOUND() {
        return WEIGHTS_UPPER_BOUND;
    }

    public static void main(String[] args){

//        DataSet dataset = new DataSet();
//
//        dataset.addData(new double[] {0.1, 0.5}, new double[] {0.3});
//        dataset.addData(new double[] {0.3, 0.7}, new double[] {0.5});
//        dataset.addData(new double[] {0.2, 0.6}, new double[] {0.4});
//        dataset.addData(new double[] {0.2, 0.2}, new double[] {0.2});
//
//        Network net = new Network(2, 4, 1);
//        net.train(dataset, 1000, 1000, 4, 0.3);
//
//        net.think(0.2, 0.4); // 0.3
//        System.out.println(net.round(net.network[2][0]));
//
//        net.think(0.1, 0.7); // 0.4
//        System.out.println(net.round(net.network[2][0]));
//
//        net.think(0.3, 0.9); // 0.6
//        System.out.println(net.round(net.network[2][0]));

        double[][] a = {
                {2, 8, 3},
                {5, 4, 1}
        };

        double[][] b = {
                {4, 1, 5},
                {6, 3, 5},
                {2, 4, 4}
        };

        double[][] p = dotProduct(a, b);

        for(int i = 0; i<p.length; i++){
            for(int j = 0; j<p[0].length; j++){
                System.out.print(p[i][j] + ", ");
            }
            System.out.println();
        }

    }

    public double round(double x){
        return ((int)(x * 10))/10d;
    }

    public static double[][] dotProduct(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }
}
