package imageReconstruction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class FitnessFunctionTest {
    public static void main(String[] args) {
        // BufferedImage srcImage = null;
        BufferedImage targetImage = null;
        try {
            // srcImage = ImageIO.read(new File("images/result.png"));
            targetImage = ImageIO.read(new File("images/monalisa.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Image> images = new ArrayList<Image>();
        ArrayList<Image> pool = new ArrayList<Image>();

        for (int i = 0; i < 1000; i++) {
            Image img = new Image(128, 128, 100);
            images.add(img);
        }

        /**------------
         * SEQUENTIAL
         --------------*/

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < images.size(); i++) {
            MSESingle mseSingle = new MSESingle();
            float resultSequential = mseSingle.ImageMSE(images.get(i).image, targetImage);
            images.get(i).fitness = resultSequential;
        }
        long endTime = System.currentTimeMillis();

        System.out.println("SEQUENTIAL");
        // System.out.println("Result: " + resultSequential);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        System.out.println("------------------------");

        /**---------
         * PARALLEL
         -----------*/

        startTime = System.currentTimeMillis();

        int threads = Runtime.getRuntime().availableProcessors();
        int chunkHeight = images.size() / threads;

        MSEWorker[] workers = new MSEWorker[threads];
        for (int i = 0; i < threads; i++) {
            if (i == threads - 1) {
                // workers[i] = new MSEWorker(i, i*chunkHeight, img.image.getHeight(), img.image, targetImage, resultParallel, true);
                workers[i] = new MSEWorker(i*chunkHeight, images.size(), images, targetImage);
            }
            else {
                // workers[i] = new MSEWorker(i, i*chunkHeight, i*chunkHeight + chunkHeight, img.image, targetImage, resultParallel, false);
                workers[i] = new MSEWorker(i*chunkHeight, i*chunkHeight + chunkHeight, images, targetImage);
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
        //System.out.println("Result: " + resultParallel);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }
}
