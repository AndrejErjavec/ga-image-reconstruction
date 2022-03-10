import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Population {
    public int size;
    public int pixel_size;
    public ArrayList<Pixel[]> population;
    public float mutationRate;
    public BufferedImage target_image;

    private int image_width;
    private int image_height;
    public int total_pixels;

    ColorUtils cu = new ColorUtils();

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
            Pixel[] image = new Pixel[total_pixels];
            for (int j = 0; j < total_pixels; j++) {
                int pos_x = (int)(Math.random() * image_width);
                int pos_y = (int)(Math.random() * image_height);
                int gray_shade = (int)(Math.random() * 255);
                Color color = new Color(gray_shade, gray_shade, gray_shade);
                image[j] = new Pixel(pos_x, pos_y, color);
            }
            population.add(image);
        }
    }

    public int[] calculateFitness() {
        int[] fitness = new int[size];
        for (int i = 0; i < population.size(); i++) {
            int total_diff = 0;
            Pixel[] image = population.get(i);
            for (int j = 0; j < total_pixels; j++) {
                total_diff += cu.colorDiff(image[j], pixel_size, target_image);
            }
            fitness[i] = total_diff;
        }
        return fitness;
    }

    public int bestFitness() {
        int index = 0;
        long currentBest = Integer.MAX_VALUE;
        int[] fitnessArray = calculateFitness();
        for (int i = 0; i < fitnessArray.length; i++) {
            if (fitnessArray[i] < currentBest) {
                currentBest = fitnessArray[i];
                index = i;
            }
        }
        return index;
    }

    public Pixel[] bestImage() {
        int bestImageIndex = bestFitness();
        Pixel[] best = population.get(bestImageIndex);
        return best;
    }


    public void draw(Graphics g) {
        Pixel[] pixels = bestImage();
        for (int i = 0; i < pixels.length; i++) {
            int pos_x = pixels[i].pos_x;
            int pos_y = pixels[i].pos_y;
            g.setColor(pixels[i].color);
            g.fillRect(pos_x, pos_y, pixel_size, pixel_size);
            // System.out.println("(" + pixels[i].pos_x + ", " + pixels[i].pos_y + ", " + pixels[i].color + ")");
        }
    }
}
