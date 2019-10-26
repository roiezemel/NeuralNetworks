package MNIST;

import NeuralNetworks.DataSet;
import NeuralNetworks.Network;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class MnistOCR extends JPanel implements MouseListener, MouseMotionListener, KeyListener {


    int gridWidth = 196;
    int gridHeight = 196;
    Rectangle[][] grid = new Rectangle[gridWidth][gridHeight];
    int[][] currentImage;
    int currentLabel;
    Font font = new Font("David", Font.BOLD, 400);

    Network net = new Network(784, 170, 85, 10);


    boolean test = false;

    public MnistOCR(){

        net.load("res/test28.txt");

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);

        this.setFocusable(true);

        JFrame frame = new JFrame("Trainer");
        frame.setSize(1000, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);


        int size = 2;
        for(int i = 0; i<gridWidth; i++){
            for(int j = 0; j<gridHeight; j++){
                grid[i][j] = new Rectangle(size*i + 70, size*j + 70, size, size);
            }
        }

        clear();


    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        for (int i = 0; i < currentImage.length; i++) {
            for (int j = 0; j < currentImage[i].length; j++) {
                g2d.setColor(new Color((int) currentImage[j][i], (int) currentImage[j][i], (int) currentImage[j][i]));
                if(currentImage.length == 28){
                    for(int d = 0; d<gridWidth / 28; d++){
                        for(int v = 0; v<gridHeight / 28; v++){
                            g2d.fill(grid[d + (gridWidth / 28*i)][v + (gridHeight / 28*j)]);
                        }
                    }
                }
                else
                    g2d.fill(grid[i][j]);
            }
        }

        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        g2d.drawString(currentLabel + "", 700, 380);
    }

    public double[] reshapeAndSquash(int[][] image){
        double[][] resized = resize(image);
        double[] reshaped = new double[28*28];
        for(int j = 0; j<28*28; j++){
                reshaped[j] = resized[j/28][j - (j/28)*28]/255d;
        }
        return reshaped;
    }

    public double[][] resize(int[][] image){
        double[][] resized = new double[28][28];
        int width = image.length / 28;
        int height = image[0].length / 28;

        for(int i = 0; i<28; i++){
            for(int j = 0; j<28; j++){
                double sum = 0;
                for(int d = 0; d<width; d++){
                    for(int v = 0; v<height; v++){
                        sum += image[d + (width*i)][v + (height * j)];
                    }
                }

                resized[i][j] = (sum * 1.0) / (width * height);

            }
        }

        return resized;
    }

    public void wait(int milliSeconds){
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        if (currentImage == null || currentImage.length != gridWidth) {
            currentImage = new int[gridWidth][gridHeight];
        }
        for(int i = 0; i<gridWidth; i++){
            for(int j = 0; j<gridHeight; j++){
                currentImage[i][j] = 0;
            }
        }
    }

    public void test(int batchSize, boolean train){

      //  test = true;
        ArrayList<int[][]> pixels = (ArrayList<int[][]>) MNIST.pixels;
        DataSet dataSet = new DataSet();

        System.out.println("Preparing dataset...");
        for(int i = 0; i<pixels.size(); i++){
            dataSet.addData(reshapeAndSquash(pixels.get(i)), MNIST.labels[i], true);
        }

        if(!train) {
                int corrects = 0;
                for (int i = 0; i < batchSize; i++) {
                    currentImage = pixels.get(i);
                    net.think(reshapeAndSquash(currentImage));
                    currentLabel = net.getHighestNumberIndex();

                    if (net.getHighestNumberIndex() == MNIST.labels[i]) {
                        corrects++;
                    }
                    repaint();
                    wait(0);
                }
                System.out.println(corrects * 100d / batchSize + "% correct!");
        }
        else{
            System.out.println("Start training...");

            for(int i = 0; i<1000; i++){
                net.train(dataSet, 1, 500, 200, 0.3);
                System.out.println(i + 1);
            }

            System.out.println("Result: ");
            int index = (int)(Math.random() * (pixels.size() - 1));
            net.think(reshapeAndSquash(pixels.get(index)));

            System.out.println("Label: " + MNIST.labels[index]);
            System.out.println("Network's thought: " + net.getHighestNumberIndex());
            System.out.println(net.getOutput());
            net.save("res/test30.txt");

            test(10000, false);
        }

      //  test = false;
    }

    public void displayResults(){
        net.think(reshapeAndSquash(currentImage));
        currentLabel = net.getHighestNumberIndex();
//        double[][] resized = resize(currentImage);
//        int[][] retyped = new int[28][28];
//        for(int i = 0; i<28; i++){
//            for(int j = 0; j<28; j++){
//                retyped[i][j] = (int)resized[i][j];
//            }
//        }
//
//        currentImage = retyped;
        repaint();
    }

    public static void main(String[] args){
        new MnistOCR();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_T){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test(60000, false);
                }
            }).start();
        }

        if(e.getKeyCode() == KeyEvent.VK_L){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test(10000, true);
                }
            }).start();
        }

        if(e.getKeyCode() == KeyEvent.VK_C){
            clear();
            repaint();
        }

        if(e.getKeyCode() == KeyEvent.VK_SPACE){
           displayResults();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(int i = 0; i<gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                if (grid[i][j].intersects(new Rectangle(e.getX(), e.getY(), 1, 1))) {
                    if(currentImage.length != gridWidth)
                        clear();
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        displayResults();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for(int i = 0; i<gridWidth; i++) {
            for(int j = 0; j<gridHeight; j++) {
                if(grid[i][j].intersects(new Rectangle(e.getX(), e.getY(), 1, 1))) {
                    for(int d = i-6; d<i+6; d++) {
                        for(int c = j-6; c<j+6; c++) {
                            try {
                                currentImage[c][d] = 255;
                            }catch(Exception E) {}
                        }
                    }
                    currentImage[j][i] = 255;
                }
            }
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
