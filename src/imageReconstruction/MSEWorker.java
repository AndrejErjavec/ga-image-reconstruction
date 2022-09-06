package imageReconstruction;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;

public class MSEWorker extends Thread {
    int start;
    int end;
    BufferedImage srcImage;
    BufferedImage targetImage;
    ArrayList<Image> srcImages;
    AtomicReference<Float> result;

    /**
     * SUITABLE FOR CALCULATING MSE FOR SINGLE IMAGE AT A TIME
     * @param start - thread starting row on image
     * @param end - thread ending row on image
     * @param srcImage - Image object to calculate MSE for
     * @param targetImage - reference image
     * @param result - Atomic variable for adding chunk results to
     */
    public MSEWorker(int start, int end, BufferedImage srcImage, BufferedImage targetImage, AtomicReference<Float> result) {
        this.start = start;
        this.end = end;
        this.srcImage = srcImage;
        this.targetImage = targetImage;
        this.result = result;
    }

    /**
     * SUITABLE FOR CALCULATING MSE FOR MULTIPLE IMAGES (LIST OF IMAGES)
     * @param start - thread starting point in Image list
     * @param end - thread ending point in Image list
     * @param srcImages - list of Image objects to calculate MSE values for
     * @param targetImage - reference image
     */
    public MSEWorker(int start, int end, ArrayList<Image> srcImages, BufferedImage targetImage) {
        this.start = start;
        this.end = end;
        this.srcImages = srcImages;
        this.targetImage = targetImage;
    }

    @Override
    public void run() {
        calculateImageMSEPerImage(start, end, srcImages, targetImage);
    }

    /**
     * FUNCTION OPTIMIZED FOR CALCULATING MSE FOR MULTIPLE IMAGES (LIST OF IMAGES)
     */
    public void calculateImageMSEPerImage(int start, int end, ArrayList<Image> srcImages, BufferedImage targetImage) {
        for (int k = start; k < end; k++) {
            Image img = srcImages.get(k);
            float diff = 0.0f;
            for (int i = 0; i < img.image.getHeight(); i++) {
                for (int j = 0; j < img.image.getWidth(); j++) {
                    Color srcPixelColor = new Color(img.image.getRGB(j, i));
                    int srcPixelR = srcPixelColor.getRed();
                    int srcPixelG = srcPixelColor.getGreen();
                    int srcPixelB = srcPixelColor.getBlue();

                    Color targetPixelColor = new Color(targetImage.getRGB(j, i));
                    int targetPixelR = targetPixelColor.getRed();
                    int targetPixelG = targetPixelColor.getGreen();
                    int targetPixelB = targetPixelColor.getBlue();

                    int diffR = 255 - Math.abs(srcPixelR - targetPixelR);
                    int diffG = 255 - Math.abs(srcPixelG - targetPixelG);
                    int diffB = 255 - Math.abs(srcPixelB - targetPixelB);

                    int diffR_sq = (int) Math.pow(diffR, 2d);
                    int diffG_sq = (int) Math.pow(diffG, 2d);
                    int diffB_sq = (int) Math.pow(diffB, 2d);

                    diff += (diffR_sq + diffG_sq + diffB_sq);
                }
            }
            diff = diff / (3 * img.image.getHeight() * img.image.getWidth());
            img.fitness = diff;
        }
    }

    /**
     * FUNCTION OPTIMIZED FOR CALCULATING MSE FOR SINGLE IMAGE AT A TIME
     */
    public void calculateImageMSE(int start, int end, BufferedImage srcImage, BufferedImage targetImage, AtomicLong result, boolean last) {
        if (srcImage.getHeight() != targetImage.getHeight() | srcImage.getWidth() != targetImage.getWidth()) {
            throw new Error("Images should be equal in size");
        }
        long chunkTmp = 0;
        for (int i = start; i < end; i++) {
            for (int j = 0; j < srcImage.getWidth(); j++) {
                Color srcPixelColor = new Color(srcImage.getRGB(j, i));
                int srcPixelR = srcPixelColor.getRed();
                int srcPixelG = srcPixelColor.getGreen();
                int srcPixelB = srcPixelColor.getBlue();

                Color targetPixelColor = new Color(targetImage.getRGB(j, i));
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
        //System.out.println("Result before update: " + result.longValue());
        /*if (last) {
            result.getAndUpdate(devide);
            System.out.println("Out of function result: " + result.longValue());
        }*/
    }

    private final LongUnaryOperator devide = a -> a / (3L * srcImage.getWidth() * srcImage.getHeight());

}
