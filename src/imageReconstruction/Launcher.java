package imageReconstruction;

import jdk.swing.interop.SwingInterOpUtils;
import mpi.MPI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        String IMAGE_PATH = new File("images/monalisa.gif").getAbsolutePath();
        int POPULATION_SIZE = 200;
        int IMAGE_FRAGMENTS = 200;
        float MUTATION_RATE = 0.02f;
        int MAX_GENERATIONS = 1000;
        boolean useAlphaColors = false;
        RunMode runMode = RunMode.SEQUENTIAL;

        MPI.Init(args);
        int id = MPI.COMM_WORLD.Rank();

        Config conf = new Config();
        conf.setConfig(runMode, useAlphaColors);

        BufferedImage TARGET_IMAGE = null;
        try {
            TARGET_IMAGE = ImageIO.read(new File(IMAGE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (runMode != RunMode.DISTRIBUTED) {
            if (id == 0) {
                ImageReconstruction ir = new ImageReconstruction(POPULATION_SIZE, IMAGE_FRAGMENTS, MUTATION_RATE, MAX_GENERATIONS, TARGET_IMAGE);
                ir.run();
            }
        }
        else {
            ImageReconstructionDistributed ird = new ImageReconstructionDistributed(POPULATION_SIZE, IMAGE_FRAGMENTS, MUTATION_RATE, MAX_GENERATIONS, TARGET_IMAGE);
            ird.run();
        }
    }
}
