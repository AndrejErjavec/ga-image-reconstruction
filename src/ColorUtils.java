import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorUtils {

    public int colorDiff(Pixel a, int pixel_size, BufferedImage target_image) {
        int pos_x = a.pos_x;
        int pos_y = a.pos_y;
        Color targetPixelColor = averageColor(pos_x, pos_y, pixel_size, pixel_size, target_image); // new Color(target_image.getRGB(pos_x, pos_y));
        int targetPixelR = targetPixelColor.getRed();
        int targetPixelG = targetPixelColor.getGreen();
        int targetPixelB = targetPixelColor.getBlue();
        int diffR = Math.abs(a.color.getRed() - targetPixelR);
        int diffG = Math.abs(a.color.getGreen() - targetPixelG);
        int diffB = Math.abs(a.color.getBlue() - targetPixelB);
        return diffR + diffG + diffB;
    }

    public Color averageColor(int startX, int startY, int width, int height, BufferedImage image) {
        int pixels = width * height;
        int sumR = 0, sumG = 0, sumB = 0;
        for (int i = startY; i < height; i++) {
            for (int j = startY; j < width; j++) {
                Color c = new Color(image.getRGB(i, j));
                sumR += c.getRed();
                sumG += c.getGreen();
                sumB = c.getBlue();
            }
        }
        return new Color(sumR/pixels, sumG/pixels, sumB/pixels);
    }
}
