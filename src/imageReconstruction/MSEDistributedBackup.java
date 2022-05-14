package imageReconstruction;
import mpi.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

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

            // System.out.println("srcImagesArraySize: " + srcImagesArraySize[0]);
            // System.out.println("targetImageArraySize: " + targetImageArraySize[0]);

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
        // broadcast single image size
        MPI.COMM_WORLD.Bcast(imageSize, 0, 1, MPI.INT, 0);

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
         *
         * (defined below)
         * chunkSize - chunk size in pixels
         * imagesPerChunk
         */

        int[] chunkSize = new int[srcImagesArraySize[0] / size];

        int k = chunkSize.length / imageSize[0];
        int rem = chunkSize.length % imageSize[0];
        if (chunkSize.length % imageSize[0] != 0) {
            chunkSize = new int[k * imageSize[0]];
        }

        int[] imagesPerChunk = new int[chunkSize.length / imageSize[0]];

        int[] displacements = new int[size];
        int[] sendCount = new int[size];
        for (int i = 0; i < sendCount.length ; i++) {
            if (i == sendCount.length - 1) {
                sendCount[i] = chunkSize.length + rem;
            }
            else {
                sendCount[i] = chunkSize.length;
            }
        }
        System.out.println("Send count: " + Arrays.toString(sendCount));

        // MPI.COMM_WORLD.Scatter(srcImagesArray, 0, chunkSize.length, MPI.INT, chunkSize, 0, chunkSize.length, MPI.INT,0);
        MPI.COMM_WORLD.Scatterv(srcImagesArray, 0, sendCount, displacements, MPI.INT, chunkSize, 0, chunkSize.length, MPI.INT, 0);

        float[] chunkFitnesses = new float[imagesPerChunk.length];

        for (int i = 0; i < imagesPerChunk.length; i++) {
            float diff = 0.0f;
            int index = 0;
            for (int j = 0; j < imageSize[0]; j++) {
                Color srcPixelColor = new Color(srcImagesArray[i*imageSize[0] + j]);
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
            /**
             * tukaj se konča za eno sliko
             */
            diff = diff / (3 * imageSize[0]);
            System.out.println("Diff: " + diff);
            // fitness of one image
            chunkFitnesses[i] = diff;
        }

        float[] allImagesFitnesses = new float[srcImagesArraySize[0] / imageSize[0]];
        MPI.COMM_WORLD.Gather(chunkFitnesses, 0, imagesPerChunk.length, MPI.FLOAT, allImagesFitnesses, 0, imagesPerChunk.length, MPI.FLOAT, 0);
        //MPI.COMM_WORLD.Gatherv(chunkFitnesses, 0, sendCount.length, MPI.FLOAT, allImagesFitnesses, 0, sendCount, displacements, MPI.FLOAT, 0);

        // first thread assigns fitness values to all images
        if (id == 0) {
            for (int i = 0; i < allImagesFitnesses.length; i++) {
                srcImages.get(i).fitness = allImagesFitnesses[i];
            }
        }
    }

}