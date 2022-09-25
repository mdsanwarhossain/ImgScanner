package Features;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Rotate90deg {

    public static BufferedImage rotate(BufferedImage img) {

        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2 = newImage.createGraphics();
        g2.rotate(Math.toRadians(90), width / 2, height / 2);
        g2.drawImage(img, null, 0, 0);
        return newImage;
    }

    public static void main(String[] args) {
        try {
            BufferedImage OrgImg = ImageIO.read(new File("D:\\1.jpg"));
            BufferedImage RotImg = rotate(OrgImg);
            File outputfile = new File("D:\\rotate90.jpeg");
            ImageIO.write(RotImg, "jpg", outputfile);
            System.out.println("Image rotated successfully on location " + outputfile.getPath());
            System.out.println("Noted that I provided all necessary images in image folder. If you want to run this code successfully, then set the input 1.jpg and output rotate.jpeg image path in D disk");

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
