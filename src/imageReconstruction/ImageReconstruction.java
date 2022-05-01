package imageReconstruction;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class ImageReconstruction {
        private final int population_size;
        private final int image_fragments;
        private final float mutation_rate;
        private final int max_generations;
        public int current_generation;
        private int noImprovementCount;

        private boolean running = false;

        private Display display;
        private Population population;
        private BufferedImage target_image;
        private BufferStrategy bs;
        private Graphics g;

        private float lastBestFitness;

        long startTime = 0;
        long endTime = 0;

        public ImageReconstruction(int population_size, int image_fragments, float mutation_rate, int max_generations, BufferedImage target_image) {
            this.population_size = population_size;
            this.image_fragments = image_fragments;
            this.mutation_rate = mutation_rate;
            this.max_generations = max_generations;
            this.target_image = target_image;
            this.current_generation = 1;
            this.noImprovementCount = 0;
            this.lastBestFitness = 0;
        }

    /**
     * PROGRAM RUNS HERE
     */
    public void run() {
        startTime = System.currentTimeMillis();
        start();
        init();
        while (running && current_generation <= max_generations && noImprovementCount < 200 && current_generation < 501) {
            population.naturalSelection();
            population.generateNewPopulation();
            checkImprovement();
            draw();
            print();
            current_generation++;
        }
        endTime = System.currentTimeMillis();
        // population.exportEndResult();
        endMessage();
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
        display = new Display("Canvas", target_image.getWidth(), target_image.getHeight());
        population = new Population(population_size, image_fragments, mutation_rate, target_image);
        population.initialize();
        running = true;
    }

    public void draw() {
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null) {
            display.getCanvas().createBufferStrategy(3);
            draw();
        }

        g = bs.getDrawGraphics();
        // drawing starts
        g.clearRect(0, 0, target_image.getWidth(), target_image.getHeight());
        population.draw(g);
        // drawing ends
        bs.show();
        g.dispose();
    }

    private void print() {
        System.out.println("Current generation: " + current_generation);
        System.out.println("Best image fitness: " + Math.round(population.bestFittingImage.fitness * 100) / 100d);
        // System.out.println("Max fitness: " + population.maxFitness);
        System.out.println("Average fitness: " + population.averageFitness);
        System.out.println("----------------------------------");
    }

    private void endMessage() {
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
