package MNIST;
import java.util.LinkedList;
import java.util.List;

public class MNIST {

	public static List<int[][]> pixels = MnistReader.getImages("res\\train-images-idx3-ubyte\\train-images.idx3-ubyte");
	public static int[] labels = MnistReader.getLabels("res\\train-labels-idx1-ubyte\\train-labels.idx1-ubyte");
	
	public static List<double[][]> getPixels(){
		List<double[][]> item = new LinkedList<double[][]>(); 
		for(int i = 0; i<pixels.size(); i++) {
			item.add(new double[pixels.get(i).length][pixels.get(i)[0].length]);
			for(int j = 0; j<pixels.get(i).length; j++) {
				for(int d = 0; d<pixels.get(i)[j].length; d++) {
					item.get(i)[j][d] = pixels.get(i)[j][d];  
				}
			}
		}
		return item;
	}
}