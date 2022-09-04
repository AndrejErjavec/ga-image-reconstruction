package imageReconstruction;
import mpi.*;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageReconstructionDistributed {
    private static int id;
    private static int size;

    private final int population_size;
    private final int image_fragments;
    private final float mutation_rate;
    private final int max_generations;
    public int current_generation = 1;
    private int noImprovementCount = 0;

    private boolean running = false;

    private Display display;
    private Population population;
    private BufferedImage targetImage;
    private BufferStrategy bs;
    private Graphics g;

    private float lastBestFitness;

    long startTime = 0;
    long endTime = 0;

    public ImageReconstructionDistributed(int population_size, int image_fragments, float mutation_rate, int max_generations, BufferedImage targetImage) {
        this.population_size = population_size;
        this.image_fragments = image_fragments;
        this.mutation_rate = mutation_rate;
        this.max_generations = max_generations;
        this.targetImage = targetImage;
    }

    public void run() {
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
            startTime = System.currentTimeMillis();
            init();
        }

        start();
        while (running && current_generation < max_generations) {
            if (id == 0) {
                srcImagesArray = new int[population.population.size() * population.population.get(0).width * population.population.get(0).height];
                targetImageArray = new int[targetImage.getWidth() * targetImage.getHeight()];
                srcImagesArraySize[0] = srcImagesArray.length;
                targetImageArraySize[0] = targetImageArray.length;
                imageSize[0] = population.population.get(0).width * population.population.get(0).height;

                // fill target images array with RGB values for each pixel on each image
                int ind = 0;
                for (int i = 0; i < targetImage.getHeight(); i++) {
                    for (int j = 0; j < targetImage.getWidth(); j++) {
                        targetImageArray[ind] = targetImage.getRGB(j, i);
                        ind++;
                    }
                }

                // fill src images array with RGB values for each pixel on each image
                int index = 0;
                for (int i = 0; i < population.population.size(); i++) {
                    for (int j = 0; j < population.population.get(0).height; j++) {
                        for (int k = 0; k < population.population.get(0).width; k++) {
                            srcImagesArray[index] = population.population.get(i).image.getRGB(k, j);
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
            imageSize = new int[imageSize[0]];
        }

        // broadcast target image array
        MPI.COMM_WORLD.Bcast(targetImageArray, 0, targetImageArraySize[0], MPI.INT, 0);
        // broadcast single image size
        MPI.COMM_WORLD.Bcast(imageSize, 0, 1, MPI.INT, 0);

        /**
         * prepare data for scatter
         */

        // array with chunk sizes (in images) for each process
        int[] chunkSizes = new int[size];
        int imagesRemaining = srcImagesArraySize[0] / imageSize[0];
        int ci = 0;
        while (imagesRemaining > 0) {
            if (ci == chunkSizes.length) ci = 0;
            chunkSizes[ci] += 1;
            imagesRemaining--;
            ci++;
        }

        // array with chunk sizes (in pixels) for each process
        int[] sendCount = new int[size];
        for (int i = 0; i < sendCount.length; i++) {
            sendCount[i] = chunkSizes[i] * imageSize[0];
        }

        int[] displacements = new int[size];
        displacements[0] = 0;
        for (int i = 1; i < displacements.length; i++) {
            displacements[i] = displacements[i-1] + sendCount[i-1];
        }

        int[] chunk = new int[sendCount[id]];

        /**
         * scatters: srcImageArray
         * to: chunk of each process
         */
        MPI.COMM_WORLD.Scatterv(srcImagesArray, 0, sendCount, displacements, MPI.INT, chunk, 0, chunk.length, MPI.INT, 0);

        int imagesPerChunk = chunkSizes[id]; // = chunk.length / imageSize[0];
        float[] chunkFitnesses = new float[imagesPerChunk];

        /**
         * calculate fitness
         */
        for (int i = 0; i < imagesPerChunk; i++) {
            float diff = 0.0f;
            int index = 0;

            for (int j = 0; j < imageSize[0]; j++) {
                Color srcPixelColor = new Color(chunk[i*imageSize[0] + j]);
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
            diff = diff / (3 * imageSize[0]);
            // fitness of one image
            chunkFitnesses[i] = diff;
        }

        float[] allImagesFitnesses = new float[srcImagesArraySize[0] / imageSize[0]];

        int[] displacementsGather = new int[size];
        displacementsGather[0] = 0;
        for (int i = 1; i < displacementsGather.length; i++) {
            displacementsGather[i] = displacementsGather[i-1] + chunkSizes[i-1];
        }

        /**
         * gathers: chunkFitnesses
         * to: allImageFitnesses
         */
        MPI.COMM_WORLD.Gatherv(chunkFitnesses, 0, chunkFitnesses.length, MPI.FLOAT, allImagesFitnesses, 0, chunkSizes, displacementsGather, MPI.FLOAT, 0);

        // first thread assigns fitness values to all images
        if (id == 0) {
            for (int i = 0; i < allImagesFitnesses.length; i++) {
                population.population.get(i).fitness = allImagesFitnesses[i];
            }

            population.naturalSelection();
            population.generateNewPopulation();

            checkImprovement();
            draw();
            print();
            current_generation++;
        }
        }

        endTime = System.currentTimeMillis();
        if (id == 0) {
            population.exportEndResult();
            printEndMessage();
            System.exit(0);
        }
    }



    /**
     * HELPER FUNCTIONS
     */
    private void checkImprovement() {
        if (this.lastBestFitness == population.bestFittingImage.fitness) {
            this.noImprovementCount ++;
        }
        else if (this.lastBestFitness < population.bestFittingImage.fitness) {
            this.lastBestFitness = population.bestFittingImage.fitness;
            this.noImprovementCount = 0;
        }
    }

    private void start() {
        if (!running) running = true;
    }

    private void init() {
        display = new Display("Canvas", targetImage.getWidth(), targetImage.getHeight());
        population = new Population(population_size, image_fragments, mutation_rate, targetImage);
        population.initialize();
    }

    public void draw() {
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null) {
            display.getCanvas().createBufferStrategy(3);
            draw();
        }

        g = bs.getDrawGraphics();
        // drawing starts
        g.clearRect(0, 0, targetImage.getWidth(), targetImage.getHeight());
        population.draw(g);
        // drawing ends
        bs.show();
        g.dispose();
    }

    private void print() {
        System.out.println("Current generation: " + current_generation);
        // System.out.println("Best image fitness: " + Math.round(population.bestFittingImage.fitness * 100) / 100d);
        // System.out.println("Max fitness: " + population.maxFitness);
        // System.out.println("Average fitness: " + population.averageFitness);
        System.out.println("----------------------------------");
    }

    private void printEndMessage() {
        System.out.println();
        System.out.println("----------------------------------");
        System.out.println("LOCAL MAXIMUM REACHED");
        System.out.println("Evolution finished in generation: " + current_generation);
        System.out.println("Best image fitness: " + population.bestFittingImage.fitness);
        System.out.println("Average fitness: " + population.averageFitness);
        System.out.println();
        long time_taken = endTime - startTime;
        System.out.println("Time taken to complete: " + printTime(time_taken));
        System.out.println("----------------------------------");
    }

    private String printTime(long ms) {
        long seconds = ms / 1000;
        long m = seconds / 60;
        long s = seconds % 60;
        return m + "min " + s + "s";
    }
}
