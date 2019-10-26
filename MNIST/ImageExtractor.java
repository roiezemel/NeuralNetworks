package MNIST;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.sql.SQLOutput;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.LinkedList;

public class ImageExtractor {

    int[][] grid;
    LinkedList<int[]> mappedBlackPixels; // index2 => 0 = x, 1 = y
    public static LinkedList<double[][]> test = new LinkedList<>();
    public static BufferedImage image;

    public LinkedList<double[]> extractImages(int[][] grid){

        test = new LinkedList<>();

        this.grid = grid;

        this.grid = new int[grid.length][grid[0].length];

        for(int i = 0; i<grid.length; i++){
            for(int j = 0; j<grid[0].length; j++){
                this.grid[i][j] = grid[i][j];
            }
        }

        LinkedList<double[]> images = new LinkedList<>();

        for(int i = 0; i<this.grid.length; i++){
            for(int j = 0; j<this.grid[i].length; j++){
                if(this.grid[i][j] == 0){
                    images.add(getImage(i, j));
                }
            }
        }

        return images;

    }

     // Reshape
    private double[] getImage(int x, int y){

        mappedBlackPixels = new LinkedList<>();
        mapBlackPixels(x, y);
        double[][] image = createImage();
        double[] reshaped = new double[28*28];
        for(int j = 0; j<28*28; j++){
            reshaped[j] = image[j/28][j - (j/28)*28]/255d;
        }

        return reshaped;
    }

    private void mapBlackPixels(int x, int y){

        if(isPixelInMap(x, y)) return;

        mappedBlackPixels.add(new int[] {x, y});
        grid[x][y] = 255;

        for(int i = x - 1; i<=x + 1; i++){
            for(int j = y - 1; j<=y + 1; j++){
                if(!(i < 0) && !(j < 0) && !(i >= grid.length) && !(j >= grid[0].length)){
                    if(grid[i][j] == 0){
                        mapBlackPixels(i, j);
                    }
                }
            }
        }

    }

    private int findSmallestCoordinate(int index){ // index => 0 = x, 1 = y

        int smallest = 1000000000;
        for(int i = 0; i<mappedBlackPixels.size(); i++){
            if(mappedBlackPixels.get(i)[index] < smallest)
                smallest = mappedBlackPixels.get(i)[index];
        }

        return smallest;

    }

    private int findBiggestCoordinate(int index){ // index => 0 = x, 1 = y
        int biggest = -1000000000;
        for(int i = 0; i<mappedBlackPixels.size(); i++){
            if(mappedBlackPixels.get(i)[index] > biggest)
                biggest = mappedBlackPixels.get(i)[index];
        }

        return biggest;
    }

    private int getPixelColor(int x, int y){
        if(isPixelInMap(x, y)) return 0;
        return 255;
    }

    private boolean isPixelInMap(int x, int y){
        for(int i = 0; i<mappedBlackPixels.size(); i++){
            if(mappedBlackPixels.get(i)[0] == x && mappedBlackPixels.get(i)[1] == y){
                return true;
            }
        }

        return false;
    }

    private double[][] createImage(){

        int startX = findSmallestCoordinate(0);
        int startY = findSmallestCoordinate(1);

        int endX = findBiggestCoordinate(0);
        int endY = findBiggestCoordinate(1);

        BufferedImage before = new BufferedImage(endX - startX + 1, endY - startY + 1, BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i<before.getWidth(); i++){
            for(int j = 0; j<before.getHeight(); j++){
                before.setRGB(i, j, toRGB(getPixelColor(i + startX, j + startY)));
            }
        }

        if(before.getHeight() > before.getWidth()*2){
            int width = before.getHeight() + before.getHeight()%2;
            BufferedImage newImage = new BufferedImage(width + before.getWidth(), before.getHeight(), BufferedImage.TYPE_INT_RGB);
            for(int i = 0; i<width + before.getWidth(); i++){
                for(int j = 0; j<before.getHeight(); j++){

                    if(i <width/2 || i >= width/2 + before.getWidth()) {
                        newImage.setRGB(i, j, toRGB(255));
                    }

                    else
                        newImage.setRGB(i, j, before.getRGB(i - width/2, j));

                }
            }
            before = newImage;
        }

       // image = before;

        BufferedImage after = resize(before, 36, 36);

        double[][] image = new double[56][56];

        for(int j = 0; j<56; j++){
            for(int i = 0; i<56; i++){

                boolean margins = false;
                for(int d = 0; d<10; d++){
                    if(i == d || j == d || i == 55 - d || j == 55 - d)
                        margins = true;
                }

                if(margins)
                    image[i][j] = 0;

                else
                    image[i][j] = (255 - new Color(after.getRGB(i - 10, j - 10)).getRed());
            }
        }

        test.add(resize(image));
        return resize(image);
    }

    private int toRGB(int value){
        int rgb = value;
        rgb = (rgb << 8) + value;
        rgb = (rgb << 8) + value;
        return rgb;
    }

    private BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    private double[][] resize(double[][] image){
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

                resized[j][i] = (sum * 1.0) / (width * height);

            }
        }

        return resized;
    }

}
