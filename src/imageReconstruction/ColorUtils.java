package imageReconstruction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorUtils {

    public float colorDiff(PixelFragment a, int pixel_size, BufferedImage target_image) {
        int pos_x = a.pos_x;
        int pos_y = a.pos_y;
        Color targetPixelColor = new Color(target_image.getRGB(pos_x, pos_y));//averageColor(pos_x, pos_y, pixel_size, pixel_size, pixel_size, target_image);
        int targetPixelR = targetPixelColor.getRed();
        int targetPixelG = targetPixelColor.getGreen();
        int targetPixelB = targetPixelColor.getBlue();
        int diffR = Math.abs(a.color.getRed() - targetPixelR);
        int diffG = Math.abs(a.color.getGreen() - targetPixelG);
        int diffB = Math.abs(a.color.getBlue() - targetPixelB);
        return (float)(diffR + diffG + diffB) / 3;
    }

    public int isRightColor(PixelFragment a, BufferedImage target_image) {
        int pos_x = a.pos_x;
        int pos_y = a.pos_y;
        Color targetPixelColor = new Color(target_image.getRGB(pos_x, pos_y));

        if (a.color.equals(targetPixelColor)) {
            return 1;
        }
        return 0;
    }

    public Color averageColor(int startX, int startY, int width, int height, int pixel_size, BufferedImage image) {
        int pixels = width * height;
        int sumR = 0, sumG = 0, sumB = 0;
        for (int i = startY; i < startY + height - pixel_size; i++) {
            for (int j = startX; j < startX + width - pixel_size; j++) {
                Color c = new Color(image.getRGB(i, j));
                sumR += c.getRed();
                sumG += c.getGreen();
                sumB += c.getBlue();
            }
        }
        return new Color(sumR/pixels, sumG/pixels, sumB/pixels);
    }
}
