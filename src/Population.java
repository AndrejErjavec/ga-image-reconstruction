import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Population {
    int size;
    Pixel[] pixels;
    float mutationRate;
    BufferedImage target_image;

    public Population(int size, float mutationRate, BufferedImage target_image) {
        this.size = size;
        this.pixels = new Pixel[size];
        this.mutationRate = mutationRate;
        this.target_image = target_image;
    }

    public Population(int size, float mutationRate) {
        this.size = size;
        this.pixels = new Pixel[size];
        this.mutationRate = mutationRate;
    }


    public void initialize() {
        for (int i = 0; i < this.size; i++) {
            int image_width = target_image.getWidth();
            int image_height = target_image.getHeight();
            int pos_x = (int)(Math.random() * image_width);
            int pos_y = (int)(Math.random() * image_height);
            int gray_shade = (int)(Math.random() * 255);
            Color color = new Color(gray_shade, gray_shade, gray_shade);
            pixels[i] = new Pixel(pos_x, pos_y, color);
        }
    }

    public void draw(Canvas g) {
        for (int i = 0; i < this.size; i++) {
            int pos_x = pixels[i].pos_x;
            int pos_y = pixels[i].pos_y;
            //g.paint();
        }
    }
}
