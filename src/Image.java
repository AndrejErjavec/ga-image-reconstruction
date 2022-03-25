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
        int k = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // int pos_x = (int) (Math.random() * width);
                // int pos_y = (int) (Math.random() * height);
                int pos_x = j;
                int pos_y = i;
                int gray_shade = 0;
                if (Math.random() > 0.5)  gray_shade = 255;
                Color color = new Color(gray_shade, gray_shade, gray_shade);
                this.image[k] = new Pixel(pos_x, pos_y, color);
                k++;
            }
        }
            // int gray_shade = (int)(Math.random() * 255);
    }

    public void calculateFitness(BufferedImage target_image, int pixel_size) {
        ColorUtils cu = new ColorUtils();
        float fitness = 0;
        for (int i = 0; i < image.length; i++) {
            fitness += cu.isRightColor(image[i], target_image);
            //fitness += cu.colorDiff(image[i], pixel_size, target_image);
        }
        // this.fitness = 1 - (fitness / (width*height)) / 255;
        this.fitness = fitness;
    }

    public Image crossover(Image parent2) {
        Image child = new Image(width, height);
        int mid = (int)(image.length / 2);

        for (int i = 0; i < child.image.length; i++) {
            if (i <= mid) {
                child.image[i] = this.image[i];
            }
            else {
                child.image[i] = parent2.image[i];
            }
        }
        return child;
    }

    public void mutate(float mutation_rate) {
        for (int i = 0; i < image.length; i++) {
            if (Math.random() < mutation_rate) {
                int randomColor = 0;
                if (Math.random() > 0.5) randomColor = 255;
                Color color = new Color(randomColor, randomColor, randomColor);
                image[i] = new Pixel(width, height, color);
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


/*    private void initialize() {
        this.image = new Pixel[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // int pos_x = (int) (Math.random() * width);
                // int pos_y = (int) (Math.random() * height);
                int pos_x = j;
                int pos_y = i;
                int gray_shade = 0;
                if (Math.random() > 0.5)  gray_shade = 255;
                Color color = new Color(gray_shade, gray_shade, gray_shade);
                this.image[pos_x][pos_y] = new Pixel(pos_x, pos_y, color);
            }
        }
        // int gray_shade = (int)(Math.random() * 255);
    }

    public void calculateFitness(BufferedImage target_image, int pixel_size) {
        ColorUtils cu = new ColorUtils();
        float fitness = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                fitness += cu.isRightColor(image[i][j], target_image);
            }
        }
        this.fitness = fitness;
    }

    public Image crossover(Image parent2) {
        Image child = new Image(width, height);
        int mid = (image[0].length / 2);

        for (int i = 0; i < child.image.length; i++) {
            for (int j = 0; j < child.image[0].length; j++) {
                if (j <= mid) {
                    child.image[i][j] = this.image[i][j];
                }
                else {
                    child.image[i][j] = parent2.image[i][j];
                }
            }

        }
        return child;
    }

    public void mutate(float mutation_rate) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (Math.random() < mutation_rate) {
                    int randomColor = 0;
                    if (Math.random() > 0.5) randomColor = 255;
                    Color color = new Color(randomColor, randomColor, randomColor);
                    image[i][j] = new Pixel(width, height, color);
                }
            }
        }
    }

    public void draw(Graphics g, int pixel_size) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pos_x = image[i][j].pos_x;
                int pos_y = image[i][j].pos_y;
                g.setColor(image[i][j].color);
                g.fillRect(pos_x, pos_y, pixel_size, pixel_size);
            }
        }
    }

 */

}
