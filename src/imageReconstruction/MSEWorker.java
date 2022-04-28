package imageReconstruction;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;

public class MSEWorker extends Thread {
    int id;
    int start;
    int end;
    BufferedImage srcImage;
    BufferedImage targetImage;
    AtomicLong result;
    boolean last;

    public MSEWorker(int id, int start, int end, BufferedImage srcImage, BufferedImage targetImage, AtomicLong result, boolean last) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.srcImage = srcImage;
        this.targetImage = targetImage;
        this.result = result;
        this.last = last;
    }

    @Override
    public void run() {
        calculateImageMSE(id, start, end, srcImage, targetImage, result, last);
    }

    public synchronized void calculateImageMSE(int id, int start, int end, BufferedImage srcImage, BufferedImage targetImage, AtomicLong result, boolean last) {
        if (srcImage.getHeight() != targetImage.getHeight() | srcImage.getWidth() != targetImage.getWidth()) {
            throw new Error("Images should be equal in size");
        }
        long chunkTmp = 0;
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
                chunkTmp += r;
            }
        }
        result.getAndAdd(chunkTmp);
        if (this.last) {
            result.getAndUpdate(devide);
        }
    }

    private LongUnaryOperator devide = a -> a / (3 * srcImage.getWidth() * srcImage.getHeight());

}
