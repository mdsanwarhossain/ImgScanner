package Features;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SobelEdgeDetect {

    public static void main(String args[]) throws IOException {

        File f = new File("D:\\1.jpg");
        BufferedImage img = ImageIO.read(f);

        int x = img.getWidth();
        int y = img.getHeight();
        BufferedImage bufferedImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);

        int[][] edgeColors = new int[x][y];
        int MaxGradient = -1;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {

                int v00 = getGrayScale(img.getRGB(i - 1, j - 1));
                int v01 = getGrayScale(img.getRGB(i - 1, j));
                int v02 = getGrayScale(img.getRGB(i - 1, j + 1));

                int v10 = getGrayScale(img.getRGB(i, j - 1));
                int v11 = getGrayScale(img.getRGB(i, j));
                int v12 = getGrayScale(img.getRGB(i, j + 1));

                int v20 = getGrayScale(img.getRGB(i + 1, j - 1));
                int v21 = getGrayScale(img.getRGB(i + 1, j));
                int v22 = getGrayScale(img.getRGB(i + 1, j + 1));

                int gx = ((-1 * v00) + (0 * v01) + (1 * v02))
                        + ((-2 * v10) + (0 * v11) + (2 * v12))
                        + ((-1 * v20) + (0 * v21) + (1 * v22));

                int gy = ((-1 * v00) + (-2 * v01) + (-1 * v02))
                        + ((0 * v10) + (0 * v11) + (0 * v12))
                        + ((1 * v20) + (2 * v21) + (1 * v22));

                double GradientValue = Math.sqrt((gx * gx) + (gy * gy));
                int g = (int) GradientValue;

                if (MaxGradient < g) {
                    MaxGradient = g;
                }

                edgeColors[i][j] = g;
            }
        }

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {
                int edgeColor = edgeColors[i][j];
                if (edgeColor < 200) {
                    edgeColor = 0xff000000;
                } else {
                    edgeColor = 0xffffffff;
                }

                bufferedImage.setRGB(i, j, edgeColor);
            }
        }

        File outputfile = new File("D:\\sobeledge.png");
        ImageIO.write(bufferedImage, "png", outputfile);

        System.out.println("Successfully Detect the Edge on location " + outputfile.getPath());
        System.out.println("Noted that I provided all necessary images in image folder. If you want to run this code successfully, then set the input 1.jpg and output sobeledge.png image path in D disk");

    }

    public static int getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        int Gray = (r + g + b) / 3;

        return Gray;
    }
}
