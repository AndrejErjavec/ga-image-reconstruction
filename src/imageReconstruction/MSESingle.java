package imageReconstruction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MSESingle {
    public float ImageMSE(BufferedImage srcImage, BufferedImage targetImage) {
        if (srcImage.getHeight() != targetImage.getHeight() | srcImage.getWidth() != targetImage.getWidth()) {
            throw new Error("Images should be equal in size");
        }
        float diff = 0;
        for (int i = 0; i < srcImage.getHeight(); i++) {
            for (int j = 0; j < srcImage.getWidth(); j++) {
                Color srcPixelColor = new Color(srcImage.getRGB(i, j));
                int srcPixelR = srcPixelColor.getRed();
                int srcPixelG = srcPixelColor.getGreen();
                int srcPixelB = srcPixelColor.getBlue();
                /*if ((i*j+j) < 10) {
                    System.out.println("RGB src: " + srcPixelR + ", " + srcPixelG + ", " + srcPixelB);
                }*/

                Color targetPixelColor = new Color(targetImage.getRGB(i, j));
                int targetPixelR = targetPixelColor.getRed();
                int targetPixelG = targetPixelColor.getGreen();
                int targetPixelB = targetPixelColor.getBlue();
                /*if (i == 0 && j < 10) {
                    System.out.println("RGB target: " + targetPixelR + ", " + targetPixelG + ", " + targetPixelB);
                }*/

                int diffR = 255 - Math.abs(srcPixelR - targetPixelR);
                int diffG = 255 - Math.abs(srcPixelG - targetPixelG);
                int diffB = 255 - Math.abs(srcPixelB - targetPixelB);

                double diffR_sq = Math.pow(diffR, 2d);
                double diffG_sq = Math.pow(diffG, 2d);
                double diffB_sq = Math.pow(diffB, 2d);

                diff += (diffR_sq + diffG_sq + diffB_sq);
            }
        }
        return diff / (3L * srcImage.getHeight() * srcImage.getWidth());
    }
}
