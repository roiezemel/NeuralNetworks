package NeuralNetworks;

import java.io.*;

public class SaveAndLoad implements Serializable {


    public int[] network_layers_sizes;
    public double[][][] weights;
    public double[][] biases;

    public void save(String path, double[][] biases, double[][][] weights, int[] network_layers_sizes){
        this.biases = biases;
        this.weights = weights;
        this.network_layers_sizes = network_layers_sizes;

        File file = new File(path);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(this);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SaveAndLoad load(String path){
        File file = new File(path);
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            SaveAndLoad saveAndLoad = (SaveAndLoad) in.readObject();
            in.close();
            return saveAndLoad;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
