import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageReconstruction {
        private int population_size;
        private float mutation_rate;
        private int width, height;

        private Display display;
        private Population population;

        public ImageReconstruction(int width, int height, int population_size, float mutation_rate) {
            this.width = width;
            this.height = height;
            this.population_size = population_size;
            this.mutation_rate = mutation_rate;
            display = new Display("Canvas", width, height);
            population = new Population(population_size, mutation_rate);
        }


}
