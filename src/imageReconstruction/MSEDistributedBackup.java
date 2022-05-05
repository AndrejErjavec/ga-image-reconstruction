package imageReconstruction;

import mpi.MPI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MSEDistributedBackup {
    private static int id;
    private static int size;

    public ArrayList<Image> srcImages;
    public BufferedImage targetImage;

    public MSEDistributedBackup(ArrayList<Image> srcImages, BufferedImage targetImage) {
        this.srcImages = srcImages;
        this.targetImage = targetImage;
    }

    public void start() {
        id = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();
        System.out.println("jaz sem "+id+" od "+size);

        // size of population (Image list)
        int[] populationSize = new int[1];
        // array of RGB values for each pixel
        int[] targetImageArray = new int[0];

        if (id == 0) {
            // first PC
            populationSize[0] = srcImages.size();
            targetImageArray = targetImage.getRGB(0, 0, targetImage.getWidth(), targetImage.getHeight(), null, 0, targetImage.getWidth());
        }

        // broadcast a message with population size data to all VMs
        MPI.COMM_WORLD.Bcast(populationSize, 0, 1, MPI.INT, 0);


        if (id != 0) {
            targetImageArray = new int[populationSize[0]];
        }

        int[] chunk = new int[populationSize[0] / size];
        MPI.COMM_WORLD.Scatter(targetImageArray, 0, chunk.length, MPI.INT, chunk, 0, chunk.length, MPI.INT, 0);

        for (int k = 0; k < chunk.length; k++) {
            Image img = srcImages.get(k);
            float diff = 0.0f;
            int index = 0;
            for (int i = 0; i < srcImages.get(0).height; i++) {
                for (int j = 0; j < srcImages.get(0).width; j++) {
                    Color srcPixelColor = new Color(img.image.getRGB(i, j));
                    int srcPixelR = srcPixelColor.getRed();
                    int srcPixelG = srcPixelColor.getGreen();
                    int srcPixelB = srcPixelColor.getBlue();

                    Color targetPixelColor = new Color(targetImageArray[index]);
                    int targetPixelR = targetPixelColor.getRed();
                    int targetPixelG = targetPixelColor.getGreen();
                    int targetPixelB = targetPixelColor.getBlue();

                    int diffR = 255 - Math.abs(srcPixelR - targetPixelR);
                    int diffG = 255 - Math.abs(srcPixelG - targetPixelG);
                    int diffB = 255 - Math.abs(srcPixelB - targetPixelB);

                    double diffR_sq = Math.pow(diffR, 2d);
                    double diffG_sq = Math.pow(diffG, 2d);
                    double diffB_sq = Math.pow(diffB, 2d);

                    diff += (diffR_sq + diffG_sq + diffB_sq);
                    index++;
                }
            }
            diff = diff / (3 * img.image.getHeight() * img.image.getWidth());
            img.fitness = diff;
        }
    }
}
