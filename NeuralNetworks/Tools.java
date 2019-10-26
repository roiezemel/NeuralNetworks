package NeuralNetworks;

import java.util.Arrays;

public class Tools {

    public static double[] valueOf(double[] array){
        double[] item = new double[array.length];
        for(int i = 0; i<array.length; i++){
            item[i] = array[i];
        }
        return item;
    }

    public static int[] valueOf(int[] array){
        int[] item = new int[array.length];
        for(int i = 0; i<array.length; i++){
            item[i] = array[i];
        }
        return item;
    }

    public static double[][] valueOf(double[][] array){
        double[][] item = new double[array.length][];

        for(int i = 0; i < array.length; i++){
            item[i] = new double[array[i].length];
            for(int j = 0; j<array[i].length; j++){
                item[i][j] = array[i][j];
            }
        }

        return item;
    }

    public static int[][] valueOf(int[][] array){
        int[][] item = new int[array.length][];

        for(int i = 0; i < array.length; i++){
            item[i] = new int[array[i].length];
            for(int j = 0; j<array[i].length; j++){
                item[i][j] = array[i][j];
            }
        }

        return item;
    }

    public static double[][][] valueOf(double[][][] array){

        double[][][] item = new double[array.length][][];

        for(int i = 0; i<array.length; i++){

            item[i] = new double[array[i].length][];

            for(int j = 0; j<array[i].length; j++){

                item[i][j] = new double[array[i][j].length];

                for(int d = 0; d<array[i][j].length; d++){
                    item[i][j][d] = array[i][j][d];
                }

            }

        }

        return item;

    }

    public static int[][][] valueOf(int[][][] array){

        int[][][] item = new int[array.length][][];

        for(int i = 0; i<array.length; i++){

            item[i] = new int[array[i].length][];

            for(int j = 0; j<array[i].length; j++){

                item[i][j] = new int[array[i][j].length];

                for(int d = 0; d<array[i][j].length; d++){
                    item[i][j][d] = array[i][j][d];
                }

            }

        }

        return item;

    }

    public static int[] cast(double[] array){
        int[] item = new int[array.length];
        for(int i = 0; i<array.length; i++){
            item[i] = (int)array[i];
        }

        return item;
    }

    public static double[] cast(int[] array){
        double[] item = new double[array.length];
        for(int i = 0; i<array.length; i++){
            item[i] = array[i];
        }

        return item;
    }

    public static int[][] cast(double[][] array){

        int[][] item = new int[array.length][];

        for(int i = 0; i<array.length; i++){
            item[i] = cast(array[i]);
        }

        return item;

    }

    public static double[][] cast(int[][] array) {

        double[][] item = new double[array.length][];

        for (int i = 0; i < array.length; i++) {
            item[i] = cast(array[i]);
        }

        return item;

    }

    public static int[][][] cast(double[][][] array){

        int[][][] item = new int[array.length][][];

        for(int i = 0; i<array.length; i++){
            item[i] = cast(array[i]);
        }

        return item;

    }

    public static double[][][] cast(int[][][] array){

        double[][][] item = new double[array.length][][];

        for(int i = 0; i<array.length; i++){
            item[i] = cast(array[i]);
        }

        return item;

    }

    public static double max(double[] array){
        double[] item = valueOf(array);
        for(int i = 1; i<item.length; i++){
            if(item[i] > item[0]){
                item[0] = item[i];
            }
        }

        return item[0];
    }

    public static double min(double[] array){
        double[] item = valueOf(array);
        for(int i = 1; i<item.length; i++){
            if(item[i] < item[0]){
                item[0] = item[i];
            }
        }

        return item[0];
    }

    public static double max(double[][] array){
        double[][] item = valueOf(array);
        for(int i = 1; i<item.length; i++){
            if(max(item[i]) > max(item[0])){
                item[0] = item[i];
            }
        }

        return max(item[0]);
    }

    public static double min(double[][] array){
        double[][] item = valueOf(array);
        for(int i = 1; i<item.length; i++){
            if(max(item[i]) < max(item[0])){
                item[0] = item[i];
            }
        }

        return max(item[0]);
    }

    public static double max(double[][][] array){
        double[][][] item = valueOf(array);
        for(int i = 1; i<item.length; i++){
            if(max(item[i]) > max(item[0])){
                item[0] = item[i];
            }
        }

        return max(item[0]);
    }

    public static double min(double[][][] array){
        double[][][] item = valueOf(array);
        for(int i = 1; i<item.length; i++){
            if(max(item[i]) < max(item[0])){
                item[0] = item[i];
            }
        }

        return max(item[0]);
    }

    public static int max_index(double[] array){
        double[] item = valueOf(array);
        int index = 0;
        for(int i = 1; i<item.length; i++){
            if(item[i] > item[0]){
                item[0] = item[i];
                index = i;
            }
        }

        return index;
    }

    public static int min_index(double[] array){
        double[] item = valueOf(array);
        int index = 0;
        for(int i = 1; i<item.length; i++){
            if(item[i] < item[0]){
                item[0] = item[i];
                index = i;
            }
        }

        return index;
    }
}
