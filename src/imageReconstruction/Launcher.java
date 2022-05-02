package imageReconstruction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        String IMAGE_PATH = new File("images/monalisa.gif").getAbsolutePath();
        int POPULATION_SIZE = 200;
        int IMAGE_FRAGMENTS = 500;
        float MUTATION_RATE = 0.02f;
        int MAX_GENERATIONS = 10000;
        boolean useAlphaColors = false;
        RunMode runMode = RunMode.PARALLEL;

        Config conf = new Config();
        conf.setConfig(runMode, useAlphaColors);

        BufferedImage TARGET_IMAGE = null;
        try {
            TARGET_IMAGE =  ImageIO.read(new File(IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageReconstruction ir = new ImageReconstruction(POPULATION_SIZE, IMAGE_FRAGMENTS, MUTATION_RATE, MAX_GENERATIONS, TARGET_IMAGE);
        ir.run();
    }
}