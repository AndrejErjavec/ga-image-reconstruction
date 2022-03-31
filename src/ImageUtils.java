import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public float ImageMSE(BufferedImage src_image, BufferedImage target_image) {
        if (src_image.getHeight() != target_image.getHeight() | src_image.getWidth() != target_image.getWidth()) {
            throw new Error("Images should be equal in size");
        }
        float diff = 0.0f;
        for (int i = 0; i < src_image.getHeight(); i++) {
            for (int j = 0; j < src_image.getWidth(); j++) {
                Color srcPixelColor = new Color(src_image.getRGB(i, j));
                int srcPixelR = srcPixelColor.getRed();
                int srcPixelG = srcPixelColor.getGreen();
                int srcPixelB = srcPixelColor.getBlue();

                Color targetPixelColor = new Color(src_image.getRGB(i, j));
                int targetPixelR = targetPixelColor.getRed();
                int targetPixelG = targetPixelColor.getGreen();
                int targetPixelB = targetPixelColor.getBlue();

                double diffR_sq = Math.pow((srcPixelR - targetPixelR), 2d);
                double diffG_sq = Math.pow((srcPixelG - targetPixelG), 2d);
                double diffB_sq = Math.pow((srcPixelB - targetPixelB), 2d);

                diff += (diffR_sq + diffG_sq + diffB_sq);
                // System.out.println(diff);
            }
        }
        return diff / (3 * src_image.getHeight()* src_image.getWidth());
    }
}
