import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Population {
    public int size;
    public int pixel_size;
    public ArrayList<Image> population;
    public float mutationRate;
    public BufferedImage target_image;
    public ArrayList<Image> matingPool;
    public float bestFitness;

    private int image_width;
    private int image_height;
    public int total_pixels;


    public Population(int size, float mutationRate, int pixel_size, BufferedImage target_image) {
        this.size = size;
        this.mutationRate = mutationRate;
        this.pixel_size = pixel_size;
        this.target_image = target_image;
        this.population = new ArrayList<>();

        this.image_width = target_image.getWidth();
        this.image_height = target_image.getHeight();
        this.total_pixels = (image_width * image_height) / pixel_size;
    }

    public void initialize() {
        for (int i = 0; i < this.size; i++) {
            Image image = new Image(image_width, image_height);
            image.calculateFitness(target_image, pixel_size);
            population.add(image);
        }
    }

    public void naturalSelection() {
        // System.out.println("Performing natural selection...");
        this.matingPool = new ArrayList<>();

        for (int i = 0; i < population.size(); i++) {
            int n = (int) (population.get(i).fitness * 100);
            for (int j = 0; j < n; j++) {
                matingPool.add(population.get(i));
            }
        }
    }

    public void generateNewPopulation() {
        // System.out.println("Generating new population...");
        for (int i = 0; i < population.size(); i++) {
            int a = (int)(Math.random() * matingPool.size());
            int b = (int)(Math.random() * matingPool.size());

            Image parentA = matingPool.get(a);
            Image parentB = matingPool.get(b);

            Image child = parentA.crossover(parentB);
            child.mutate(mutationRate);
            this.population.set(i, child);
        }
    }

    public Image getBestFittingImage() {
        // System.out.println("Getting best fitting image...");
        float bestFitness = 0;
        int best = 0;
        for (int i = 0; i < population.size(); i++) {
            if (population.get(i).fitness > bestFitness) {
                bestFitness = population.get(i).fitness;
                best = i;
            }
        }
        this.bestFitness = bestFitness;
        return population.get(best);
    }

    public void draw(Graphics g) {
        Image image = getBestFittingImage();
        // System.out.println("Drawing best image...");
        image.draw(g, pixel_size);
    }
}
