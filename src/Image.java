import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
    int width;
    int height;
    Pixel[] image;
    float fitness;

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.initialize();
    }

    private void initialize() {
        int total_pixels = width * height;
        this.image = new Pixel[total_pixels];
        for (int j = 0; j < total_pixels; j++) {
            int pos_x = (int) (Math.random() * width);
            int pos_y = (int) (Math.random() * height);
            int gray_shade = (int) (Math.random() * 255);
            Color color = new Color(gray_shade, gray_shade, gray_shade);
            this.image[j] = new Pixel(pos_x, pos_y, color);
        }
    }

    public void calculateFitness(BufferedImage target_image, int pixel_size) {
        ColorUtils cu = new ColorUtils();
        float fitness = 0;
        for (int i = 0; i < image.length; i++) {
            fitness += cu.colorDiff(image[i], pixel_size, target_image);
        }
        this.fitness = 1 - (fitness / (width*height)) / 255;
        // System.out.println("Fitness: " + this.fitness);
    }

    public Image crossover(Image parent2) {
        Image child = new Image(width, height);
        int mid = (int) ((width*height) / 2);

        for (int i = 0; i < child.image.length; i++) {
            if (i <= mid) {
                child.image[i] = parent2.image[i];
            }
            else {
                child.image[i] = this.image[i];
            }
        }
        return child;
    }

    public void mutate(float mutation_rate) {
        for (int i = 0; i < image.length; i++) {
            if (Math.random() < mutation_rate) {
                image[i] = new Pixel(width, height);
            }
        }
    }

    public void draw(Graphics g, int pixel_size) {
        for (int i = 0; i < image.length; i++) {
            int pos_x = image[i].pos_x;
            int pos_y = image[i].pos_y;
            g.setColor(image[i].color);
            g.fillRect(pos_x, pos_y, pixel_size, pixel_size);
            // System.out.println("(" + pixels[i].pos_x + ", " + pixels[i].pos_y + ", " + pixels[i].color + ")");
        }
    }
}
