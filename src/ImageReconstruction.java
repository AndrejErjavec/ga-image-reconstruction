import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageReconstruction {
        private int population_size;
        private float mutation_rate;
        private int width, height;
        private int pixel_size;

        private Display display;
        private Population population;
        private BufferedImage target_image;
        private Graphics g;

        public ImageReconstruction(int width, int height, int population_size, float mutation_rate, int pixel_size, BufferedImage target_image) {
            this.width = width;
            this.height = height;
            this.population_size = population_size;
            this.mutation_rate = mutation_rate;
            this.pixel_size = pixel_size;
            this.target_image = target_image;
        }

        public void init() {
            display = new Display("Canvas", target_image.getWidth(), target_image.getHeight());
            population = new Population(population_size, mutation_rate, display.getCanvas(), pixel_size, target_image);
            population.initialize();
            g = display.getCanvas().getGraphics();
        }

        public void draw() {
            population.draw(g);
            g.dispose();
            // System.out.println(Arrays.toString(population.calculateFitness()));
        }

}
