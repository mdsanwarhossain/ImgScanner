package LineDetection;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.File;

public class HoughTransform {

    public static void main(String[] args) throws Exception {
        String filename = "D:\\sobeledge1.png";
        BufferedImage image = javax.imageio.ImageIO.read(new File(filename));
        HoughTransform h = new HoughTransform(image.getWidth(), image.getHeight());
        h.addPoints(image);
        Vector<HoughLine> lines = h.getLines(30);
        for (int j = 0; j < lines.size(); j++) {
            HoughLine line = lines.elementAt(j);
            line.draw(image, Color.RED.getRGB());
        }
        File outputfile = new File("D:\\StraightLine.png");
        javax.imageio.ImageIO.write(image, "png", outputfile);
        System.out.println("line detect successfully on location " + outputfile.getPath());
        System.out.println("Noted that I provided all necessary images in image folder. If you want to run this code successfully, then set the input sobeledge1.png and output StraightLine.png image path in D disk");
    }
    final int neighbourhoodSize = 4;
    final int maxTheta = 180;
    final double thetaStep = Math.PI / maxTheta;
    int width, height, houghHeight, doubleHeight, numPoints;
    int[][] houghArray;
    protected float centerX, centerY;
    double[] sinCache;
    double[] cosCache;

    public HoughTransform(int width, int height) {
        this.width =width;
        this.height = height;
        initialise();
    }

    public final void initialise() {
        houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;
        doubleHeight = 2 * houghHeight;
        houghArray = new int[maxTheta][doubleHeight];
        centerX = width / 2;
        centerY = height / 2;
        numPoints = 0;
        sinCache = new double[maxTheta];
        cosCache = sinCache.clone();
        for (int t = 0; t < maxTheta; t++) {
            double realTheta = t * thetaStep;
            sinCache[t] = Math.sin(realTheta);
            cosCache[t] = Math.cos(realTheta);
        }
    }

    public void addPoints(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if ((image.getRGB(x, y) & 0x000000ff) != 0) {
                    addPoint(x, y);
                }

            }
        }
    }

    public void addPoint(int x, int y) {
        for (int t = 0; t < maxTheta; t++) {
            int r = (int) (((x - centerX) * cosCache[t]) + ((y - centerY) * sinCache[t]));
            r += houghHeight;
            if (r < 0 || r >= doubleHeight) {
                continue;
            }
            houghArray[t][r]++;
        }
        numPoints++;
    }

    public Vector<HoughLine> getLines(int threshold) {
        Vector<HoughLine> lines = new Vector<>(10);
        if (numPoints == 0) {
            return lines;
        }
        for (int t = 0; t < maxTheta; t++) {
            loop:
            for (int r = neighbourhoodSize; r < doubleHeight - neighbourhoodSize; r++) {
                if (houghArray[t][r] > threshold) {
                    int peak = houghArray[t][r];
                    for (int dx = -neighbourhoodSize; dx <= neighbourhoodSize; dx++) {
                        for (int dy = -neighbourhoodSize; dy <= neighbourhoodSize; dy++) {
                            int dt = t + dx;
                            int dr = r + dy;
                            if (dt < 0) {
                                dt = dt + maxTheta;
                            } else if (dt >= maxTheta) {
                                dt = dt - maxTheta;
                            }
                            if (houghArray[dt][dr] > peak) {
                                continue loop;
                            }
                        }
                    }
                    double theta = t * thetaStep;

                    lines.add(new HoughLine(theta, r));
                }
            }
        }
        return lines;
    }

    public int getHighestValue() {
        int max = 0;
        for (int t = 0; t < maxTheta; t++) {
            for (int r = 0; r < doubleHeight; r++) {
                if (houghArray[t][r] > max) {
                    max = houghArray[t][r];
                }
            }
        }
        return max;
    }

    public BufferedImage getHoughArrayImage() {
        int max = getHighestValue();
        BufferedImage image = new BufferedImage(maxTheta, doubleHeight, BufferedImage.TYPE_INT_ARGB);
        for (int t = 0; t < maxTheta; t++) {
            for (int r = 0; r < doubleHeight; r++) {
                double value = 255 * ((double) houghArray[t][r]) / max;
                int v = 255 - (int) value;
                int c = new Color(v, v, v).getRGB();
                image.setRGB(t, r, c);
            }
        }
        return image;
    }
}
