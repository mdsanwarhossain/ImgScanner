
package imagescanner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageScanner {
    
    public static double [] transformImage(double u1, double v1, double u2, double v2, double u3, double v3, double u4, double v4, int width, int height) {
        double x1 = 0;
        double x2 = width;
        double x3 = width;
        double x4 = 0;
        double y1 = 0;
        double y2 = 0;
        double y3 = height;
        double y4 = height;
        
        double [][]A= {{x1, y1, 1, 0,  0,  0, -u1*x1, -u1*y1},
        {0,  0,  0, x1, y1, 1, -v1*x1, -v1*y1},
        {x2, y2, 1, 0,  0,  0, -u2*x2, -u2*y2},
        {0,  0,  0, x2, y2, 1, -v2*x2, -v2*y2},
        {x3, y3, 1, 0,  0,  0, -u3*x3, -u3*y3},
        {0,  0,  0, x3, y3, 1, -v3*x3, -v3*y3},
        {x4, y4, 1, 0,  0,  0, -u4*x4, -u4*y4},
        {0,  0,  0, x4, y4, 1, -v4*x4, -v4*y4}};
        
        double [] b = {u1, v1, u2, v2, u3, v3, u4, v4};
     
        return GaussElimination.solveValue(A, b);
    }
    
    public static void writeImage (BufferedImage input, BufferedImage output, double [] mat) {
        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                double denominator = mat[6]*x + mat[7]*y + 1;
                int u = (int) ((mat[0]*x + mat[1]*y + mat[2]) / denominator);
                int v = (int) ((mat[3]*x + mat[4]*y + mat[5]) / denominator);
                int p = input.getRGB(u, v);
                output.setRGB(x, y, p);
            }
        }
    }

    public static void main(String[] args) {
        BufferedImage img = null;
        File f = null;

        //read image
        try {
            f = new File("D:\\6.jpeg");
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }

        //get height and width of image
        int height = img.getHeight();
        int width = img.getWidth();
        int h = 500;
        int w= 1000;
        
        double[] mat = transformImage(255,134,919,147,975,553,190,539,w, h);
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        writeImage (img, output, mat);
        
        try {
            f = new File("D:\\ScannedImage.jpg");
            ImageIO.write(output, "jpg", f);
            System.out.println("Successfully converted as Scanned Image on location "+f.getPath());
            System.out.println("Noted that I provided all necessary images in image folder. If you want to run this code successfully, then set the input 6.jpeg and output Scanned.jpg image path in D disk");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
}
