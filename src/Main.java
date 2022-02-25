import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        int POPULATION_SIZE = 1000;
        float MUTATION_RATE = 0.1f;

        int WIDTH = 800;
        int HEIGHT = 600;

        Population pop = new Population(POPULATION_SIZE, MUTATION_RATE);
        Canvas canvas = new Canvas();
        canvas.setSize(WIDTH, HEIGHT);

        pop.initialize();
        //pop.draw();

    }
}
