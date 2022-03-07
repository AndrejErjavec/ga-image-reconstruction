import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class ImageReconstruction {
        private int population_size;
        private float mutation_rate;
        private int width, height;

        private Display display;
        private Population population;
        private BufferStrategy bs;
        private Graphics g;

        public ImageReconstruction(int width, int height, int population_size, float mutation_rate) {
            this.width = width;
            this.height = height;
            this.population_size = population_size;
            this.mutation_rate = mutation_rate;
        }

        public void init() {
            display = new Display("Canvas", width, height);
            population = new Population(population_size, mutation_rate, display.getCanvas());
            population.initialize();
            // display.getCanvas().createBufferStrategy(3);
            // bs = display.getCanvas().getBufferStrategy();
            // g = bs.getDrawGraphics();
            g = display.getCanvas().getGraphics();
        }

        public void draw() {
            population.draw(g);
            bs.show();
            g.dispose();
        }

}
