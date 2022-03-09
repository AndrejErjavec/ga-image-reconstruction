import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.Random;

public class Population {
    int size;
    int pixel_size;
    Pixel[] pixels;
    float mutationRate;
    BufferedImage target_image;
    Canvas canvas;

    ColorUtils cu = new ColorUtils();

    public Population(int size, float mutationRate, Canvas canvas, int pixel_size, BufferedImage target_image) {
        this.size = size;
        this.mutationRate = mutationRate;
        this.canvas = canvas;
        this.pixel_size = pixel_size;
        this.target_image = target_image;
    }


    public void initialize() {
        this.pixels = new Pixel[size];
        for (int i = 0; i < this.size; i++) {
            int image_width = target_image.getWidth();
            int image_height = target_image.getHeight();
            // int image_width = canvas.getWidth();
            // int image_height = canvas.getHeight();
            int pos_x = (int)(Math.random() * image_width);
            int pos_y = (int)(Math.random() * image_height);
            int gray_shade = (int)(Math.random() * 255);
            Color color = new Color(gray_shade, gray_shade, gray_shade);
            pixels[i] = new Pixel(pos_x, pos_y, color);
        }
    }

    public int[] calculateFitness() {
        int[] fitness = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            fitness[i] = cu.colorDiff(pixels[i], pixel_size, target_image);
        }
        return fitness;
    }

    public void draw(Graphics g) {
        for (int i = 0; i < pixels.length; i++) {
            int pos_x = pixels[i].pos_x;
            int pos_y = pixels[i].pos_y;
            g.setColor(pixels[i].color);
            g.fillRect(pos_x, pos_y, pixel_size, pixel_size);
            // System.out.println("(" + pixels[i].pos_x + ", " + pixels[i].pos_y + ", " + pixels[i].color + ")");
        }
    }
}
