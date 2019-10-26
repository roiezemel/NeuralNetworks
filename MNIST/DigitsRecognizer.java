package MNIST;

import NeuralNetworks.Network;
import NeuralNetworks.NetworkListener;
import NeuralNetworks.NetworkVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class DigitsRecognizer extends JPanel implements MouseListener, MouseMotionListener, KeyListener, NetworkListener {

    int gridWidth = 300;
    int gridHeight = 100;
    Rectangle[][] grid;
    int[][] gridImage;
    JFrame frame;
    LinkedList<Integer> labels = new LinkedList<>();
    Network net = new Network();
    Font font = new Font("David", Font.BOLD, 200);
    Font font2 = new Font("Miriam", Font.BOLD, 50);
    boolean calculating = false;
    boolean mouseReleased = false;
    NetworkVisualizer networkVisualizer;

    public DigitsRecognizer(){

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);

        frame = new JFrame("Digits Recognizer");
        frame.setSize(1200, 700);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);

        grid = new Rectangle[gridWidth][gridHeight];
        gridImage = new int[gridWidth][gridHeight];

        initGrid((getWidth() - 1200)/2, 20, 1200, 400);

        initGrid((getWidth() - 1200)/2, 20, 1200, 400);

        initGrid((getWidth() - 1200)/2, 20, 1200, 400);

        net.load("res/test28.txt");

        net.addListener(this);

        networkVisualizer = new NetworkVisualizer(900, 660, 200, 40 , net, 20, 30, 1);
        networkVisualizer.addLabels("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

        repaint();

    }

    @Override
    public void paint(Graphics g){

        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if(networkVisualizer != null) {
            networkVisualizer.paint(g2d);
        }

        if(!isMaximumSizeSet()) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        for(int i = 0; i<gridWidth; i++){
            for(int j = 0; j<gridHeight; j++){
                g2d.setColor(new Color(gridImage[i][j], gridImage[i][j], gridImage[i][j]));
                g2d.fill(grid[i][j]);

            }
        }

        g2d.setColor(Color.BLACK);
        g2d.drawRect((getWidth() - 1200)/2, 20, 1200, 400);

        g2d.setFont(font);

        for(int i = 0; i<labels.size(); i++){
            g2d.drawString(labels.get(i) + "", i * 80, 700);
        }

        for(int i = 0; i<ImageExtractor.test.size(); i++){

            for(int x = 0; x<28; x++){
                for(int y = 0; y<28; y++){
                    g2d.setColor(new Color((int)(ImageExtractor.test.get(i)[y][x]), (int)(ImageExtractor.test.get(i)[y][x]), (int)(ImageExtractor.test.get(i)[y][x])));
                    g2d.fillRect(x*3 + i*3*25 + 10, y*3 + 800, 3, 3);
                }
            }

        }

        if(ImageExtractor.image != null){
            g2d.setColor(Color.RED);
            g2d.drawImage(ImageExtractor.image, 800, 700, this);
            g2d.drawRect(800, 700, ImageExtractor.image.getWidth(), ImageExtractor.image.getHeight());
        }

        if(calculating){
            g2d.setFont(font2);
            g2d.setColor(Color.RED);
            g2d.drawString("Processing...", 300, 500);
        }

    }

    private void wait(int milliSeconds){
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initGrid(int x, int y, double width, double height){
        if(grid == null) return;
            int squareWidth = (int)(width / gridWidth);
            int squareHeight = (int)(height / gridHeight);
            for (int i = 0; i < gridWidth; i++) {
                for (int j = 0; j < gridHeight; j++) {
                    grid[i][j] = new Rectangle(i * squareWidth + x, j * squareHeight + y, squareWidth, squareHeight);
                    gridImage[i][j] = 255;
                }
            }
    }

    private void clear(){
        for(int i = 0; i<gridWidth; i++){
            for(int j = 0; j<gridHeight; j++){
                gridImage[i][j] = 255;
            }
        }
    }

    public static void main(String[] args){
        new DigitsRecognizer();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        System.out.print("");
        if(e.getKeyCode() == KeyEvent.VK_C){
            clear();
            repaint();
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

            if(SwingUtilities.isRightMouseButton(e)){
            clear();
            repaint();
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseReleased = true;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for(int i = 0; i<gridWidth; i++){
            for(int j = 0; j<gridHeight; j++){
                if(grid[i][j].intersects(new Rectangle(e.getX(), e.getY(), 1, 1))){
                    try {
                        for(int d = i - 3; d<i + 2; d++){
                            for(int v = j - 2; v<j + 3; v++){
                                gridImage[d][v] = 0;
                                repaint();
                            }
                        }
                    } catch(Exception ex){}
                }
            }
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        if(mouseReleased && !(new Rectangle((getWidth() - 1200)/2, 20, 1200, 400).intersects(new Rectangle(e.getX(), e.getY(), 1, 1)))){
            mouseReleased = false;
            while(calculating){
                System.out.print("");
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    calculating = true;
                    repaint();
                    ImageExtractor im = new ImageExtractor();
                    labels = new LinkedList<>();

                    for (double[] image : im.extractImages(gridImage)) {
                        net.think(image);
                        labels.add(net.getHighestNumberIndex());
                    }

                    calculating = false;

                    repaint();

                }
            }).start();
        }

    }

    @Override
    public void onOutputReceived(double[] output) {
       // wait(50 );
        repaint();
    }
}
