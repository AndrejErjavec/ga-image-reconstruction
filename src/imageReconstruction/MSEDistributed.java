package imageReconstruction;
import mpi.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MSEDistributed {
    private static int id;
    private static int size;

    public ArrayList<Image> srcImages;
    public BufferedImage targetImage;

    public MSEDistributed(ArrayList<Image> srcImages, BufferedImage targetImage) {
        this.srcImages = srcImages;
        this.targetImage = targetImage;
    }

    public void start() {
        id = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();
        System.out.println("jaz sem "+id+" od "+size);

        // array of values of RGB values for all images inside population
        int[] srcImagesArray = new int[0];
        // array of RGB values for each pixel
        int[] targetImageArray = new int[0];
        // size of array of source images
        int[] srcImagesArraySize = new int[1];
        // size of array of target image
        int[] targetImageArraySize = new int[1];
        // number of pixels in an image
        int[] imageSize = new int[1];

        if (id == 0) {
            // first PC
            srcImagesArray = new int[srcImages.size() * srcImages.get(0).width * srcImages.get(0).height];
            targetImageArray = targetImage.getRGB(0, 0, targetImage.getWidth(), targetImage.getHeight(), null, 0, targetImage.getWidth());
            srcImagesArraySize[0] = srcImagesArray.length;
            targetImageArraySize[0] = targetImageArray.length;
            imageSize[0] = srcImages.get(0).width * srcImages.get(0).height;

            System.out.println("srcImagesArraySize: " + srcImagesArraySize[0]);
            System.out.println("targetImageArraySize: " + targetImageArraySize[0]);

            // fill src images array with RGB values for each pixel on each image
            int index = 0;
            for (int i = 0; i < srcImages.size(); i++) {
                for (int j = 0; j < srcImages.get(0).height; j++) {
                    for (int k = 0; k < srcImages.get(0).width; k++) {
                        srcImagesArray[index] = srcImages.get(i).image.getRGB(j, k);
                        index++;
                    }
                }
            }
        }

        // broadcast target image array size
        MPI.COMM_WORLD.Bcast(targetImageArraySize, 0, 1, MPI.INT, 0);
        // broadcast src images array size
        MPI.COMM_WORLD.Bcast(srcImagesArraySize, 0, 1, MPI.INT, 0);

        if (id != 0) {
            targetImageArray = new int[targetImageArraySize[0]];
            srcImagesArray = new int[srcImagesArraySize[0]];
        }

        // broadcast src images array
        MPI.COMM_WORLD.Bcast(srcImagesArray, 0, srcImagesArraySize[0], MPI.INT, 0);
        // broadcast target image array
        MPI.COMM_WORLD.Bcast(targetImageArray, 0, targetImageArraySize[0], MPI.INT, 0);
        // broadcast image chunk size (number of pixels in an image)
        MPI.COMM_WORLD.Bcast(imageSize, 0, 1, MPI.INT, 0);

        /**
         * DO TUKAJ DELA
         * AVAILABLE VARIABLES:
            * srcImagesArray
            * targetImageArray
            * srcImagesArraySize
            * targetImageArraySize
            * imageSize
         */

        // int chunk = new int[vseh pikslov / pikslov na sliko / size]
        int[] chunk = new int[(srcImagesArraySize[0] / imageSize[0] / size) * imageSize[0]];
        System.out.println("chunk size: " + chunk);
        // MPI.COMM_WORLD.Scatter(targetImageArray, 0, chunk.length, MPI.INT, chunk, 0, chunk.length, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(srcImagesArray, 0, chunk.length, MPI.INT, chunk, 0, chunk.length, MPI.INT,0);
        
        int imagesPerChunk = chunk.length / imageSize[0];

        for (int k = 0; k < imagesPerChunk; k++) {
            // Image img = srcImages.get(k);
            float diff = 0.0f;
            int index = 0;
            for (int i = 0; i < imageSize[0]; i++) {
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
