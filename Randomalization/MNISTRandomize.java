package Randomalization;

import MNIST.MNIST;
import NeuralNetworks.DataSet;
import NeuralNetworks.Network;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class MNISTRandomize extends JPanel{

    int numberBatch = 40;

    LinkedList<double[][]> pixels;
    int[] labels = MNIST.labels;
    LinkedList<double[][]> inputs;
    LinkedList<double[][]> targets;

    int currentImage = 0;

    Network net = new Network(28 * 28, 28 * 28);

    boolean test = false;

    public MNISTRandomize(){

        JFrame frame = new JFrame();
        frame.setSize(800, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(this);

        pixels = (LinkedList<double[][]>) MNIST.getPixels();
        inputs = getData();
        targets = getData();
        pixels = (LinkedList<double[][]>) MNIST.getPixels();

        test(9);

    }

    public void display(double[][] image){

        for(int i = 0; i<28; i++){
            for(int j = 0; j<28; j++){

                if(net.round(image[i][j] / 255d) != 0)
                    System.out.print(net.round(image[i][j] / 255d) + " ");

                else
                    System.out.print("  ");

            }
            System.out.println();
        }

    }

    @Override
    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        if(test){

            int[][] pxs = reshape(net.getOutputLayer());

            for(int i = 0; i<28; i++){
                for(int j = 0; j<28; j++){

                    g2d.setColor(new Color((int)pixels.get(currentImage)[i][j], (int)pixels.get(currentImage)[i][j], (int)pixels.get(currentImage)[i][j]));
                    g2d.fillRect(j * 10 + 50, i * 10 + 20, 10, 10);

//                    g2d.setColor(new Color((int)targets.get(currentImage)[i][j], (int)targets.get(currentImage)[i][j], (int)targets.get(currentImage)[i][j]));
//                    g2d.fillRect(j * 10 + 400, i * 10 + 20, 10, 10);

                    g2d.setColor(new Color(pxs[i][j], pxs[i][j], pxs[i][j]));
                    g2d.fillRect(j * 10 + 400, i * 10 + 20, 10, 10);
                }
            }

        }

        if(currentImage == 1 && !test){
            g2d.fillOval(0, 0, 100, 100);
        }

    }

    public LinkedList<double[][]> getData(){

        LinkedList<double[][]> item = new LinkedList<>();

        for(int i = 0; i<=9; i++){
            for(int j = 0; j<numberBatch; j++){
                for(int d = 0; d<pixels.size(); d++){
                    if(labels[d] == i){
                        item.add(pixels.get(d));
                        pixels.remove(d);
                        labels = remove(labels, d);
                        break;
                    }
                }
            }
        }

        return item;

    }

    public static int[] remove(int[] array, int index){

        int[] item = new int[array.length-1];

        for(int i = 0; i<array.length; i++){

            if(i > index)
                item[i-1] = array[i];

            else if(i != index)
                item[i] = array[i];
        }

        return item;

    }

    public double[] reshape(double[][] image){
            double[] reshaped = new double[28*28];
            for(int j = 0; j<28*28; j++){
                reshaped[j] = image[j/28][j - (j/28)*28]/255d;
            }
            return reshaped;
    }

    public int[][] reshape(double[] image){
        int[][] reshaped = new int[28][28];

        for(int i = 0; i<28; i++){
            for(int j = 0; j<28; j++){
                reshaped[i][j] =  (int)(image[i * 28 + j]*255);
            }
        }

        return reshaped;
    }

    public void wait(int milliSeconds){
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void train(int number){

        DataSet dataSet = new DataSet();

        for(int i = 0; i<inputs.size(); i++){
            dataSet.addData(reshape(inputs.get(i)), reshape(targets.get(i)));
        }

        for(int i = 0; i<200; i++){
            net.train(dataSet, 1, 50, 50, 0.3);
            System.out.println(i + 1);
        }

        net.save("res/random" + number + ".txt");

        System.out.println("Done!");

        currentImage = 1;

        repaint();

    }

    public void test(int number){

        test = true;

        net.load("res/random" + number + ".txt");

        for(int i = 0; i<pixels.size(); i++){

            currentImage = i;
            net.think(reshape(pixels.get(i)));
            repaint();
            wait(500);

        }

        test = false;

    }

    public static void main(String[] args){

        new MNISTRandomize();

    }

}
