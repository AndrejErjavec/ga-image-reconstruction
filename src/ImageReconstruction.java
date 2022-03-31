import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageReconstruction {
        private final int population_size;
        private final int image_fragments;
        private final float mutation_rate;
        private final int max_generations;
        public int current_generation;

        private boolean running = false;

        private Display display;
        private Population population;
        private BufferedImage target_image;
        private BufferStrategy bs;
        private Graphics g;

        public ImageReconstruction(int population_size, int image_fragments, float mutation_rate, int max_generations, BufferedImage target_image) {
            this.population_size = population_size;
            this.image_fragments = image_fragments;
            this.mutation_rate = mutation_rate;
            this.max_generations = max_generations;
            this.target_image = target_image;
            this.current_generation = 1;
        }

        public void run() {
            start();
            init();
            while (running && current_generation <= max_generations) {
                population.naturalSelection();
                population.generateNewPopulation();
                draw();
                print();
                current_generation++;
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

        public void print() {
            System.out.println("Current generation: " + current_generation);
            System.out.println("Best fitness: " + Math.round(population.bestFittingImage.fitness * 100) / 100d);
            System.out.println("Average fitness: " + population.averageFitness);
            System.out.println("--------------------------------------------");
        }

}
