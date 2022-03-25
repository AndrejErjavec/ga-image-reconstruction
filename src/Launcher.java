import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        String IMAGE_PATH = new File("images/bw.png").getAbsolutePath();
        int POPULATION_SIZE = 1000;
        float MUTATION_RATE = 0.1f;
        int MAX_GENERATIONS = 10000;
        int PIXEL_SIZE = 1;
        BufferedImage TARGET_IMAGE = null;
        try {
            TARGET_IMAGE =  ImageIO.read(new File(IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageReconstruction ir = new ImageReconstruction(POPULATION_SIZE, MUTATION_RATE, MAX_GENERATIONS, PIXEL_SIZE, TARGET_IMAGE);
        ir.run();
    }
}
