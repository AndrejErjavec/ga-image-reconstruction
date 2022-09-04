package imageReconstruction;

import mpi.MPI;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FitnessFunctionTest {
    private static int id;
    private static ArrayList<Image> images = new ArrayList<Image>();
    private static BufferedImage targetImage;
    private static final int size = 5;
    public static void main(String[] args) {
        MPI.Init(args);

        id = MPI.COMM_WORLD.Rank();

        if (id == 0) {
            // BufferedImage srcImage = null;
            try {
                // srcImage = ImageIO.read(new File("images/result.png"));
                targetImage = ImageIO.read(new File("images/monalisa.gif"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < size; i++) {
                BufferedImage img = null;
                try {
                    img = ImageIO.read(new File("testImages/image" + i + ".png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                images.add(new Image(img));
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
            System.out.println("Time taken: " + (endTime - startTime) + "ms");
            printFitnesses(images);
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
            System.out.println("Time taken: " + (endTime - startTime) + "ms");
            printFitnesses(images);
            System.out.println("------------------------");
        }
    }

    private static void printFitnesses(ArrayList<Image> images) {
        images.forEach(image -> System.out.println("Fitness: " + image.fitness));
    }

}
