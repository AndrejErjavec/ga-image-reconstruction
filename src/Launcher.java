import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        String IMAGE_PATH = "C:/Users/Andrej/Desktop/48.gif";
        int POPULATION_SIZE = 1000;
        float MUTATION_RATE = 0.1f;
        int WIDTH = 800;
        int HEIGHT = 600;
        int PIXEL_SIZE = 5;
        BufferedImage TARGET_IMAGE = null;
        try {
            TARGET_IMAGE =  ImageIO.read(new File(IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageReconstruction ir = new ImageReconstruction(WIDTH, HEIGHT, POPULATION_SIZE, MUTATION_RATE, PIXEL_SIZE, TARGET_IMAGE);
        ir.init();
        ir.draw();

    }
}
