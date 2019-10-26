package NeuralNetworks;

import java.awt.*;

public class NetworkVisualizer implements NetworkListener{

    // Global attributes:
    int x;
    int y;
    int width;
    int height;
    Network net;

    // Local attributes:
    double padding = 10;
    double weightsStride = 1.5;
    int neuronsWidth;
    int neuronsHeight;
    double[][][] neuronsPositions; // index1 => indexI, index2 => indexJ, index3 => 0 = x, 1 = y
    boolean update = false;
    int sizeThreshold;
    String[] labels;


    public NetworkVisualizer(int x, int y, int width, int height, Network net, int sizeThreshold, double padding, double weightsStride){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.net = net;
        this.neuronsPositions = new double[net.getNETWORK_SIZE()][][];
        this.net.addListener(this);
        this.sizeThreshold = sizeThreshold;
        this.padding = padding;
        this.weightsStride = weightsStride;

        for(int i = 0; i<net.getNETWORK_SIZE(); i++){
            neuronsPositions[i] = new double[net.getNETWORK_LAYERS_SIZES()[i]][2];
        }
    }

    public void addLabels(String... labels){
        this.labels = labels;
    }

    public void update(){
        neuronsWidth = width / (net.getNETWORK_SIZE()*2);
        neuronsHeight = neuronsWidth;//(int)(height*1.0 / (Tools.max(Tools.cast(net.getNETWORK_LAYERS_SIZES()))*padding));
    }

    public void paint(Graphics2D g2d){
        if(update) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int xGap = neuronsWidth;
            int yGap = (int)(neuronsHeight/padding); //(int) ((height - Tools.max(Tools.cast(net.getNETWORK_LAYERS_SIZES())) * neuronsHeight)
                   // / (Tools.max(Tools.cast(net.getNETWORK_LAYERS_SIZES())) - 1));

            int x = this.x;
            int y = this.y;

            for (int i = 0; i < net.getNETWORK_SIZE(); i++) {

                int layerSize = 0;

                if(net.getNETWORK_LAYERS_SIZES()[i] >= sizeThreshold) {
                    y += (height - (sizeThreshold * neuronsHeight) -
                            ((sizeThreshold - 1) * yGap)) / 2;
                    layerSize = sizeThreshold;
                }
                else {
                    y += (height - (net.getNETWORK_LAYERS_SIZES()[i] * neuronsHeight) -
                            ((net.getNETWORK_LAYERS_SIZES()[i] - 1) * yGap)) / 2;
                    layerSize = net.getNETWORK_LAYERS_SIZES()[i];
                }

                for (int j = 0; j < layerSize; j++) {
                    if(!(j >= sizeThreshold)) {

                        neuronsPositions[i][j][0] = x + neuronsWidth / 2;
                        neuronsPositions[i][j][1] = y + neuronsHeight / 2;
                        y += yGap + neuronsHeight;
                    }
                }
                x += xGap + neuronsWidth;
                y = this.y;
            }

            //(net.getWeights()[i][j][d] + Math.abs(Tools.min(net.getWeights()[i]))) * weightsStride)
            for (int i = 1; i < net.getNETWORK_SIZE(); i++) {
                for (int j = 0; j < net.getNETWORK_LAYERS_SIZES()[i]; j++) {
                    for (int d = 0; d < net.getNETWORK_LAYERS_SIZES()[i - 1]; d++) {
                        if(!(d >= sizeThreshold) && !(j >= sizeThreshold)) {
                            g2d.setColor(new Color((int) (net.sigmoid(net.getWeights()[i][j][d]) * 150), (int) (net.sigmoid(net.getWeights()[i][j][d]) * 150), (int) (net.sigmoid(net.getWeights()[i][j][d]) * 150)));
                            g2d.setStroke(new BasicStroke((float) (net.sigmoid(net.getWeights()[i][j][d]) * weightsStride)));
                            g2d.drawLine((int) neuronsPositions[i][j][0], (int) neuronsPositions[i][j][1], (int) neuronsPositions[i - 1][d][0], (int) neuronsPositions[i - 1][d][1]);
                        }
                    }
                }
            }

            for(int i = 0; i<neuronsPositions.length; i++){
                for(int j = 0; j<neuronsPositions[i].length; j++){
                    g2d.setColor(new Color((int) (net.getNetwork()[i][j] * 255), (int) (net.getNetwork()[i][j] * 255), (int) (net.getNetwork()[i][j] * 255)));
                    g2d.fillOval((int)neuronsPositions[i][j][0] - neuronsWidth/2, (int)neuronsPositions[i][j][1] - neuronsHeight/2, neuronsWidth, neuronsHeight);

                    g2d.setColor(Color.black);
                    g2d.drawOval((int)neuronsPositions[i][j][0] - neuronsWidth/2, (int)neuronsPositions[i][j][1] - neuronsHeight/2, neuronsWidth, neuronsHeight);
                }
            }

            if(labels != null) {
                for (int i = 0; i < net.getOUTPUT_SIZE(); i++) {
                    System.out.println(i);
                    g2d.drawString(labels[i], (int)neuronsPositions[net.getNETWORK_SIZE()-1][i][0] + neuronsWidth, (int)neuronsPositions[net.getNETWORK_SIZE()-1][i][1] + neuronsHeight/4);
                }
            }

            net.getHighestNumberIndex();

            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect((int)neuronsPositions[net.getNETWORK_SIZE()-1][net.getHighestNumberIndex()][0] - neuronsWidth/2, (int)neuronsPositions[net.getNETWORK_SIZE()-1][net.getHighestNumberIndex()][1] - neuronsHeight/2, (int)neuronsWidth + 25, (int)neuronsHeight);

            update = false;
        }
    }

    @Override
    public void onOutputReceived(double[] output) {
        update = true;
        update();
    }

    private void wait(int milliSeconds){
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Network reduceLayerSizes(Network net){
        Network item = new Network(Tools.valueOf(net.NETWORK_LAYERS_SIZES));
        item.setNetwork(Tools.valueOf(net.getNetwork()));
        item.setWeights(Tools.valueOf(net.getWeights()));
        item.setBiases(Tools.valueOf(net.getBiases()));
        return item;
    }

    public int[] getNetworkLayerSizes(){
        int[] item = new int[net.getNETWORK_LAYERS_SIZES().length];
        for(int i = 0; i<net.getNETWORK_LAYERS_SIZES().length; i++){
            for(int j = 0; j<((net.getNETWORK_LAYERS_SIZES()[i] >= sizeThreshold)?sizeThreshold:net.getNETWORK_LAYERS_SIZES()[i]); j++){
                item[i] = net.getNETWORK_LAYERS_SIZES()[i];
            }
        }
        return item;
    }

    private void drawRect(int x, int y, int width, int height, int milliSeconds, Graphics2D g2d){

        for(int i = x; i<x+width; i++){
            g2d.drawRect(i, y, 1, 1);
            wait(milliSeconds);
        }

        for(int i = y; i<y+height; i++){
            g2d.drawRect(x+width, i, 1, 1);
            wait(milliSeconds);
        }

        for(int i = x+width; i>x; i--){
            g2d.drawRect(i, y+height, 1, 1);
            wait(milliSeconds);
        }

        for(int i = y+height; i>y; i--){
            g2d.drawRect(x, i, 1, 1);
            wait(milliSeconds);
        }

    }
}
