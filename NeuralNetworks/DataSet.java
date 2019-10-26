package NeuralNetworks;

import java.util.Arrays;
import java.util.LinkedList;

public class DataSet {

    private LinkedList<double[]> inputs = new LinkedList<>();
    private LinkedList<double[]> targets = new LinkedList<>();
    private LinkedList<Integer> labels = new LinkedList<>();

    public void addData(double[] input, int label){
        inputs.add(input);
        labels.add(label);
    }

    public void addData(double[] input, double[] target){
        inputs.add(input);
        targets.add(target);
    }

    public void addData(double[] input, int label, boolean createTargetArray){
        inputs.add(input);
        labels.add(label);
        if(createTargetArray){
            targets.add(getTargetsArray(label));
        }
    }

    public double[] getTargetsArray(int label){
        double[] targets = new double[10];
        for(int i = 0; i<10; i++){
            targets[i] = 0;
            if(i == label)
                targets[i] = 1;
        }

        return targets;
    }

    public double[][][] createBatch(int batchSize){
        double[][][] batch = new double[batchSize][2][]; // index3 => 0 = inputs, 1 = targets
        for(int i = 0; i<batchSize; i++){
            int index = (int)(Math.random() * (inputs.size()-1));
            batch[i][0] = inputs.get(index);
            batch[i][1] = targets.get(index);
        }
        return batch;
    }

    public double[][][] getWholeSet(){
        double[][][] batch = new double[inputs.size()][2][]; // index3 => 0 = inputs, 1 = targets
        for(int i = 0; i<inputs.size(); i++){
            batch[i][0] = inputs.get(i);
            batch[i][1] = targets.get(i);
        }
        return batch;
    }

    public LinkedList<double[]> getInputs() {
        return inputs;
    }

    public void setInputs(LinkedList<double[]> inputs) {
        this.inputs = inputs;
    }

    public LinkedList<Integer> getLabels() {
        return labels;
    }

    public void setLabels(LinkedList<Integer> labels) {
        this.labels = labels;
    }

    public double[] normalizeData(double[] data){
        double[] normalized = new double[data.length];
        for(int i = 0; i<data.length; i++){
            normalized[i] = (data[i] - min(data)) / (max(data) - min(data));
        }
        return normalized;
    }

    private double max(double[] data){
        if(data == null) return 0;

        double max = data[0];

        for(int i = 1; i<data.length; i++){
            if(data[i] > max)
                max = data[i];
        }

        return max;
    }

    private double min(double[] data){
        if(data == null) return 0;

        double min = data[0];

        for(int i = 1; i<data.length; i++){
            if(data[i] < min)
                min = data[i];
        }

        return min;
    }
}
