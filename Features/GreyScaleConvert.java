package Features;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class GreyScaleConvert {

    public static void main(String[] args) throws IOException {
        BufferedImage img = null;
        File f = null;

        //read image
        try {
            f = new File("D:\\1.jpg");
            img = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println(e);
        }

        //get height and width of image
        int height = img.getHeight();
        int width = img.getWidth();

        //convert to greyscale
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = img.getRGB(x, y);
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;
                //calcuting average
                int avg = (r + g + b) / 3;
                p = (avg << 16) | (avg << 8) | avg;
                img.setRGB(x, y, p);

            }
        }
        try {
            f = new File("D:\\grayscale.jpg");
            ImageIO.write(img, "jpg", f);
            System.out.println("Successfully converted to greyscale on location " + f.getPath());
            System.out.println("Noted that I provided all necessary images in image folder. If you want to run this code successfully, then set the input 1.jpg and output grayscale.jpg image path in D disk");
        } catch (IOException e) {
            System.out.println(e);
        }

    }
}
