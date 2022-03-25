import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Population {
    public int size;
    public int pixel_size;
    public ArrayList<Image> population;
    public float mutationRate;
    public BufferedImage target_image;
    public ArrayList<Image> matingPool;
    public float bestFitness;
    public Image bestFittingImage;
    public float averageFitness;

    private int image_width;
    private int image_height;
    public int total_pixels;


    public Population(int size, float mutationRate, int pixel_size, BufferedImage target_image) {
        this.size = size;
        this.mutationRate = mutationRate;
        this.pixel_size = pixel_size;
        this.target_image = target_image;
        this.population = new ArrayList<>();
        this.matingPool = new ArrayList<>();
        this.bestFitness = 0.0f;
        this.averageFitness = 0.0f;

        this.image_width = target_image.getWidth();
        this.image_height = target_image.getHeight();
        this.total_pixels = (image_width * image_height) / pixel_size;
    }

    public void initialize() {
        for (int i = 0; i < this.size; i++) {
            Image image = new Image(image_width-1, image_height-1);
            population.add(image);
        }
    }

    public void naturalSelection() {
        // System.out.println("Performing natural selection...");
        matingPool.clear();
        for (int i = 0; i < population.size(); i++) {
            population.get(i).calculateFitness(target_image, pixel_size);
            int n = (int) (population.get(i).fitness /* *100 */ );
            /*for (int j = 0; j < n; j++) {
                matingPool.add(population.get(i));
            }*/
            matingPool.add(population.get(i));
        }

        getBestFittingImage();
        getAverageFitness();
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

    public void getBestFittingImage() {
        // System.out.println("Getting best fitting image...");
        for (int i = 0; i < population.size(); i++) {
            Image img  = population.get(i);
            if (img.fitness > this.bestFitness) {
                this.bestFitness = img.fitness;
                this.bestFittingImage = img;
            }
        }
    }

    public void getAverageFitness() {
        float total = 0.0f;
        for (int i = 0; i < population.size(); i++) {
            total += population.get(i).fitness;
        }
        //System.out.println(total);
        this.averageFitness = (total / population.size());
    }
    public void sortMatingPool() {
        Collections.sort(matingPool, new Comparator<Image>() {

            public int compare(Image o1, Image o2) {
                return Float.valueOf(o1.fitness).compareTo(Float.valueOf(o2.fitness));
            }
        });
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing best image...");
        Image image = bestFittingImage;
        image.draw(g, pixel_size);
    }
}
