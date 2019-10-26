package Visualization;

import NeuralNetworks.DataSet;
import NeuralNetworks.Network;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

public class AreaDetector extends JPanel implements MouseListener, KeyListener {

    LinkedList<int[]> points = new LinkedList<>(); // 0 => x, 1 => y, 2 => 0 = blue, 1 = red
    boolean train = false;

    int width = 400;
    int height = 400;

    Network net;
    DataSet dataset;

    int i = width;
    int j = height;

    public static void main(String[] args){
        new AreaDetector();
    }

    public AreaDetector(){

        this.addMouseListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);


        JFrame frame = new JFrame("Area Detector");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(this);

        net = new Network(2, 100, 70, 2);
    }

    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D)g;

        if(!train) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        for(int i = 0; i<points.size(); i++){

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.BLACK);
            g2d.fillOval(points.get(i)[0] - 8, points.get(i)[1] - 8, 16, 16);

            if(points.get(i)[2] == 0)
                g2d.setColor(Color.BLUE);

            else
                g2d.setColor(Color.RED);

            g2d.fillOval(points.get(i)[0] - 5, points.get(i)[1] - 5, 10, 10);

        }

        if(train){
                net.think(i/(width*1d), j/(height*1d));
                g2d.setColor(new Color((int)(net.getOutputLayer()[1]*255), 0, (int)(net.getOutputLayer()[0]*255)));
                g2d.fillRect(i, j, 1, 1);

                j--;

                if(j == -1) {
                    j = height;
                    i--;
                }

                if(i == -1){
                    net.train(dataset, 1000, 1, 0.3);
                    i = width;
                    j = height;
                }

                repaint();
            }


        }

    private void wait(int milliSeconds){
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

        if(!train){
            if(SwingUtilities.isRightMouseButton(e)){
                points.add(new int[] {e.getX(), e.getY(), 1}); // red
            }

            if(SwingUtilities.isLeftMouseButton(e)){
                points.add(new int[] {e.getX(), e.getY(), 0}); // blue
            }
            repaint();
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            train = true;

            dataset = new DataSet();

            for(int i = 0; i<points.size(); i++){
                dataset.addData(new double[] {points.get(i)[0]/(width*1d), points.get(i)[1]/(height*1d)}, new double[] {1 - points.get(i)[2], points.get(i)[2]});
            }

            System.out.println("Started...");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    net.train(dataset, 1000, 1, 0.3);
                    repaint();
                }
            }).start();
        }

        if(e.getKeyCode() == KeyEvent.VK_R){
            for(int i = 0; i<20; i++){

                int x = (int)(Math.random() * width);
                int y = (int)(Math.random() * height);
                int color = (Math.random() > 0.5)?0:1;
                points.add(new int[] {x, y, color});

            }
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
