import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageReconstruction {
        private int population_size;
        private float mutation_rate;
        private int pixel_size;
        private int max_generations;
        public int current_generation;

        private boolean running = false;

        private Display display;
        private Population population;
        private BufferedImage target_image;
        private BufferStrategy bs;
        private Graphics g;

        public ImageReconstruction(int population_size, float mutation_rate, int max_generations, int pixel_size, BufferedImage target_image) {
            this.population_size = population_size;
            this.mutation_rate = mutation_rate;
            this.max_generations = max_generations;
            this.pixel_size = pixel_size;
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
            population = new Population(population_size, mutation_rate, pixel_size, target_image);
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
            //g.dispose();
        }

        public void print() {
            System.out.println("Current generation: " + current_generation);
            System.out.println("Best fitness: " + Math.round(population.bestFittingImage.fitness * 100) / 100d);
            System.out.println("Average fitness: " + population.averageFitness);
            System.out.println("--------------------------------------------");
        }

}
