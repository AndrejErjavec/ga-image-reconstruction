package imageReconstruction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class FitnessFunctionTest {
    public static void main(String[] args) {
        BufferedImage srcImage = null;
        BufferedImage targetImage = null;
        try {
            srcImage = ImageIO.read(new File("images/result.png"));
            targetImage = ImageIO.read(new File("images/monalisa.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**------------
         * SEQUENTIAL
         --------------*/

        long startTime = System.currentTimeMillis();
        MSESingle mseSingle = new MSESingle();
        float resultSequential = mseSingle.ImageMSE(srcImage, targetImage);
        long endTime = System.currentTimeMillis();

        System.out.println("SEQUENTIAL");
        System.out.println("Result: " + resultSequential);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        System.out.println("------------------------");


        /**---------
         * PARALLEL
         -----------*/

        AtomicLong resultParallel = new AtomicLong(0);

        startTime = System.currentTimeMillis();
        int threads = Runtime.getRuntime().availableProcessors();
        int chunkHeight = srcImage.getHeight() / threads;

        MSEWorker[] workers = new MSEWorker[threads];
        for (int i = 0; i < threads; i++) {
            if (i == threads - 1) {
                workers[i] = new MSEWorker(i, i*chunkHeight, srcImage.getHeight(), srcImage, targetImage, resultParallel, true);
            }
            else {
                workers[i] = new MSEWorker(i, i*chunkHeight, i*chunkHeight + chunkHeight, srcImage, targetImage, resultParallel, false);
            }
            workers[i].start();
        }

        for (int i = 0; i < threads; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        endTime = System.currentTimeMillis();

        System.out.println("PARALLEL");
        System.out.println("Result: " + resultParallel);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }
}
