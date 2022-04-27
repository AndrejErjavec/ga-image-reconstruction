import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

public class MSEWorker extends Thread {
    int id;
    int start;
    int end;
    BufferedImage srcImage;
    BufferedImage targetImage;
    float[] result;

    public MSEWorker(int id, int start, int end, BufferedImage srcImage, BufferedImage targetImage, float[] result) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.srcImage = srcImage;
        this.targetImage = targetImage;
        this.result = result;
    }

    @Override
    public void run() {
        calculateImageMSE(id, start, end, srcImage, targetImage, result);
    }

    public synchronized void calculateImageMSE(int id, int start, int end, BufferedImage srcImage, BufferedImage targetImage, float[] result) {
        if (srcImage.getHeight() != targetImage.getHeight() | srcImage.getWidth() != targetImage.getWidth()) {
            throw new Error("Images should be equal in size");
        }
        int tmp = 0;
        for (int i = start; i < end; i++) {
            for (int j = 0; j < srcImage.getWidth(); j++) {
                Color srcPixelColor = new Color(srcImage.getRGB(i, j));
                int srcPixelR = srcPixelColor.getRed();
                int srcPixelG = srcPixelColor.getGreen();
                int srcPixelB = srcPixelColor.getBlue();

                Color targetPixelColor = new Color(targetImage.getRGB(i, j));
                int targetPixelR = targetPixelColor.getRed();
                int targetPixelG = targetPixelColor.getGreen();
                int targetPixelB = targetPixelColor.getBlue();

                int diffR = 255 - Math.abs(srcPixelR - targetPixelR);
                int diffG = 255 - Math.abs(srcPixelG - targetPixelG);
                int diffB = 255 - Math.abs(srcPixelB - targetPixelB);

                int diffR_sq = (int) Math.pow(diffR, 2d);
                int diffG_sq = (int) Math.pow(diffG, 2d);
                int diffB_sq = (int) Math.pow(diffB, 2d);

                int r = (diffR_sq + diffG_sq + diffB_sq) ;
                tmp += r;
            }
        }
        result[id] = tmp / (float)(3 * srcImage.getHeight() * srcImage.getWidth());
    }

}
