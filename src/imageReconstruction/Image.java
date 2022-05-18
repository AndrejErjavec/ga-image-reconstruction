package imageReconstruction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import imageReconstruction.Config;

public class Image {
    int width;
    int height;
    int fragment_count;
    TriangleFragment[] fragments;
    BufferedImage image;
    float fitness;
    int fitnessScore;

    public Image(int width, int height, int fragment_count) {
        this.width = width;
        this.height = height;
        this.fragment_count = fragment_count;
        this.initialize();
    }

    public Image(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    private void initialize() {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.fragments = new TriangleFragment[fragment_count];
        this.fitness = 0.0f;
        this.fitnessScore = 0;

        for (int i = 0; i < fragment_count; i++) {
            int p1_pos_x = (int) (Math.random() * width);
            int p1_pos_y = (int) (Math.random() * height);
            int p2_pos_x = (int) (Math.random() * width);
            int p2_pos_y = (int) (Math.random() * height);
            int p3_pos_x = (int) (Math.random() * width);
            int p3_pos_y = (int) (Math.random() * height);

            Point p1 = new Point(p1_pos_x, p1_pos_y);
            Point p2  = new Point(p2_pos_x, p2_pos_y);
            Point p3 = new Point(p3_pos_x, p3_pos_y);

            int gray_shade = (int)(Math.random() * 255);
            Color color = new Color(gray_shade, gray_shade, gray_shade);
            if (Config.useAlphaColors) {
                int alpha = (int)(Math.random() * 100);
                color = new Color(gray_shade, gray_shade, gray_shade, alpha);
            }
            this.fragments[i] = new TriangleFragment(p1, p2, p3, color);
        }
        generateImage();
    }

    public void calculateFitnessSequential(BufferedImage targetImage) {
        MSESingle mse = new MSESingle();
        this.fitness = mse.ImageMSE(this.image, targetImage);
        // System.out.println(this.fitness);
    }

/*    private void calculateFitnessParallel(BufferedImage targetImage) {
        int threads = Config.threads;
        int chunkHeight = height / threads;
        AtomicReference<Float> fitnessResult = new AtomicReference<Float>(0.0f);
        MSEWorker[] workers = new MSEWorker[threads];

        for (int i = 0; i < threads; i++) {
            if (i == threads - 1) {
                workers[i] = new MSEWorker(i*chunkHeight, targetImage.getHeight(), this.image, targetImage, fitnessResult, true);
            }
            else {
                workers[i] = new MSEWorker(i*chunkHeight, i*chunkHeight + chunkHeight, this.image, targetImage, fitnessResult, false);
            }
            workers[i].start();
        }

        for (int i = 0; i < threads; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.fitness = fitnessResult.get() / (3 * image.getWidth() * image.getHeight());
        // System.out.println("Image result: " + this.fitness);
    }

 */

    /**
     * CROSSOVER METHODS:
     * one point crossover
     * multi point crossover
     * uniform crossover
     */
    public Image onePointCrossover(Image parent2) {
        Image child = new Image(width, height, fragment_count);
        double splitPoint = Math.random() * fragments.length;
        for (int i = 0; i < child.fragments.length; i++) {
            if (i <= splitPoint) {child.fragments[i] = this.fragments[i];}
            else {child.fragments[i] = parent2.fragments[i];}
        }
        return child;
    }

    public Image multiPointCrossover(Image parent2) {
        Image child = new Image(width, height, fragment_count);
        return child;
    }

    public Image uniformCrossover(Image parent2) {
        Image child = new Image(width, height, fragment_count);
        for (int i = 0; i < child.fragments.length; i++) {
            double coin = Math.random();
            if (coin <= 0.5) {child.fragments[i] = this.fragments[i];}
            else {child.fragments[i] = parent2.fragments[i];}
        }
        return child;
    }

    public void mutate(float mutation_rate) {
        for (int i = 0; i < fragments.length; i++) {
            double r = Math.random();
            if (r <= 0.5) {
                if (r <= 0.166) {
                    int p1_pos_x = (int) (Math.random() * width);
                    int p1_pos_y = (int) (Math.random() * height);
                    fragments[i].moveP1(p1_pos_x, p1_pos_y);
                } else if (r <= 0.333) {
                    int p2_pos_x = (int) (Math.random() * width);
                    int p2_pos_y = (int) (Math.random() * height);
                    fragments[i].moveP2(p2_pos_x, p2_pos_y);
                } else {
                    int p3_pos_x = (int) (Math.random() * width);
                    int p3_pos_y = (int) (Math.random() * height);
                    fragments[i].moveP3(p3_pos_x, p3_pos_y);
                }
            }
            else {
                int gray_shade = (int)(Math.random() * 255);
                fragments[i].changeColor(new Color(gray_shade, gray_shade, gray_shade));
            }
        }
    }

    public void mutateOriginal(float mutation_rate) {
        for (int i = 0; i < fragments.length; i++) {
            if (Math.random() < mutation_rate) {
                int p1_pos_x = (int) (Math.random() * width);
                int p1_pos_y = (int) (Math.random() * height);
                int p2_pos_x = (int) (Math.random() * width);
                int p2_pos_y = (int) (Math.random() * height);
                int p3_pos_x = (int) (Math.random() * width);
                int p3_pos_y = (int) (Math.random() * height);

                int gray_shade = (int)(Math.random() * 255);
                Color color = new Color(gray_shade, gray_shade, gray_shade);
                if (Config.useAlphaColors) {
                    int alpha = (int) (Math.random() * 100);
                    color = new Color(gray_shade, gray_shade, gray_shade, alpha);
                }

                fragments[i] = new TriangleFragment(new Point(p1_pos_x, p1_pos_y), new Point(p2_pos_x, p2_pos_y), new Point(p3_pos_x, p3_pos_y), color);
            }
        }
    }

    public void generateImage() {
        Graphics2D g = this.image.createGraphics();
        for (int i = 0; i < fragments.length; i++) {
            int p1_x = (int) fragments[i].p1.getX();
            int p1_y = (int) fragments[i].p1.getY();
            int p2_x = (int) fragments[i].p2.getX();
            int p2_y = (int) fragments[i].p2.getY();
            int p3_x = (int) fragments[i].p3.getX();
            int p3_y = (int) fragments[i].p3.getY();
            g.setColor(fragments[i].color);
            g.fillPolygon(new int[] {p1_x, p2_x, p3_x}, new int[] {p1_y, p2_y, p3_y}, 3);
        }
        g.dispose();
    }

    public void draw(Graphics g) {
        g.drawImage(this.image, 0, 0, null);
    }

    public void export() {
        try {
            File imageExport = new File("image.png");
            ImageIO.write(this.image, "png", imageExport);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
