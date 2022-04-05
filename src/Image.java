import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
    int width;
    int height;
    int fragment_count;
    TriangleFragment[] fragments;
    BufferedImage image;
    float fitness;

    public Image(int width, int height, int fragment_count) {
        this.width = width;
        this.height = height;
        this.fragment_count = fragment_count;
        this.initialize();
    }

    private void initialize() {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.fragments = new TriangleFragment[fragment_count];

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

            this.fragments[i] = new TriangleFragment(p1, p2, p3, color);
        }
        generateImage();
    }

    public void calculateFitness(BufferedImage target_image) {
        ImageUtils imageUtils = new ImageUtils();
        this.fitness =  - imageUtils.ImageMSE(this.image, target_image);
    }

    public Image crossover(Image parent2) {
        Image child = new Image(width, height, fragment_count);
        int mid = (fragments.length / 2);

        for (int i = 0; i < child.fragments.length; i++) {
            if (i <= mid) {
                child.fragments[i] = this.fragments[i];
            }
            else {
                child.fragments[i] = parent2.fragments[i];
            }
        }
        return child;
    }

    public void mutate(float mutation_rate) {
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

}
